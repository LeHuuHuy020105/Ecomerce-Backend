package backend_for_react.backend_for_react.config.auth;

import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.ErrorResponse;
import backend_for_react.backend_for_react.exception.MessageError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Date;
@Slf4j
public class JwtAuthenticaionEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Date())
                .status(errorCode.getHttpStatus().value())
                .path(request.getRequestURI())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .message(MessageError.UNAUTHENTICATED)
                .build();
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // tra du lieu ve gi , o day tra ve JSON

        ObjectMapper objectMapper = new ObjectMapper(); //convert object thanh 1 json

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse)); // can truyen String , truyen noi dung body muon truyen dat
        response.flushBuffer(); // tra ve
    }
}
