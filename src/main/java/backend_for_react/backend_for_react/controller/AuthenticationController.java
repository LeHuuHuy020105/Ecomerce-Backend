package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.Authentication.*;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.AuthenticationResponse;
import backend_for_react.backend_for_react.controller.response.IntrospectResponse;
import backend_for_react.backend_for_react.config.auth.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest req,
                                          HttpServletResponse response) {
        AuthenticationResponse result = authenticationService.authication(req);
        return result;
    }

    @PostMapping("/register")
    public ApiResponse<Long> register(@RequestBody RegisterRequest req) throws IOException {
        Long result = authenticationService.register(req);
        return ApiResponse.<Long>builder()
                .status(HttpStatus.CREATED.value())
                .message("Registered successfully")
                .data(result)
                .build();
    }


    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest req,
                                    HttpServletResponse response) throws ParseException, JOSEException {
        authenticationService.logout(req);

        // Xóa cookie bằng cách tạo lại cookie cùng tên, tuổi thọ 0
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);

        return ApiResponse.<Void>builder()
                .message("Đăng xuất thành công")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest req) throws ParseException, JOSEException {
        AuthenticationResponse result = authenticationService.refreshToken(req);
        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .build();
    }

    @PostMapping("/introspect")
    public IntrospectResponse introspect(@RequestBody IntrospectRequest req) throws ParseException, JOSEException {
        IntrospectResponse result = authenticationService.introspect(req);
        return result;
    }
}
