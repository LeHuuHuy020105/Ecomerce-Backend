package backend_for_react.backend_for_react.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalException {
    private static final String MIN_ATTRIBUTES = "min";
    private static final String VALIDATION_FAILED_PREFIX = "Validation failed: ";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.error("Business Exception occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Date())
                .status(ex.getCode())
                .path(request.getRequestURI())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .data(ex.getData())
                .build();
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Date())
                .status(errorCode.getHttpStatus().value())
                .path(request.getRequestURI())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .message(MessageError.UNAUTHORIZED)
                .build();
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        log.warn("Validation exception occurred");

        List<String> errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(this::processValidationError)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Date())
                .status(errorCode.getHttpStatus().value())
                .path(request.getRequestURI())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .message(VALIDATION_FAILED_PREFIX + String.join(", ", errorMessages))
                .details(errorMessages)
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        log.warn("Constraint violation exception occurred");

        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new Date())
                .status(errorCode.getHttpStatus().value())
                .path(request.getRequestURI())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .message(VALIDATION_FAILED_PREFIX + String.join(", ", errorMessages))
                .details(errorMessages)
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(Exception ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", request.getRequestURI());

        String errorMessage = ex instanceof NoHandlerFoundException ?
                "Endpoint not found: " + ((NoHandlerFoundException) ex).getRequestURL() :
                "Static resource not found: " + ex.getMessage();

        return ErrorResponse.builder()
                .timestamp(new Date())
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .error("Not Found")
                .message(errorMessage)
                .build();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.warn("File size limit exceeded");
        return ErrorResponse.builder()
                .timestamp(new Date())
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .path(request.getRequestURI())
                .error("File Too Large")
                .message("Maximum upload size exceeded")
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        return ErrorResponse.builder()
                .timestamp(new Date())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .build();
    }

    private String processValidationError(ObjectError error) {
        String message = error.getDefaultMessage();
        try {
            ConstraintViolation<?> violation = error.unwrap(ConstraintViolation.class);
            Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();
            log.debug("Validation attributes: {}", attributes);
            message = mapAttributes(message, attributes);
        } catch (Exception e) {
            log.debug("Could not unwrap ConstraintViolation", e);
        }
        return message;
    }

    private String mapAttributes(String message, Map<String, Object> attributes) {
        if (message != null && attributes != null && attributes.containsKey(MIN_ATTRIBUTES)) {
            return message.replace("{" + MIN_ATTRIBUTES + "}", String.valueOf(attributes.get(MIN_ATTRIBUTES)));
        }
        return message;
    }
}