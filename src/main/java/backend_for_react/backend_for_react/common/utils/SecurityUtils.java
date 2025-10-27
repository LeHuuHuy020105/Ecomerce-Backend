package backend_for_react.backend_for_react.common.utils;

import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;
    public User getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String username = jwt.getSubject();
            if(username != null){
                return userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
            }else {
                String email = jwt.getClaimAsString("email");
                return  userRepository.findByProviderAndEmail("google",email)
                        .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST , MessageError.USER_NOT_FOUND));
            }
        }
        return null;
    }
}
