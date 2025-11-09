package backend_for_react.backend_for_react.service.impl;

import backend_for_react.backend_for_react.common.enums.*;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.config.AppConfig;
import backend_for_react.backend_for_react.controller.FeeResponse;
import backend_for_react.backend_for_react.controller.request.Order.OrderCreationRequest;
import backend_for_react.backend_for_react.controller.request.Order.OrderItem.OrderItemCreationRequest;
import backend_for_react.backend_for_react.controller.request.ProductPackage.ProductPackage;
import backend_for_react.backend_for_react.controller.request.ProductPackage.ProductPackageResponse;
import backend_for_react.backend_for_react.controller.request.Shipping.FeeRequest;
import backend_for_react.backend_for_react.controller.request.Shipping.ShippingOrderItem;
import backend_for_react.backend_for_react.controller.request.Shipping.ShippingOrderRequest;
import backend_for_react.backend_for_react.controller.response.*;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.ProductVariantMapper;
import backend_for_react.backend_for_react.mapper.UserMapper;
import backend_for_react.backend_for_react.model.*;
import backend_for_react.backend_for_react.repository.*;
import backend_for_react.backend_for_react.service.FireBaseService;
import backend_for_react.backend_for_react.service.GhnService;
import backend_for_react.backend_for_react.service.VoucherService;
import backend_for_react.backend_for_react.state.Order.OrderState;
import backend_for_react.backend_for_react.state.Order.OrderStateFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static backend_for_react.backend_for_react.common.utils.ShippingHelper.*;
import static backend_for_react.backend_for_react.mapper.OrderMapper.getOrderItemResponse;
import static backend_for_react.backend_for_react.mapper.OrderMapper.getOrderResponse;


@Slf4j(topic = "ORDER-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    ProductRepository productRepository;
    OrderItemRepository orderItemRepository;
    SecurityUtils securityUtils;
    ProductVariantRepository productVariantRepository;
    ProductService productService;
    VoucherService voucherService;
    VoucherRepository voucherRepository;
    VoucherUsageRepository voucherUsageRepository;
    GhnService ghnService;
    FireBaseService fireBaseService;
    UserService userService;
    AppConfig appConfig;
    private final UserRepository userRepository;

    public PageResponse<OrderResponse> findAllByUser(String keyword, String sort, int page, int size , boolean isAll, DeliveryStatus orderStatus ) {
        log.info("KEYWORD : ", keyword);
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
        Page<Order> orders = null;
        if (keyword == null || keyword.isEmpty()) {
            if(isAll) orders = orderRepository.findAllByUser(user,pageable);
            else orders = orderRepository.findAllByUserAndOrderStatus(user,orderStatus,pageable);
        } else {
            log.info("Keyword");
            keyword = "%" + keyword.toLowerCase() + "%";
            if (isAll) orders = orderRepository.searchByKeywordAndUser(keyword,pageable,user);
            else orders = orderRepository.searchByKeywordAndUser(keyword,pageable,user,orderStatus);
        }
        PageResponse response = getOrderPageResponse(pageNo, size, orders);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_ALL_ORDER')")
    public PageResponse<OrderResponse> findAllByAdmin( String keyword,
                                                       boolean isAll,
                                                       DeliveryStatus orderStatus,
                                                       String sort,
                                                       int page,
                                                       int size,
                                                       LocalDateTime startDate,
                                                       LocalDateTime endDate) {
        log.info("KEYWORD : ", keyword);
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
        String search = (keyword == null || keyword.isEmpty()) ? "" : keyword.trim().toLowerCase();
        Page<Order> orders = null;
        if(isAll) orders = orderRepository.searchByKeywordAndFilter(search,startDate, endDate, pageable);
        else orders = orderRepository.searchByKeywordAndFilter(search,orderStatus,startDate, endDate, pageable);
        return getOrderPageResponse(pageNo, size, orders);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long save(OrderCreationRequest req) {
        Order newOrder = new Order();
        User currentUser = securityUtils.getCurrentUser();

        // Thiết lập thông tin cơ bản
        newOrder.setCustomerName(req.getCustomerName());
        newOrder.setCustomerPhone(req.getCustomerPhone());
        newOrder.setDeliveryAddress(req.getDeliveryAddress());
        newOrder.setDeliveryDistrictId(req.getDeliveryDistrictId());
        newOrder.setDeliveryWardName(req.getDeliveryWardName());
        newOrder.setDeliveryProvinceId(req.getDeliveryProvinceId());
        newOrder.setDeliveryProvinceName(req.getDeliveryProvinceName());
        newOrder.setDeliveryDistrictName(req.getDeliveryDistrictName());
        newOrder.setDeliveryWardCode(req.getDeliveryWardCode());
        newOrder.setPaymentType(req.getPaymentType());
        newOrder.setOrderStatus(DeliveryStatus.PENDING);
        newOrder.setPaymentStatus(PaymentStatus.UNPAID);
        newOrder.setAfterSaleStatus(OrderAfterSaleStatus.NONE);
        newOrder.setNote(req.getNote());

        if (currentUser != null) {
            newOrder.setUser(currentUser);
        }


        Map<Long, Integer> mergedVariants = new HashMap<>();
        for (OrderItemCreationRequest itemReq : req.getOrderItems()) {
            mergedVariants.merge(itemReq.getProductVariantId(), itemReq.getQuantity(), Integer::sum);
        }


        // Tạo order item
        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : mergedVariants.entrySet()) {
            Long variantId = entry.getKey();
            Integer totalQuantity = entry.getValue();
            ProductVariant productVariant = productVariantRepository.findByIdAndStatus(variantId, Status.ACTIVE)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.PRODUCT_VARIANT_NOT_FOUND));

            if (totalQuantity> productVariant.getQuantity()) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Product exceeds quantity");
            }

            // Cập nhật tồn kho
            productVariant.setQuantity(productVariant.getQuantity() - totalQuantity);
            productVariantRepository.save(productVariant);

            // Tạo order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(newOrder);
            orderItem.setListPriceSnapShot(productVariant.getPrice());
            orderItem.setNameProductSnapShot(productVariant.getProduct().getName());
            orderItem.setVariantAttributesSnapshot(ProductVariantMapper.buildVariantName(productVariant));
            orderItem.setProductVariant(productVariant);
            orderItem.setUrlImageSnapShot(productVariant.getProduct().getUrlCoverImage());
            orderItem.setQuantity(totalQuantity);

            BigDecimal itemTotal = productVariant.getPrice().multiply(BigDecimal.valueOf(totalQuantity));
            subTotal = subTotal.add(itemTotal);

            // Tạm gán finalPrice = giá gốc (chưa trừ voucher)
            orderItem.setFinalPrice(productVariant.getPrice());

            orderItems.add(orderItem);
        }
        newOrder.setOrderItems(orderItems);

        // Cập nhật kích thước - trọng lượng
        List<ProductPackage> packages = orderItems.stream()
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
        newOrder.setHeight(height);
        newOrder.setLength(length);
        newOrder.setWeight(weight);
        newOrder.setWidth(width);
        newOrder.setServiceTypeId(determineServiceTypeId(weight, length, width, height));
        //Tinh phi ship
        FeeRequest feeRequest = ghnService.toFeeRequest(newOrder);
        BigDecimal feeShip = ghnService.calculateShippingFee(feeRequest).getTotal();
        newOrder.setTotalFeeForShip(feeShip);

        // Áp dụng voucher nếu có
        BigDecimal discountValue = BigDecimal.ZERO;
        Voucher voucher = null;

        if (req.getVoucherId() != null) {
            voucher = voucherRepository.findByIdAndStatus(req.getVoucherId(),VoucherStatus.ACTIVE)
                    .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "Voucher not found"));

            voucherService.validateVoucherWithOderAmount(voucher, subTotal);
            voucherService.validateVoucherUsageUser(voucher, currentUser);

            if (voucher.getIsShipping() != null && voucher.getIsShipping()) {
                log.info("abcsd");
                discountValue = voucherService.calculateDiscountValue(feeShip, voucher);
            } else {
                discountValue = voucherService.calculateDiscountValue(subTotal, voucher);
            }

            voucherService.decreaseVoucherQuantity(voucher);
            newOrder.setVoucher(voucher);
            newOrder.setVoucherDiscountValue(discountValue);
        }

        // Phân bổ giảm giá cho từng sản phẩm
        if (discountValue.compareTo(BigDecimal.ZERO) > 0 && !voucher.getIsShipping()) {
            for (OrderItem item : orderItems) {
                BigDecimal itemTotal = item.getFinalPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal ratio = itemTotal.divide(subTotal, 6, BigDecimal.ROUND_HALF_UP);
                BigDecimal itemDiscount = discountValue.multiply(ratio);
                BigDecimal finalUnitPrice = item.getFinalPrice().subtract(itemDiscount.divide(BigDecimal.valueOf(item.getQuantity()), 2, BigDecimal.ROUND_HALF_UP));

                item.setFinalPrice(finalUnitPrice);
            }
        }
        discountValue = discountValue.add(appConfig.getPointValue().multiply(BigDecimal.valueOf(req.getPoint())));
        // Cập nhật tổng tiền đơn hàng
        log.info("discountValue: " + discountValue);
        BigDecimal totalAfterDiscount = subTotal.subtract(discountValue);
        newOrder.setOriginalOrderAmount(subTotal);
        newOrder.setTotalAmount(totalAfterDiscount.add(feeShip));

        orderRepository.save(newOrder);

        currentUser.setPoint(currentUser.getPoint()- req.getPoint());
        userRepository.save(currentUser);

        if(voucher != null){
            if (currentUser != null) {
                VoucherUsage usage = new VoucherUsage();
                usage.setVoucher(voucher);
                usage.setUser(currentUser);
                usage.setOrder(newOrder);
                voucherUsageRepository.save(usage);
            }
        }

        fireBaseService.updateStatus(newOrder);
        return newOrder.getId();
    }


    public OrderPricePreviewResponse previewOrderPrice(OrderCreationRequest req) {
        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItemPreview> itemPreviews = new ArrayList<>();

        // 1. Gộp variant giống nhau
        Map<Long, Integer> mergedVariants = new HashMap<>();
        for (OrderItemCreationRequest itemReq : req.getOrderItems()) {
            mergedVariants.merge(itemReq.getProductVariantId(), itemReq.getQuantity(), Integer::sum);
        }

        // 2. Lấy thông tin từng sản phẩm và tính subtotal
        List<OrderItem> tempOrderItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : mergedVariants.entrySet()) {
            Long variantId = entry.getKey();
            Integer quantity = entry.getValue();
            ProductVariant variant = productVariantRepository.findByIdAndStatus(variantId, Status.ACTIVE)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, "Variant not found"));

            BigDecimal itemTotal = variant.getPrice().multiply(BigDecimal.valueOf(quantity));
            subTotal = subTotal.add(itemTotal);

            // Tạo item preview
            OrderItemPreview itemPreview = OrderItemPreview.builder()
                    .productVariantId(variantId)
                    .productName(variant.getProduct().getName())
                    .unitPrice(variant.getPrice())
                    .quantity(quantity)
                    .finalPrice(variant.getPrice()) // sẽ update sau nếu có voucher/point
                    .build();
            itemPreviews.add(itemPreview);

            // Tạo OrderItem tạm để tính phí ship
            OrderItem tempItem = new OrderItem();
            tempItem.setProductVariant(variant);
            tempItem.setQuantity(quantity);
            tempItem.setNameProductSnapShot(variant.getProduct().getName());
            tempOrderItems.add(tempItem);
        }

        // 3. Tạo Order tạm để tính phí ship
        Order tempOrder = new Order();
        tempOrder.setOrderItems(tempOrderItems);

        // Tính tổng chiều cao, dài, rộng, cân nặng
        List<ProductPackage> packages = tempOrderItems.stream()
                .map(item -> new ProductPackage(
                        item.getNameProductSnapShot(),
                        item.getProductVariant().getLength(),
                        item.getProductVariant().getWidth(),
                        item.getProductVariant().getHeight(),
                        item.getProductVariant().getWeight(),
                        item.getQuantity()
                )).toList();

        tempOrder.setHeight(calculateAverageHeight(packages));
        tempOrder.setWidth(calculateAverageWidth(packages));
        tempOrder.setLength(calculateAverageLength(packages));
        tempOrder.setWeight(calculateTotalWeight(packages));
        tempOrder.setServiceTypeId(determineServiceTypeId(
                tempOrder.getWeight(), tempOrder.getLength(),
                tempOrder.getWidth(), tempOrder.getHeight()
        ));

        // 4. Tính phí ship chính xác
        FeeRequest feeRequest = ghnService.toFeeRequest(tempOrder);
        BigDecimal feeShip = ghnService.calculateShippingFee(feeRequest).getTotal();

        // 5. Áp dụng voucher (nếu có)
        BigDecimal discountValue = BigDecimal.ZERO;
        Voucher voucher = null;
        if (req.getVoucherId() != null) {
            voucher = voucherRepository.findByIdAndStatus(req.getVoucherId(), VoucherStatus.ACTIVE)
                    .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "Voucher not found"));

            if (Boolean.TRUE.equals(voucher.getIsShipping())) {
                discountValue = voucherService.calculateDiscountValue(feeShip, voucher);
            } else {
                discountValue = voucherService.calculateDiscountValue(subTotal, voucher);
            }
        }

        // 6. Phân bổ giảm giá cho từng sản phẩm (nếu voucher không phải ship)
        if (discountValue.compareTo(BigDecimal.ZERO) > 0 && (voucher == null || !Boolean.TRUE.equals(voucher.getIsShipping()))) {
            for (OrderItemPreview item : itemPreviews) {
                BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal ratio = itemTotal.divide(subTotal, 6, RoundingMode.HALF_UP);
                BigDecimal itemDiscount = discountValue.multiply(ratio);
                BigDecimal finalUnitPrice = item.getUnitPrice()
                        .subtract(itemDiscount.divide(BigDecimal.valueOf(item.getQuantity()), 2, RoundingMode.HALF_UP));
                item.setFinalPrice(finalUnitPrice);
            }
        }

        // 7. Trừ điểm nếu có
        BigDecimal pointDiscount = appConfig.getPointValue().multiply(BigDecimal.valueOf(req.getPoint()));
        discountValue = discountValue.add(pointDiscount);

        // 8. Tổng sau giảm giá + phí ship
        BigDecimal totalAfterDiscount = subTotal.subtract(discountValue);
        BigDecimal finalTotal = totalAfterDiscount.add(feeShip);

        // 9. Build response
        return OrderPricePreviewResponse.builder()
                .items(itemPreviews)
                .subTotal(subTotal)
                .discount(discountValue)
                .shippingFee(feeShip)
                .finalTotal(finalTotal)
                .build();
    }




    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CHANGE_STATUS_ORDER')")
    public void changeStatus(Long orderId, DeliveryStatus nextStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.ORDER_NOT_FOUND));

        // Check thanh toán chuyển khoản chưa trả tiền
        if (order.getPaymentType() == PaymentType.BANK_TRANSFER &&
                order.getPaymentStatus() == PaymentStatus.UNPAID) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    "You can't change status for unpaid bank transfer order");
        }

        OrderState currentState = OrderStateFactory.getState(order.getOrderStatus());
        currentState.changeState(order, nextStatus);
        orderRepository.save(order);

        fireBaseService.updateStatus(order);
    }


    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId) {
        User currentUser = securityUtils.getCurrentUser();
        Order order = orderRepository.findOrderByIdAndUser(orderId,currentUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED,"Order not found or not yours"));
        if(order.getOrderStatus().equals(DeliveryStatus.CANCELLED)){
            throw new BusinessException(ErrorCode.BAD_REQUEST," You can't complete order cancelled");
        }
       if(!order.getOrderStatus().equals(DeliveryStatus.COMPLETED)){
           throw new BusinessException(ErrorCode.BAD_REQUEST,"Order is not completed");
       }
       if(order.getPaymentStatus().equals(PaymentStatus.UNPAID)){
           throw new BusinessException(ErrorCode.BAD_REQUEST,"The order has not been paid yet");
       }
       if(order.isConfirmed()){
           throw new BusinessException(ErrorCode.BAD_REQUEST,"Order is confirmed");
       }
       order.setConfirmed(true);
        // Nếu có user, cập nhật tổng chi tiêu và hạng
        User user = order.getUser();
        if (user != null) {
            user.setTotalSpent(user.getTotalSpent().add(order.getTotalAmount()));
            userService.updateRank(user);
        }
        order.setCompletedAt(LocalDateTime.now());
        updateSoldQuantity(order.getOrderItems());

        fireBaseService.updateStatus(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSoldQuantity(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            ProductVariant productVariant = orderItem.getProductVariant();
            Product product = productRepository.findByIdAndProductStatus(productVariant.getProduct().getId(),ProductStatus.ACTIVE)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.PRODUCT_NOT_FOUND));
            product.setSoldQuantity(product.getSoldQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        User currentUser = securityUtils.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.ORDER_NOT_FOUND));
        if(order.getUser() != currentUser) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, " This order not yours");
        }
        if(order.getOrderStatus().equals(DeliveryStatus.PENDING)){
            for(OrderItem orderItem : order.getOrderItems()){
                ProductVariant productVariant = orderItem.getProductVariant();
                productVariant.setQuantity(productVariant.getQuantity() + orderItem.getQuantity());
                productVariantRepository.save(productVariant);
            }
            order.setOrderStatus(DeliveryStatus.CANCELLED);
            orderRepository.save(order);
            VoucherUsage voucherUsage = voucherUsageRepository.findByOrder(order)
                    .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"Voucher usage not found this order"));
            voucherUsageRepository.delete(voucherUsage);
            fireBaseService.updateStatus(order);
        }else {
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Can't cancel order");
        }
    }



    public void completePayment(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.ORDER_NOT_FOUND));
        if(!order.getPaymentType().equals(PaymentType.BANK_TRANSFER)){
            throw new BusinessException(ErrorCode.BAD_REQUEST , "Order type payment is not BANK.");
        }
        if(order.getPaymentStatus().equals(PaymentStatus.PAID)){
            throw new BusinessException(ErrorCode.BAD_REQUEST , "This order is PAID.");
        }
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setPaymentAt(LocalDateTime.now());
        orderRepository.save(order);
        fireBaseService.updateStatus(order);
    }
    public OrderResponse getOrderById(Long orderId) {
        User user = securityUtils.getCurrentUser();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.ORDER_NOT_FOUND));
        if(order.getUser() != user){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Order not yours");
        }
        return getOrderResponse(order);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_DETAIL_ORDER')")
    public OrderResponse getOrderByIdForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.ORDER_NOT_FOUND));
        return getOrderResponse(order);
    }

    /**
     * Tự động chuyển trạng thái đơn hàng từ DELIVERED -> DELIVERED_CONFIRMED
     * nếu đã quá 7 ngày kể từ ngày giao hàng thành công.
     * Chạy mỗi ngày lúc 2h sáng.
     */
    @Scheduled(cron = "0 0 2 * * *") // chạy mỗi ngày lúc 2:00 sáng
    @Transactional
    public void autoConfirmOrdersAfter7Days() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);

        // Lấy các đơn đã giao thành công nhưng chưa được xác nhận
        List<Order> ordersToUpdate = orderRepository.findAllByOrderStatusAndDeliveredAtBefore(
                DeliveryStatus.DELIVERED,
                sevenDaysAgo
        );

        if (ordersToUpdate.isEmpty()) {
            log.info("Không có đơn hàng nào cần tự động xác nhận hôm nay.");
            return;
        }

        for (Order order : ordersToUpdate) {
            try {
                order.setOrderStatus(DeliveryStatus.COMPLETED);
                User user = order.getUser();
                if (user != null) {
                    user.setTotalSpent(user.getTotalSpent().add(order.getTotalAmount()));
                    userService.updateRank(user);
                }
                order.setCompletedAt(now);
                orderRepository.save(order);
                updateSoldQuantity(order.getOrderItems());
                log.info("Đã tự động xác nhận đơn hàng id={} sau 7 ngày.", order.getId());
            } catch (Exception e) {
                log.error("Lỗi khi tự động xác nhận đơn hàng id={}: {}", order.getId(), e.getMessage());
            }
        }
    }



    private PageResponse<OrderResponse> getOrderPageResponse(int page, int size, Page<Order> orders) {
        List<OrderResponse> orderResponseList = orders.stream()
                .map(order -> getOrderResponse(order))
                .toList();
        PageResponse<OrderResponse> response = new PageResponse<>();
        response.setPageNumber(page + 1);
        response.setPageSize(size);
        response.setTotalElements(orders.getTotalElements());
        response.setTotalPages(orders.getTotalPages());
        response.setData(orderResponseList);
        return response;
    }
}
