package backend_for_react.backend_for_react.controller.request.Role;

import backend_for_react.backend_for_react.controller.request.Permission.PermissionCreationRequest;
import backend_for_react.backend_for_react.model.Permission;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleCreationRequest {
    @NotNull(message = "Name role not be null")
    String name;
    String description;
    Set<Long> permissions;
}
