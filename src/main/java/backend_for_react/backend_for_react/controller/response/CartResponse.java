package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.model.ProductVariant;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private ProductVariantResponse productVariantResponse;
    private ProductBaseResponse productBaseResponse;
    private Integer quantity;
}
