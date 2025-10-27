package backend_for_react.backend_for_react.controller.request.Supplier;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierUpdateRequest {
    Long id;

    String name;

    String address;

    String phone;
}
