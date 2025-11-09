package backend_for_react.backend_for_react.controller.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class OrderItemPreview {
    private Long productVariantId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal finalPrice;
}
