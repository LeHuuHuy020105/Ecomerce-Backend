package backend_for_react.backend_for_react.controller.request.Shipping;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeRequest {
    @NotNull
    private String fromWardCode;

    @NotNull
    private Integer fromDistrictId;

    @NotNull
    private String toWardCode;

    @NotNull
    private Integer toDistrictId;

    @NotNull
    @Min(1)
    private Integer weight;          // gram

    // optional: kích thước để GHN tính (nếu quan trọng)
    private Integer length; // cm
    private Integer width;  // cm
    private Integer height; // cm

    private Integer serviceId; // (nếu biết)
}
