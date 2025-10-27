package backend_for_react.backend_for_react.controller.request.Shipping;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingOrderItem {
    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private int quantity;

    @NotNull
    private Integer price;

    private String code;
}
