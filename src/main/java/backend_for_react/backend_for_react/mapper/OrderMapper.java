package backend_for_react.backend_for_react.mapper;

import backend_for_react.backend_for_react.controller.response.OrderItemResponse;
import backend_for_react.backend_for_react.controller.response.OrderResponse;
import backend_for_react.backend_for_react.controller.response.ProductVariantResponse;
import backend_for_react.backend_for_react.model.Order;
import backend_for_react.backend_for_react.model.OrderItem;
import backend_for_react.backend_for_react.model.ProductVariant;
import backend_for_react.backend_for_react.service.impl.ProductService;

import java.util.List;

public class OrderMapper {
    public static OrderResponse getOrderResponse(Order order) {
        List<OrderItemResponse> orderItemResponseList = order.getOrderItems().stream()
                .map(OrderMapper::getOrderItemResponse)
                .toList();
        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryDistrictId(order.getDeliveryDistrictId())
                .deliveryWardName(order.getDeliveryWardName())
                .deliveryWardCode(order.getDeliveryWardCode())
                .deliveryProvinceId(order.getDeliveryProvinceId())
                .deliveryDistrictName(order.getDeliveryDistrictName())
                .deliveryProvinceName(order.getDeliveryProvinceName())
                .serviceDeliveryId(order.getServiceDeliveryId())
                .serviceDeliveryName(order.getServiceDeliveryName())
                .paymentType(order.getPaymentType())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryStatus(order.getOrderStatus())
                .orderItemResponses(orderItemResponseList)
                .build();
    }
    public static OrderItemResponse getOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .productVariantResponse(ProductVariantMapper.getProductVariantResponse(orderItem.getProductVariant()))
                .quantity(orderItem.getQuantity())
                .returnQuantity(orderItem.getReturnedQuantity())
                .build();
    }
}
