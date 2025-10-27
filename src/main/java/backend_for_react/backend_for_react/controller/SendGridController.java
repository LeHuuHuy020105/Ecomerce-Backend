package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.service.SendGridService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Random;

@RestController
@RequestMapping("/sendgrid")
@RequiredArgsConstructor
@Slf4j(topic = "SENDGRID-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SendGridController {
    SendGridService sendGridService;

    @PostMapping("/otp")
    public ApiResponse<Void> sendotp(@RequestParam String to) throws IOException {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(999999));
        sendGridService.emailWithOTP(to,"Huy",otp);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("OTP sent")
                .build();
    }

    @PostMapping("/send")
    public ApiResponse<Void> send(@RequestParam String to, @RequestParam String message) throws IOException {
        sendGridService.sendEmail(to,"Huy",message);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Text sent")
                .build();
    }
}
