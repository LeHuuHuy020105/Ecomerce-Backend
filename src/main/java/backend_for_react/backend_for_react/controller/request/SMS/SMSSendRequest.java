package backend_for_react.backend_for_react.controller.request.SMS;

import lombok.Data;

@Data
public class SMSSendRequest {
    private String destinationSMSNumber;
    private String message;
}
