package backend_for_react.backend_for_react.controller.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticalResponse {
    private BigDecimal revenue;
    private BigDecimal profit;
}
