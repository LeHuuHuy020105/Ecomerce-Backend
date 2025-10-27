package backend_for_react.backend_for_react.controller.request.ImportProduct;

import backend_for_react.backend_for_react.controller.request.ImportProductDetail.ImportDetailCreationRequest;
import backend_for_react.backend_for_react.model.ImportDetail;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportProductCreationRequest {
    String description;
    BigDecimal totalAmount;
    Long supplierId;
    String importCode;
    List<ImportDetailCreationRequest> importDetails;
}
