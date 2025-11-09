package backend_for_react.backend_for_react.mapper;

import backend_for_react.backend_for_react.controller.response.PermissionResponse;
import backend_for_react.backend_for_react.controller.response.RoleResponse;
import backend_for_react.backend_for_react.model.Role;

import java.util.HashSet;
import java.util.Set;

public class RoleMapper {
    public static RoleResponse getRoleResponse(Role role){
        Set<PermissionResponse> permissionResponses = new HashSet<>(role.getPermissions().stream().map(PermissionMapper::getPermissionResponse).toList());
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissionResponses)
                .build();
    }
}
