package backend_for_react.backend_for_react.controller.request.Authentication;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.validator.DobConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    @NotBlank(message = MessageError.FULLNAME_NOT_BLANK)
    String fullName;
    @NotNull(message = MessageError.GENDER_NOT_BLANK)
    Gender gender;
    @DobConstraint(min = 14 ,message = MessageError.DOB_INVALID)
    LocalDate dateOfBirth;
    @Email(message = MessageError.EMAIL_INVALID)
    @NotBlank(message = MessageError.EMAIL_NOT_BLANK)
    String email;
    @NotBlank(message = MessageError.PHONE_NOT_BLANK)
    String phone;
    @NotBlank(message = MessageError.USERNAME_NOT_BLANK)
    String username;

    @NotBlank(message = MessageError.PASSWORD_NOT_BLANK)
    String password;
}
