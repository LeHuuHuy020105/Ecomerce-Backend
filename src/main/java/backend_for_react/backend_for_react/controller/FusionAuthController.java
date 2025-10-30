package backend_for_react.backend_for_react.controller;


import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.repository.UserRepository;
import backend_for_react.backend_for_react.service.FusionAuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fushionAuth")
@RequiredArgsConstructor
@Slf4j(topic = "FUSHIONAUTH-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FusionAuthController {
    FusionAuthService fusionAuthService;
    UserRepository userRepository;

    @PostMapping("/sendOTP")
    public ApiResponse<Boolean> sendOTP(){
        User user = userRepository.findById(1L).orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.USER_NOT_FOUND));
        Boolean result = fusionAuthService.sendOTP("123",user);
        return ApiResponse.<Boolean>builder()
                .message("OTP send")
                .data(result)
                .build();
    }
}
