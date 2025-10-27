package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.common.enums.OTPType;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.repository.UserRepository;
import backend_for_react.backend_for_react.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OTPController {
    private final OTPService otpService;
    private final UserRepository userRepository;

    @PostMapping("/send")
        public ApiResponse<Long> sendOTP(@RequestParam Long userId , @RequestParam OTPType otpType) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.USER_NOT_FOUND));
        otpService.sendOTP(user,otpType);
        return ApiResponse.<Long>builder()
                .status(HttpStatus.OK.value())
                .message("OTP sent ")
                .data(userId)
                .build();
    }

        @PostMapping("/verify-otp")
        public ApiResponse<String> verifyOTP(@RequestParam Long userId , @RequestParam String inputOtp, @RequestParam OTPType otpType) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.USER_NOT_FOUND));
        String result = otpService.verifyOTP(user,otpType,inputOtp);
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Verify OTP verification")
                .data(result)
                .build();
    }
}
