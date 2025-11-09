package backend_for_react.backend_for_react.mapper;

import backend_for_react.backend_for_react.controller.response.OrderItemResponse;
import backend_for_react.backend_for_react.controller.response.OrderResponse;
import backend_for_react.backend_for_react.controller.response.ProductVariantResponse;
import backend_for_react.backend_for_react.model.Order;
import backend_for_react.backend_for_react.model.OrderItem;
import backend_for_react.backend_for_react.model.ProductVariant;
import backend_for_react.backend_for_react.service.impl.ProductService;

import java.math.BigDecimal;
import java.util.List;

public class OrderMapper {
    public static OrderResponse getOrderResponse(Order order) {
        List<OrderItemResponse> orderItemResponseList = order.getOrderItems().stream()
                .map(OrderMapper::getOrderItemResponse)
                .toList();
        return OrderResponse.builder()
                .id(order.getId())
                .orderTrackingCode(order.getOrderTrackingCode())
                .userResponse(UserMapper.getPublicUserResponse(order.getUser()))
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .deliveryWardName(order.getDeliveryWardName())
                .deliveryDistrictId(order.getDeliveryDistrictId())
                .deliveryProvinceId(order.getDeliveryProvinceId())
                .deliveryDistrictName(order.getDeliveryDistrictName())
                .deliveryProvinceName(order.getDeliveryProvinceName())
                .note(order.getNote())
                .deliveryWardCode(order.getDeliveryWardCode())
                .deliveryAddress(order.getDeliveryAddress())
                .totalAmount(order.getTotalAmount())
                .originalOrderAmount(order.getOriginalOrderAmount())
                .deliveryStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .orderItemResponses(orderItemResponseList)
                .paymentType(order.getPaymentType())
                .build();
    }
    public static OrderItemResponse getOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .productVariantResponse(ProductVariantMapper.getProductVariantResponse(orderItem.getProductVariant()))
                .quantity(orderItem.getQuantity())
                .listPriceSnapShot(orderItem.getListPriceSnapShot())
                .finalPrice(orderItem.getFinalPrice())
                .urlImageSnapShot(orderItem.getUrlImageSnapShot())
                .nameProductSnapShot(orderItem.getNameProductSnapShot())
                .variantSnapShot(orderItem.getNameProductSnapShot())
                .returnQuantity(orderItem.getReturnedQuantity())
                .build();
    }
}
