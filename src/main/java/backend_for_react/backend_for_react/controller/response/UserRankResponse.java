package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRankResponse {
    private Long id;
    private String name;
    private BigDecimal minSpent;
    private Status status;
}
