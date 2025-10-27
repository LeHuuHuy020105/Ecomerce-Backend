package backend_for_react.backend_for_react.controller.request.Cart;

import lombok.Data;

@Data
public class CartCreationRequest {
    private Long productVariantId;
    private Integer quantity;
}
