package backend_for_react.backend_for_react.controller.request.Voucher;

import backend_for_react.backend_for_react.common.enums.VoucherStatus;
import backend_for_react.backend_for_react.common.enums.VoucherType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherUpdateRequest {
    private Long id;

    private String code;

    private String discription;

    private VoucherType type;

    private Double discountValue;

    private Double maxDiscountValue;

    private Double minDiscountValue;

    private Integer totalQuantity;

    private VoucherStatus status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer usageLimitPerUser = 0;

    private Long userRankId;
}
