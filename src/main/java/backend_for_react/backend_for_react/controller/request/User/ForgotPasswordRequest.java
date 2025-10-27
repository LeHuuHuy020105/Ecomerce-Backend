package backend_for_react.backend_for_react.controller.request.User;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private Long userId;
    private String password;
    private String confirmPassword;
}
