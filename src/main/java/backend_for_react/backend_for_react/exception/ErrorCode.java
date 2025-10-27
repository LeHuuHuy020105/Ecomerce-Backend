package backend_for_react.backend_for_react.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, HttpStatus.BAD_REQUEST),
    EXISTED(1002, HttpStatus.BAD_REQUEST),
    NOT_EXISTED(1005, HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,  HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, HttpStatus.FORBIDDEN),
    BAD_REQUEST(111, HttpStatus.BAD_REQUEST),
    INVALID_OPERATION(112,HttpStatus.BAD_REQUEST),
    DUPLICATE(113,HttpStatus.CONFLICT),
    NOT_VERIFY(1000, HttpStatus.BAD_REQUEST)

    ;

    private final int code;
    private final HttpStatus httpStatus;

    ErrorCode(int code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
