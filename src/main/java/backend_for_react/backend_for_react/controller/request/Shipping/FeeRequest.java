package backend_for_react.backend_for_react.controller.request.Shipping;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeRequest {

    @NotNull(message = "fromWardCode is required")
    private String fromWardCode;

    @NotNull(message = "fromDistrictId is required")
    private Integer fromDistrictId;

    @NotNull(message = "toWardCode is required")
    private String toWardCode;

    @NotNull(message = "toDistrictId is required")
    private Integer toDistrictId;

    /**
     * Tổng khối lượng của đơn hàng (gram)
     * GHN yêu cầu phải luôn truyền field này, kể cả hàng nặng
     */
    @NotNull(message = "weight is required")
    @Min(value = 1, message = "weight must be >= 1 gram")
    private Integer weight;

    /**
     * Kích thước kiện hàng (cm) — chỉ dùng cho hàng nhẹ (service_type_id = 2)
     */
    private Integer length;
    private Integer width;
    private Integer height;

    /**
     * Mã loại dịch vụ:
     * 2 = Hàng nhẹ
     * 5 = Hàng nặng
     * Có thể backend tự động xác định nếu FE không truyền
     */
    private Integer serviceTypeId;

    /**
     * Danh sách sản phẩm trong đơn (chỉ bắt buộc nếu là hàng nặng)
     */
    private List<ItemRequest> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemRequest {
        @NotBlank(message = "nameProduct is required")
        private String name;

        @NotNull(message = "length is required for heavy items")
        @Min(value = 1, message = "length must be > 0")
        private Integer length;

        @NotNull(message = "width is required for heavy items")
        @Min(value = 1, message = "width must be > 0")
        private Integer width;

        @NotNull(message = "height is required for heavy items")
        @Min(value = 1, message = "height must be > 0")
        private Integer height;

        @NotNull(message = "weight is required for heavy items")
        @Min(value = 1, message = "weight must be > 0")
        private Integer weight;

        private Integer quantity = 1;
    }
}
