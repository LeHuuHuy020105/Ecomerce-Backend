package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.controller.request.ProductPackage.ProductPackage;
import backend_for_react.backend_for_react.controller.request.ReturnOrder.ReturnOrderCreationRequest;
import backend_for_react.backend_for_react.controller.request.Shipping.FeeRequest;
import backend_for_react.backend_for_react.controller.request.Shipping.ShippingOrderRequest;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.ProductBaseResponse;
import backend_for_react.backend_for_react.controller.response.ReturnOrderItemResponse;
import backend_for_react.backend_for_react.controller.response.ReturnOrderResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.OrderMapper;
import backend_for_react.backend_for_react.mapper.UserMapper;
import backend_for_react.backend_for_react.model.*;
import backend_for_react.backend_for_react.repository.*;
import backend_for_react.backend_for_react.service.impl.OrderService;
import backend_for_react.backend_for_react.service.impl.UserService;
import backend_for_react.backend_for_react.state.ReturnOrder.ReturnOrderContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static backend_for_react.backend_for_react.common.utils.ShippingHelper.*;
import static backend_for_react.backend_for_react.mapper.OrderMapper.getOrderItemResponse;

@Slf4j(topic = "RETURN-ORDER-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnOrderService {

    ReturnOrderRepository returnOrderRepository;
    OrderRepository orderRepository;
    ReturnOrderItemRepository returnOrderItemRepository;
    OrderItemRepository orderItemRepository;
    GhnService ghnService;
    SecurityUtils securityUtils;
    ProductVariantRepository productVariantRepository;
    ImageReturnOrderRepository imageReturnOrderRepository;
    FireBaseService fireBaseService;


    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_ALL_RETURN_ORDER')")
    public PageResponse<ReturnOrderResponse> findAll(String sort, int page, int size) {
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = Sort.by(Sort.Direction.ASC, columnName);
                } else {
                    order = Sort.by(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<ReturnOrder> returnOrders = null;
        returnOrders = returnOrderRepository.findAll(pageable);
        PageResponse response = getProductPageResponse(pageNo, size, returnOrders);
        return response;
    }

    public PageResponse<ReturnOrderResponse> findAllForMe(String sort, int page, int size) {
        User user = securityUtils.getCurrentUser();
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = Sort.by(Sort.Direction.ASC, columnName);
                } else {
                    order = Sort.by(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<ReturnOrder> returnOrders = null;
        returnOrders = returnOrderRepository.findAllByUser(user,pageable);
        PageResponse response = getProductPageResponse(pageNo, size, returnOrders);
        return response;
    }


    @Transactional(rollbackFor = Exception.class)
    public ReturnOrder save(ReturnOrderCreationRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        // Kiểm tra đơn hàng tồn tại & thuộc user
        Order order = orderRepository.findOrderByIdAndUser(request.getOrderId(), currentUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "Order not found or not yours"));

        // Kiểm tra trạng thái đơn hàng

        if(order.isConfirmed()){
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Order is already confirm . You cannot return order");
        }

        if (!order.getOrderStatus().equals(DeliveryStatus.COMPLETED)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Order status must be COMPLETED");
        }


        // Kiểm tra thời gian được phép hoàn hàng (7 ngày)
        LocalDateTime deliveredAt = order.getDeliveredAt();
        if (deliveredAt == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Delivered date not found");
        }

        long daysSinceDelivered = ChronoUnit.DAYS.between(deliveredAt, LocalDateTime.now());
        if (daysSinceDelivered > 7) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Return period has expired (only within 7 days after delivery)");
        }

        // Tạo ReturnOrder
        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setOrder(order);
        returnOrder.setUser(currentUser);
        returnOrder.setStatus(ReturnStatus.REQUESTED);
        returnOrder.setReason(request.getReason());
        returnOrder.setRequestedAt(LocalDateTime.now());

        BigDecimal refundAmount = BigDecimal.ZERO;
        List<ReturnOrderItem> returnItems = new ArrayList<>();

        // Xử lý từng item trong request
        for (ReturnOrderCreationRequest.ReturnItemRequest itemRequest : request.getItems()) {
            OrderItem orderItem = orderItemRepository.findById(itemRequest.getOrderItemId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "Order item not found"));
            if(!orderItem.getOrder().equals(order)){
                throw new BusinessException(ErrorCode.BAD_REQUEST,"Order item is not part of the order");
            }
            int availableToReturn = orderItem.getQuantity() - orderItem.getReturnedQuantity();
            if (availableToReturn <= 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Item already fully returned");
            }

            if (itemRequest.getQuantity() > availableToReturn) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Return quantity exceeds available quantity");
            }

            // Tạo ReturnOrderItem
            ReturnOrderItem returnItem = new ReturnOrderItem();
            returnItem.setReturnOrder(returnOrder);
            returnItem.setOrderItem(orderItem);
            returnItem.setQuantity(itemRequest.getQuantity());

            BigDecimal itemRefund = orderItem.getFinalPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            returnItem.setItemRefundAmount(itemRefund);

            refundAmount = refundAmount.add(itemRefund);
            returnItems.add(returnItem);

            // Cập nhật số lượng đã hoàn
            orderItem.setReturnedQuantity(orderItem.getReturnedQuantity() + itemRequest.getQuantity());
            orderItemRepository.save(orderItem);
        }

        // Lưu ảnh minh chứng
        List<ImageReturnOrder> imageReturnOrders = new ArrayList<>();
        if (request.getImageReturnOrder() != null && !request.getImageReturnOrder().isEmpty()) {
            for (String url : request.getImageReturnOrder()) {
                ImageReturnOrder image = new ImageReturnOrder();
                image.setReturnOrder(returnOrder);
                image.setUrl(url);
                image.setStatus(Status.ACTIVE);
                imageReturnOrders.add(image);
            }
        }

        // Tính toán kích thước / trọng lượng
        List<OrderItem> relatedOrderItems = returnItems.stream()
                .map(ReturnOrderItem::getOrderItem)
                .toList();
        List<ProductPackage> packages = relatedOrderItems.stream()
                .map(item -> new ProductPackage(
                        item.getNameProductSnapShot(),
                        item.getProductVariant().getLength(),
                        item.getProductVariant().getWidth(),
                        item.getProductVariant().getHeight(),
                        item.getProductVariant().getWeight(),
                        item.getQuantity()
                ))
                .toList();
        int height = calculateAverageHeight(packages);
        int width = calculateAverageWidth(packages);
        int length = calculateAverageLength(packages);
        int weight = calculateTotalWeight(packages);
        returnOrder.setTotalWeight(weight);
        returnOrder.setTotalHeight(height);
        returnOrder.setTotalWidth(width);
        returnOrder.setTotalLength(length);
        returnOrder.setServiceTyeId(determineServiceTypeId(weight,length,width,height));
        returnOrder.setRefundAmount(refundAmount);

        // Lưu ReturnOrder
        returnOrderRepository.save(returnOrder);
        returnOrderItemRepository.saveAll(returnItems);
        if (!imageReturnOrders.isEmpty()) imageReturnOrderRepository.saveAll(imageReturnOrders);

        fireBaseService.updateStatusReturnOrder(returnOrder);

        log.info("ReturnOrder created: id={}, refundAmount={}", returnOrder.getId(), refundAmount);
        return returnOrder;
    }


    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CHANGE_STATUS_RETURN_ORDER')")
    public void changeStatus(Long returnOrderId, ReturnStatus newStatus) {
        ReturnOrder returnOrder = returnOrderRepository.findById(returnOrderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "Return order not found"));

        ReturnOrderContext context = new ReturnOrderContext(returnOrder,productVariantRepository,ghnService);
        context.changeState(newStatus);

        returnOrderRepository.save(returnOrder);

        fireBaseService.updateStatusReturnOrder(returnOrder);
    }


    @Transactional(rollbackFor = Exception.class)
    public void cancelReturnOrder(Long returnOrderId) {
        User currentUser = securityUtils.getCurrentUser();
        ReturnOrder returnOrder = returnOrderRepository.findByIdAndUser(returnOrderId,currentUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "Return order not found or not yours"));
        if(!returnOrder.getStatus().equals(ReturnStatus.REQUESTED)){
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Return order status invalid to cancel");
        }
        returnOrder.setStatus(ReturnStatus.CANCEL);
        returnOrderRepository.save(returnOrder);

        fireBaseService.updateStatusReturnOrder(returnOrder);
    }

    private PageResponse<ReturnOrderResponse> getProductPageResponse(int page, int size, Page<ReturnOrder> returnOrders) {
        log.info("returnOrder : {}" ,returnOrders);
        List<ReturnOrderResponse> returnOrderResponseList = returnOrders.stream()
                .map(returnOrder -> getReturnOrderBaseResponse(returnOrder))
                .toList();
        PageResponse<ReturnOrderResponse> response = new PageResponse<>();
        response.setPageNumber(page + 1);
        response.setPageSize(size);
        response.setTotalElements(returnOrders.getTotalElements());
        response.setTotalPages(returnOrders.getTotalPages());
        response.setData(returnOrderResponseList);
        return response;
    }
    private ReturnOrderResponse getReturnOrderBaseResponse(ReturnOrder returnOrder) {
        List<ReturnOrderResponse.ImageReturnOrderResponse> imageReturnOrderResponses = returnOrder.getImageReturnOrders().stream()
                .map(imageReturnOrder -> ReturnOrderResponse.ImageReturnOrderResponse.builder()
                        .id(imageReturnOrder.getId())
                        .imageUrl(imageReturnOrder.getUrl())
                        .status(imageReturnOrder.getStatus())
                        .build())
                .toList();
        List<ReturnOrderItemResponse> returnOrderItemResponses = returnOrder.getReturnOrderItems().stream()
                .map(returnOrderItem -> ReturnOrderItemResponse.builder()
                        .id(returnOrderItem.getId())
                        .quantity(returnOrderItem.getQuantity())
                        .orderItemResponse(getOrderItemResponse(returnOrderItem.getOrderItem()))
                        .itemRefundAmount(returnOrderItem.getItemRefundAmount())
                        .build())
                .toList();
        return ReturnOrderResponse.builder()
                .id(returnOrder.getId())
                .totalHeight(returnOrder.getTotalHeight())
                .totalWeight(returnOrder.getTotalWeight())
                .totalLength(returnOrder.getTotalLength())
                .totalWidth(returnOrder.getTotalWidth())
                .status(returnOrder.getStatus())
                .refundAmount(returnOrder.getRefundAmount())
                .reason(returnOrder.getReason())
                .imageReturnOrders(imageReturnOrderResponses)
                .returnTrackingCode(returnOrder.getReturnTrackingCode())
                .requestedAt(returnOrder.getRequestedAt())
                .approvedAt(returnOrder.getApprovedAt())
                .paymentAt(returnOrder.getPaymentAt())
                .orderResponse(OrderMapper.getOrderResponse(returnOrder.getOrder()))
                .build();
    }
}
