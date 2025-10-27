package backend_for_react.backend_for_react.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;
    private final HttpStatus status;
    private Object data;

    public BusinessException(ErrorCode errorCode, String message , Object data) {
        super(message);
        this.code = errorCode.getCode();
        this.status = errorCode.getHttpStatus();
        this.data = data;
    }
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.status = errorCode.getHttpStatus();
    }
}
