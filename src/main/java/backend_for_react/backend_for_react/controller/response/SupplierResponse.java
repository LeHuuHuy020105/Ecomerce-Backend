package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {
    Long id;

    String name;

    String address;

    String phone;

    Status status;
}
