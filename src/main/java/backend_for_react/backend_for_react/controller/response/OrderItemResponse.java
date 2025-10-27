package backend_for_react.backend_for_react.controller.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    Long orderItemId;
    ProductVariantResponse productVariantResponse;
    Integer quantity;
    Integer returnQuantity;
    List<ReviewResponse> reviewResponses;

}
