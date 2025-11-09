package backend_for_react.backend_for_react.controller.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Builder
@Data
public class OrderPricePreviewResponse {
    private List<OrderItemPreview> items;
    private BigDecimal subTotal;
    private BigDecimal discount;
    private BigDecimal shippingFee;
    private BigDecimal finalTotal;
}
