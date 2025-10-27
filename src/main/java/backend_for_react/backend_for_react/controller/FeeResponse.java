package backend_for_react.backend_for_react.controller;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeResponse {
    private BigDecimal shippingFee;
    private BigDecimal insuranceFee;
    private BigDecimal total;       // shippingFee + insuranceFee
    private Object raw;             // raw response from GHN for debug
}
