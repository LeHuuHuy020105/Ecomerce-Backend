package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.PaymentStatus;
import backend_for_react.backend_for_react.common.enums.PaymentType;
import backend_for_react.backend_for_react.model.OrderItem;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long id;
    UserResponse userResponse;
    String customerName;
    String customerPhone;
    String deliveryWardName;
    Integer deliveryDistrictId;
    Integer deliveryProvinceId;
    String deliveryDistrictName;
    String deliveryProvinceName;
    String deliveryWardCode;
    String deliveryAddress;
    BigDecimal totalAmount;
    BigDecimal originalOrderAmount;
    DeliveryStatus deliveryStatus;
    PaymentStatus paymentStatus;
    PaymentType paymentType;
    String orderTrackingCode;
    List<OrderItemResponse> orderItemResponses;
}
