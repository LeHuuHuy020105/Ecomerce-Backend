package backend_for_react.backend_for_react.controller.request.Order;

import backend_for_react.backend_for_react.common.enums.PaymentType;
import backend_for_react.backend_for_react.controller.request.Order.OrderItem.OrderItemCreationRequest;
import backend_for_react.backend_for_react.model.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderCreationRequest implements Serializable {
    @NotBlank(message = "Customer name must be not blank")
    private String customerName;
    @NotBlank(message = "Customer phone must be not blank")
    private String customerPhone;
    @NotBlank(message = "Delivery ward name must be not blank")
    private String deliveryWardName;
    @NotBlank(message = "Delivery ward code must be not blank")
    private String deliveryWardCode;
    @NotNull(message = "Delivery district id must be not null")
    private Integer deliveryDistrictId;
    @NotNull(message = "Delivery province id must be not null")
    private Integer deliveryProvinceId;
    @NotBlank(message = "Delivery district name must be not blank")
    private String deliveryDistrictName;
    @NotBlank(message = "Delivery province name must be not blank")
    private String deliveryProvinceName;
    @NotBlank(message = "Delivery address must be not blank")
    private String deliveryAddress;
    @NotNull(message = "Delivery service id must be not null")
    private Integer serviceDeliveryId;
    @NotBlank(message = "Delivery service name must be not blank")
    private String serviceDeliveryName;
    @NotNull(message = "Order items must be not null")
    private List<OrderItemCreationRequest>orderItems;
    @NotNull(message = "Payment type must be not null")
    private PaymentType paymentType;
    private Long voucherId;
}
