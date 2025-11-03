package backend_for_react.backend_for_react.controller.request.Supplier;

import backend_for_react.backend_for_react.exception.MessageError;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^(0[0-9]{9}|\\+84[0-9]{9})$",
            message = "Phone number is valid"
    )
    String phone;
}
