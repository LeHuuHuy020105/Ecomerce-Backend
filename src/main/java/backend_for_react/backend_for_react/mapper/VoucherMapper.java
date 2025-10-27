package backend_for_react.backend_for_react.mapper;

import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.UserResponse;
import backend_for_react.backend_for_react.controller.response.VoucherResponse;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.Voucher;
import org.springframework.data.domain.Page;

import java.util.List;

public class VoucherMapper {
    public static VoucherResponse toVoucherResponse(Voucher voucher) {
        VoucherResponse response = VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .discription(voucher.getDiscription())
                .type(voucher.getType())
                .discountValue(voucher.getDiscountValue())
                .maxDiscountValue(voucher.getMaxDiscountValue())
                .minDiscountValue(voucher.getMinDiscountValue())
                .totalQuantity(voucher.getTotalQuantity())
                .status(voucher.getStatus())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .usageLimitPerUser(voucher.getUsageLimitPerUser())
                .userRankResponse(UserRankMapper.toUserRankResponse(voucher.getUserRank()))
                .build();
        return response;
    }
}
