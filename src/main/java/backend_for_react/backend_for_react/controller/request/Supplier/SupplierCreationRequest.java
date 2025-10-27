package backend_for_react.backend_for_react.controller.request.Supplier;

import backend_for_react.backend_for_react.exception.MessageError;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierCreationRequest {
    @NotBlank(message = MessageError.FULLNAME_NOT_BLANK)
    String name;

    @NotBlank(message = MessageError.ADDRESS_NOT_BLANK)
    String address;

    @NotBlank(message = MessageError.PHONE_NOT_BLANK)
    String phone;
}
