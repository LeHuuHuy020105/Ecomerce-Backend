package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.OTPType;
import backend_for_react.backend_for_react.common.enums.VerificationMethod;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SEND-GRID")
public class OTPService {

    private final RedisTemplate redisTemplate;
    private final SendGridService sendGridService;

    @Value("${spring.sendgrid.otp-valid-minutes}")
    private int otpValidMinutes;

    public void sendOTP(User user , OTPType otpType ) throws IOException {
        String redisKey = "otp:"+ otpType.name().toLowerCase() +user.getId();
        String code = generateCode();
        redisTemplate.opsForValue().set(redisKey,code,otpValidMinutes, TimeUnit.MINUTES);
        sendGridService.emailWithOTP(user.getEmail(), user.getFullName(), code);
    }


    public String verifyOTP(User user, OTPType otpType, String inputOtp) {
        String redisKey = "otp:" + otpType.name().toLowerCase() + user.getId();

        String cachedOtp = (String) redisTemplate.opsForValue().get(redisKey);

        if (cachedOtp == null) {
            throw new BusinessException (ErrorCode.BAD_REQUEST,"OTP expired or not found");
        }

        if (!cachedOtp.equals(inputOtp)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Invalid OTP");
        }

        // Xóa OTP sau khi dùng thành công
        redisTemplate.delete(redisKey);

        log.info("OTP verified successfully for user {}", user.getId());
        return generateResetToken(user);
    }

    public boolean verifyResetToken(Long userId , String resetToken) {
        String redisKey = "reset-token:" + userId;
        String cachedToken = (String) redisTemplate.opsForValue().get(redisKey);

        if (cachedToken == null || !cachedToken.equals(resetToken)) {
            throw new BusinessException(ErrorCode.UNAUTHENTICATED, "Invalid or expired reset token");
        }

        redisTemplate.delete(redisKey);
        return true;
    }
    private String generateResetToken(User user) {
        String resetToken = UUID.randomUUID().toString();
        String redisKey = "reset-token:" + user.getId();
        redisTemplate.opsForValue().set(redisKey, resetToken, 15, TimeUnit.MINUTES); // TTL 15 phút
        return resetToken;
    }


    public String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999)); // OTP 6 chữ số
    }
}

