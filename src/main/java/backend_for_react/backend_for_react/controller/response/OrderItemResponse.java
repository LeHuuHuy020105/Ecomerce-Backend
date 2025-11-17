package backend_for_react.backend_for_react.controller.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    Long orderItemId;
    Long productId;
    Boolean isReviewed;
    ProductVariantResponse productVariantResponse;
    Integer quantity;
    BigDecimal listPriceSnapShot;
    BigDecimal finalPrice;
    String urlImageSnapShot;
    String nameProductSnapShot;
    String variantSnapShot;
    Integer returnQuantity;
}
