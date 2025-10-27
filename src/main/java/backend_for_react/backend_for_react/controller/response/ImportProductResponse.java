package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportProductResponse {
    Long id;
    String importCode;
    String description;
    BigDecimal totalAmount;
    DeliveryStatus status;
    SupplierResponse supplierResponse;
    List<ImportDetailResponse> importDetailResponses;
}
