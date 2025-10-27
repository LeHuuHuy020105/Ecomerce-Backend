package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.AuthenticationResponse;
import backend_for_react.backend_for_react.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/social")
@RequiredArgsConstructor
public class SocialAuthController {
    private final OAuth2Service oAuth2Service;

    @PostMapping("/google")
    public ApiResponse<AuthenticationResponse> loginWithGoogle(@RequestBody Map<String, String> request) {
        String accessToken = request.get("accessToken");
        var result = oAuth2Service.loginWithGoogle(accessToken);
        return ApiResponse.<AuthenticationResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Login with Google")
                .data(result)
                .build();
    }
}
