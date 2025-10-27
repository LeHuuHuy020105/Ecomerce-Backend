package backend_for_react.backend_for_react.controller.request.User;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.validator.DobConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest implements Serializable {
    String fullName;
    Gender gender;
    @DobConstraint(min = 14 ,message = MessageError.DOB_INVALID)
    LocalDate dateOfBirth;
    @Pattern(
            regexp = "^(0[0-9]{9}|\\+84[0-9]{9})$",
            message = "Phone number is valid"
    )
    String phone;
    String avatar;
}
