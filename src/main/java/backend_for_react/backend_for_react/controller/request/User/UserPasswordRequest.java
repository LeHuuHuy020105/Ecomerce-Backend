package backend_for_react.backend_for_react.controller.request.User;

import backend_for_react.backend_for_react.exception.MessageError;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserPasswordRequest implements Serializable {
    @NotBlank(message = "password must be not blank")
    private String oldPassword;
    @NotBlank(message = MessageError.PASSWORD_NOT_BLANK)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @NotBlank(message = MessageError.PASSWORD_NOT_BLANK)
    @Size(min = 8, message = "Confirm password must be at least 8 characters long")
    private String confirmPassword;
}
