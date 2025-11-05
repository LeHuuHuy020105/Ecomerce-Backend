package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.VoucherStatus;
import backend_for_react.backend_for_react.common.enums.VoucherType;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserRank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponse {
    private Long id;


    private String discription;

    private VoucherType type;

    private Double discountValue;

    private Double maxDiscountValue;

    private Double minDiscountValue;

    private Integer totalQuantity;

    private boolean isShipping;

    private VoucherStatus status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private List<Long> applicableProductsId;

    private Integer usageLimitPerUser;

    private UserRankResponse userRankResponse;
}
