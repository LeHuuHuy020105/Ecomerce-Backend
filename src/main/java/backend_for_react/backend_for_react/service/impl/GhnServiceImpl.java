package backend_for_react.backend_for_react.service.impl;


import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.PaymentStatus;
import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.config.Delivery.DeliveryConfig;
import backend_for_react.backend_for_react.controller.FeeResponse;
import backend_for_react.backend_for_react.controller.request.Shipping.*;
import backend_for_react.backend_for_react.controller.response.ShippingOrderDetailResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.Order;
import backend_for_react.backend_for_react.model.OrderItem;
import backend_for_react.backend_for_react.model.ReturnOrder;
import backend_for_react.backend_for_react.model.ReturnOrderItem;
import backend_for_react.backend_for_react.repository.OrderRepository;
import backend_for_react.backend_for_react.repository.ReturnOrderRepository;
import backend_for_react.backend_for_react.service.GhnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "GHN-SERVICE")
public class GhnServiceImpl implements GhnService {

    private final DeliveryConfig deliveryConfig;
    private final OrderRepository orderRepository;
    private final ReturnOrderRepository returnOrderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.information.fromContactName}")
    private String fromContactName;

    @Value("${spring.information.fromAddress}")
    private String fromAddress;

    @Value("${spring.information.fromContactPhone}")
    private String fromContactPhone;

    @Value("${spring.information.fromDistrictName}")
    private String fromDistrictName;

    @Value("${spring.information.fromDistrictId}")
    private Integer fromDistrictId;

    @Value("${spring.information.fromWardId}")
    private String fromWardId;

    @Value("${spring.information.fromWardName}")
    private String fromWardName;

    @Value("${spring.information.fromProvinceName}")
    private String fromProvinceName;

    @Value("${spring.information.fromContactName}")
    private String fromName;

    private HttpHeaders defaultHeaders() {
        log.info("token: {}" , deliveryConfig.getToken());
        log.info("shopId: {}" , deliveryConfig.getShopId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", deliveryConfig.getToken());
        headers.set("ShopId", deliveryConfig.getShopId().toString());
        return headers;
    }


    @Override
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_ORDER_SHIP')")
    public ShippingOrderDetailResponse createShippingOrder(Long orderId ,String requiredNote) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.ORDER_NOT_FOUND));
        if(!order.getOrderStatus().equals(DeliveryStatus.SHIPPED)){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Order status must be SHIPPED");
        }
        if(order.getPaymentStatus().equals(PaymentStatus.PAID)){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Payment status is PAIDED");
        }
        ShippingOrderRequest req = toShippingOrderRequest(order);
        String url = deliveryConfig.getBaseUrl() + "/v2/shipping-order/create";

        Map<String, Object> body = new HashMap<>();
        body.put("to_name", req.getDeliveryContactName());
        body.put("from_name", fromContactName);
        body.put("from_phone", fromContactPhone);
        body.put("from_address", fromAddress);
        body.put("from_ward_name", fromWardName);
        body.put("from_district_name", fromDistrictName);
        body.put("from_province_name", fromProvinceName);
        body.put("to_phone", req.getDeliveryContactPhone());
        body.put("to_address", req.getDeliveryAddress());
        body.put("to_district_id", req.getDeliveryDistrictId());
        body.put("to_ward_code",req.getDeliveryWardCode());
        body.put("weight", req.getWeight());
        body.put("length", req.getLength());
        body.put("width", req.getWidth());
        body.put("height", req.getHeight());
        body.put("service_id", req.getServiceId());
        body.put("required_note", requiredNote);
        body.put("payment_type_id",1);
        body.put("service_type_id",2);
        body.put("items", req.getItems());

        log.info("body: {}", body);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        log.info("GHN Create Order Response: {}", response.getBody());

        Map data = (Map) response.getBody().get("data");

        Object orderCode = data.get("order_code");
        if (orderCode != null){
            order.setOrderTrackingCode(data.get("order_code").toString());
        }
        Object totalFeeObj = data.get("total_fee");
        log.info("totalFeeObj: {}", totalFeeObj);
        if (totalFeeObj != null) {
            BigDecimal totalFee = new BigDecimal(totalFeeObj.toString());
            order.setTotalFeeForShip(totalFee);
        }
        orderRepository.save(order);

        return ShippingOrderDetailResponse.builder()
                .orderId(String.valueOf(req.getOrderId()))
                .orderCode(order.getOrderTrackingCode())
                .status(data.get("status") != null ? data.get("status").toString() : "CREATED")
                .receiver(Receiver.builder()
                        .name(req.getDeliveryContactName())
                        .phone(req.getDeliveryContactPhone())
                        .address(req.getDeliveryAddress())
                        .wardCode(req.getDeliveryWardCode())
                        .districtId(req.getDeliveryDistrictId())
                        .build())
                .sender(Sender.builder()
                        .name(fromContactName)
                        .phone(fromContactPhone)
                        .address(fromAddress)
                        .provinceName(fromProvinceName)
                        .districtName(fromDistrictName)
                        .wardName(fromWardName)
                        .build())
                .raw(response.getBody())
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_RETURN_ORDER_SHIP')")
    public ShippingOrderDetailResponse createShippingReturnOrder(Long returnOrderId ,String requiredNote) {
        ReturnOrder returnOrder = returnOrderRepository.findById(returnOrderId)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, "Return Order not found"));

        ShippingOrderRequest req = toShippingReturnOrderRequest(returnOrder);
        String url = deliveryConfig.getBaseUrl() + "/v2/shipping-order/create";

        Map<String, Object> body = new HashMap<>();
        body.put("to_name", req.getDeliveryContactName());
        body.put("from_name", returnOrder.getOrder().getCustomerName());
        body.put("from_phone", returnOrder.getOrder().getCustomerPhone());
        body.put("from_address", returnOrder.getOrder().getDeliveryAddress());
        body.put("from_ward_name", returnOrder.getOrder().getDeliveryWardName());
        body.put("from_district_name", returnOrder.getOrder().getDeliveryDistrictName());
        body.put("from_province_name", returnOrder.getOrder().getDeliveryProvinceName());
        body.put("to_phone", req.getDeliveryContactPhone());
        body.put("to_address", req.getDeliveryAddress());
        body.put("to_district_id", req.getDeliveryDistrictId());
        body.put("to_ward_code",req.getDeliveryWardCode());
        body.put("weight", req.getWeight());
        body.put("length", req.getLength());
        body.put("width", req.getWidth());
        body.put("height", req.getHeight());
        body.put("service_id", req.getServiceId());
        body.put("required_note", requiredNote);
        body.put("payment_type_id",2);
        body.put("items", req.getItems());

        log.info("body: {}", body);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        log.info("GHN Create Order Response: {}", response.getBody());

        Map data = (Map) response.getBody().get("data");

        Object orderCode = data.get("order_code");
        if (orderCode != null){
            returnOrder.setReturnTrackingCode(data.get("order_code").toString());
        }
        Object totalFeeObj = data.get("total_fee");
        if (totalFeeObj != null) {
            BigDecimal totalFee = new BigDecimal(totalFeeObj.toString());
            returnOrder.setReturnShippingFee(totalFee);
        }
        returnOrderRepository.save(returnOrder);

        return ShippingOrderDetailResponse.builder()
                .orderId(String.valueOf(req.getOrderId()))
                .orderCode(returnOrder.getReturnTrackingCode())
                .status(data.get("status") != null ? data.get("status").toString() : "CREATED")
                .receiver(Receiver.builder()
                        .name(req.getDeliveryContactName())
                        .phone(req.getDeliveryContactPhone())
                        .address(req.getDeliveryAddress())
                        .wardCode(req.getDeliveryWardCode())
                        .districtId(req.getDeliveryDistrictId())
                        .build())
                .sender(Sender.builder()
                        .name(fromContactName)
                        .phone(fromContactPhone)
                        .address(fromAddress)
                        .provinceName(fromProvinceName)
                        .districtName(fromDistrictName)
                        .wardName(fromWardName)
                        .build())
                .raw(response.getBody())
                .build();
    }

    @Override
    public ShippingOrderDetailResponse getShippingDetail(String ghnOrderCode) {
        String url = deliveryConfig.getBaseUrl() + "/v2/shipping-order/detail";
        Map<String, Object> body = new HashMap<>();
        body.put("order_code", ghnOrderCode);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        log.info("GHN Detail Response: {}", response.getBody());
        Map data = (Map) response.getBody().get("data");

        return ShippingOrderDetailResponse.builder()
                .orderId(data.get("order_code").toString())
                .status(data.get("status").toString())
                .note(data.get("note") != null ? data.get("note").toString() : "")
                .estimatedDelivery(data.get("expected_delivery_time") != null ? data.get("expected_delivery_time").toString() : "")
                .raw(response.getBody())
                .build();
    }

    /***
     * caculatee ship fee
     * @param req
     * @return
     */

    @Override
    public FeeResponse calculateShippingFee(FeeRequest req) {
        log.info("Calculate Fee Request: {}", req);
        String url = deliveryConfig.getBaseUrl() + "/v2/shipping-order/fee";

        Map<String, Object> body = new HashMap<>();
        body.put("from_district_id", req.getFromDistrictId());
        body.put("from_ward_code",req.getFromWardCode());
        body.put("to_district_id", req.getToDistrictId());
        body.put("to_ward_code",req.getToWardCode());
        body.put("service_id", req.getServiceId());
        body.put("weight", req.getWeight());
        body.put("height", req.getHeight());
        body.put("width", req.getWidth());
        body.put("length", req.getLength());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        log.info("GHN Fee Response: {}", response.getBody());

        Map data = (Map) response.getBody().get("data");
        FeeResponse res = FeeResponse.builder()
                .shippingFee(new BigDecimal(data.get("total").toString()))
                .insuranceFee(java.math.BigDecimal.ZERO)
                .total(new BigDecimal(data.get("total").toString()))
                .raw(response.getBody())
                .build();

        return res;
    }

    @Override
    public void cancelShippingOrder(String ghnOrderCode) {

    }

    @Override
    public FeeRequest toFeeRequest(Order order) {
        return FeeRequest.builder()
                .fromDistrictId(fromDistrictId)
                .fromWardCode(fromWardId)
                .serviceId(order.getServiceDeliveryId())
                .toDistrictId(order.getDeliveryDistrictId())
                .toWardCode(order.getDeliveryWardCode())
                .weight(order.getWeight())
                .height(order.getHeight())
                .width(order.getWidth())
                .length(order.getLength())
                .build();
    }

    @Override
    public FeeRequest toFeeRequest(ReturnOrder returnOrder) {
        return FeeRequest.builder()
                .fromDistrictId(returnOrder.getOrder().getDeliveryDistrictId())
                .fromWardCode(returnOrder.getOrder().getDeliveryWardCode())
                .serviceId(53320)
                .toDistrictId(fromDistrictId)
                .toWardCode(fromWardId)
                .weight(returnOrder.getTotalWeight())
                .height(returnOrder.getTotalHeight())
                .width(returnOrder.getTotalWidth())
                .length(returnOrder.getTotalLength())
                .build();
    }

    @Override
    public ShippingOrderRequest toShippingOrderRequest(Order order) {
        List<ShippingOrderItem> items = order.getOrderItems().stream()
                .map(this::toShippingOrderItem)
                .toList();
        return ShippingOrderRequest.builder()
                .orderId(order.getId())
                .deliveryWardCode(order.getDeliveryWardCode())
                .deliveryDistrictId(order.getDeliveryDistrictId())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryContactName(order.getCustomerName())
                .deliveryContactPhone(order.getCustomerPhone())
                .serviceId(order.getServiceDeliveryId())
                .weight(order.getWeight())
                .length(order.getLength())
                .width(order.getWidth())
                .height(order.getHeight())
                .items(items)
                .build();
    }

    @Override
    public ShippingOrderRequest toShippingReturnOrderRequest(ReturnOrder returnOrder) {
        List<ShippingOrderItem> items = returnOrder.getReturnOrderItems().stream()
                .map(this::toShippingReturnOrderItem)
                .toList();
        return ShippingOrderRequest.builder()
                .orderId(returnOrder.getId())
                .deliveryWardCode(fromWardId)
                .deliveryDistrictId(fromDistrictId)
                .deliveryAddress(fromAddress)
                .deliveryContactName(fromContactName)
                .deliveryContactPhone(fromContactPhone)
                .serviceId(53320)
                .weight(returnOrder.getTotalWeight())
                .length(returnOrder.getTotalLength())
                .width(returnOrder.getTotalWidth())
                .height(returnOrder.getTotalHeight())
                .items(items)
                .build();
    }

    private ShippingOrderItem toShippingReturnOrderItem(ReturnOrderItem returnOrderItem) {
        BigDecimal priceDecimal = returnOrderItem.getOrderItem().getFinalPrice();


        int price = priceDecimal.setScale(0, RoundingMode.CEILING).intValueExact();

        return ShippingOrderItem.builder()
                .name(returnOrderItem.getOrderItem().buildName())
                .quantity(returnOrderItem.getQuantity())
                .price(price)
                .code(returnOrderItem.getOrderItem().getProductVariant().getSku())
                .build();
    }


    private ShippingOrderItem toShippingOrderItem(OrderItem orderItem) {
        BigDecimal priceDecimal = orderItem.getProductVariant().getPrice();


        int price = priceDecimal.setScale(0, RoundingMode.CEILING).intValueExact();

        return ShippingOrderItem.builder()
                .name(orderItem.buildName())
                .quantity(orderItem.getQuantity())
                .price(price)
                .code(orderItem.getProductVariant().getSku())
                .build();
    }


    @Override
    public Map<String, String> resolveAddress(Integer districtId, String wardCode) {
        // Lấy thông tin Quận
        String districtUrl = deliveryConfig.getBaseUrl() + "/master-data/district";
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders());

        ResponseEntity<Map> districtResp = restTemplate.exchange(districtUrl, HttpMethod.POST, entity, Map.class);
        String districtName = null;
        String provinceName = null;

        if (districtResp.getStatusCode().is2xxSuccessful()) {
            var districts = (Iterable<Map<String, Object>>) districtResp.getBody().get("data");
            for (Map<String, Object> d : districts) {
                if (d.get("DistrictID").equals(districtId)) {
                    districtName = (String) d.get("DistrictName");
                    provinceName = (String) d.get("ProvinceName");
                    break;
                }
            }
        }

        // Lấy thông tin Phường
        String wardUrl = deliveryConfig.getBaseUrl() + "/master-data/ward";
        Map<String, Integer> req = Map.of("district_id", districtId);
        HttpEntity<Map<String, Integer>> wardEntity = new HttpEntity<>(req, defaultHeaders());

        ResponseEntity<Map> wardResp = restTemplate.exchange(wardUrl, HttpMethod.POST, wardEntity, Map.class);
        String wardName = null;
        if (wardResp.getStatusCode().is2xxSuccessful()) {
            var wards = (Iterable<Map<String, Object>>) wardResp.getBody().get("data");
            for (Map<String, Object> w : wards) {
                if (w.get("WardCode").equals(wardCode)) {
                    wardName = (String) w.get("WardName");
                    break;
                }
            }
        }

        return Map.of(
                "wardName", wardName != null ? wardName : "",
                "districtName", districtName != null ? districtName : "",
                "provinceName", provinceName != null ? provinceName : ""
        );
    }


}
