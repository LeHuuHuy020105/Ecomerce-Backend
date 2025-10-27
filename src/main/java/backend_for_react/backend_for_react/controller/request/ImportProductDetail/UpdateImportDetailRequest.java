package backend_for_react.backend_for_react.controller.request.ImportProductDetail;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateImportDetailRequest {
    Long importDetailId;
    Integer quantity;
}
