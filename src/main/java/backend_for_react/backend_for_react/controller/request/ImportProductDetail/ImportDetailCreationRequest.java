package backend_for_react.backend_for_react.controller.request.ImportProductDetail;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportDetailCreationRequest {
    Integer quantity;

    BigDecimal unitPrice;

    Long productVariantId;
}
