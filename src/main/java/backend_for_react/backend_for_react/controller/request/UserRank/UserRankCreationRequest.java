package backend_for_react.backend_for_react.controller.request.UserRank;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRankCreationRequest {
    private String name;
    private BigDecimal minSpent;
}
