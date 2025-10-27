package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectResponse {
    boolean valid;
    String fullName;
    String avatar;
    String email;
    Gender gender;
    UserStatus status;
    List<String> roles;
}
