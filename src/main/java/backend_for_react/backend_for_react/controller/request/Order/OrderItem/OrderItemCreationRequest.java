package backend_for_react.backend_for_react.controller.request.Order.OrderItem;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderItemCreationRequest implements Serializable {
    private int quantity;
    private Long productVariantId;
}
