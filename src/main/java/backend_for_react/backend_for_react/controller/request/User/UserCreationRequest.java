package backend_for_react.backend_for_react.controller.request.User;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.validator.DobConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotBlank(message = MessageError.FULLNAME_NOT_BLANK)
    String fullName;
    @NotNull(message = MessageError.GENDER_NOT_BLANK)
    Gender gender;
    @DobConstraint(min = 14 ,message = MessageError.DOB_INVALID)
    @NotNull(message = "Birthday not null")
    LocalDate dateOfBirth;
    @Email(message = MessageError.EMAIL_INVALID)
    @NotBlank(message = MessageError.EMAIL_NOT_BLANK)
    String email;
    @NotBlank(message = MessageError.PHONE_NOT_BLANK)
    @Pattern(
            regexp = "^(0[0-9]{9}|\\+84[0-9]{9})$",
            message = "Phone number is valid"
    )
    String phone;
    @NotBlank(message = MessageError.USERNAME_NOT_BLANK)
    String username;

    @NotBlank(message = MessageError.PASSWORD_NOT_BLANK)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password;

    @NotEmpty(message = MessageError.ROLE_NOT_EMPTY)
    List<Long> roleId;

    List<Long> permissionId;
}
