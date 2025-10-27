package backend_for_react.backend_for_react.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ReturnOrderItemResponse {
    private Long id;
    private OrderItemResponse orderItemResponse;
    private Integer quantity;
    private BigDecimal itemRefundAmount;
}
