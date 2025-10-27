package backend_for_react.backend_for_react.mapper;

import backend_for_react.backend_for_react.controller.response.PermissionResponse;
import backend_for_react.backend_for_react.model.Permission;

public class PermissionMapper {
    public static PermissionResponse getPermissionResponse (Permission permission){
        return PermissionResponse.builder()
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
