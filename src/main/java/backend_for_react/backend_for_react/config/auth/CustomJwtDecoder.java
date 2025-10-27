package backend_for_react.backend_for_react.config.auth;

import backend_for_react.backend_for_react.controller.request.Authentication.IntrospectRequest;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import com.nimbusds.jose.JOSEException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;

@Slf4j
@Component
@Primary // để Spring ưu tiên dùng decoder này
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {

    private final AuthenticationService authenticationService;

    @Value("${jwt.signerKey}")
    private String signerKey;

    private JwtDecoder delegate;

    @PostConstruct
    public void init() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");
        this.delegate = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) throws BusinessException {

        try {
            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build());
            log.info("response : {} ", response);
            if (!response.isValid()) {
                throw new BusinessException(ErrorCode.UNAUTHENTICATED , MessageError.TOKEN_INVALID);
            }
            return delegate.decode(token);
        } catch (JOSEException | ParseException e) {
            log.error("JWT decode error: ", e);
            throw new JwtException("Token decode failed", e);
        }
    }
}
