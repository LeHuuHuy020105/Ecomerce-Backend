package backend_for_react.backend_for_react.controller.request.OTP;

import lombok.Data;

@Data
public class OTPRequest {
    private Long userId;
    private String code;
}
