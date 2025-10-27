package backend_for_react.backend_for_react.controller.request.Voucher;

import backend_for_react.backend_for_react.common.enums.VoucherType;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.validator.DobConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoucherCreationRequest {
    @NotNull(message ="Voucher code not blank")
    private String code;

    @NotNull(message ="Discription voucher not blank")
    private String description;

    @NotNull(message ="Voucher type not blank")
    private VoucherType type;

    @NotNull(message ="Discount value voucher not blank")
    private Double discountValue;

    private Double maxDiscountValue;

    private Double minDiscountValue;

    @NotNull(message ="Quantity voucher not blank")
    private Integer totalQuantity;

    @NotNull(message ="Date start voucher not blank")
    private LocalDateTime startDate;

    @NotNull(message ="Date end voucher not blank")
    private LocalDateTime endDate;

    @Min(value = 1, message = "Each user must be allowed to use the voucher at least once")
    private Integer usageLimitPerUser = 1;

    private Long userRankId;

    @NotNull(message = "Shipping is not null")
    private Boolean isShipping;
}
