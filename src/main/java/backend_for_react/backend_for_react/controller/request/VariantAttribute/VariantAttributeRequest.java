package backend_for_react.backend_for_react.controller.request.VariantAttribute;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantAttributeRequest {
    @NotBlank(message = "Attribute name not blank")
    private String attribute; // Tên phân loại (VD: "Color")
    @NotBlank(message = "Attribute value not blank")
    private String value; // Giá trị cụ thể (VD: "Đỏ")
}
