package backend_for_react.backend_for_react.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantAttributeResponse {
    private Long id;
    private String attribute; // Tên phân loại (VD: "Color")
    private String value; // Giá trị cụ thể (VD: "Đỏ")

}
