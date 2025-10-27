package backend_for_react.backend_for_react.controller.request.Cart;

import lombok.Data;

import javax.swing.*;

@Data
public class CartUpdateRequest {
    private Long productVariantId;
    private Integer quantity;
}
