package backend_for_react.backend_for_react.controller.request.Shipping;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingOrderRequest {
    @NotBlank
    @NotNull(message = "Order id must be not null")
    private Long orderId;

    @NotNull
    private String deliveryWardCode;

    @NotNull
    private Integer deliveryDistrictId;

    @NotBlank
    private String deliveryAddress;

    @NotBlank
    private String deliveryContactName;

    @NotBlank
    private String deliveryContactPhone;

    // info goods

    @NotNull
    @Min(1)
    private Integer weight;              // gram (GHN expects grams)

    @Min(0)
    private Integer length;              // cm (optional)

    @Min(0)
    private Integer width;               // cm (optional)

    @Min(0)
    private Integer height;              // cm (optional)

    @NotNull
    private List<ShippingOrderItem> items;


    // Serive and payment
    @NotNull
    private Integer serviceId;           // GHN service id (vd: 53320...), hoặc service_type_id tùy API


    @NotNull
    private Integer paymentType = 1;         // 1 = Người gửi trả, 2 = Người nhận trả (tùy GHN)

    // ghi chú
    private String note;                 // ghi chú cho shipper

}
