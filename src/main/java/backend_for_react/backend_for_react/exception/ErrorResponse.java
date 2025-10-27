package backend_for_react.backend_for_react.exception;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private String path;
    private String error;
    private String message;
    private List<String> details;
    private Object data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class VerifyAccountData {
        private Long userId;
        private String email;
    }
}
