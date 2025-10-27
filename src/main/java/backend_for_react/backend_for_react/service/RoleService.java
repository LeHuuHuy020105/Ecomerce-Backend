package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.Role.RoleCreationRequest;
import backend_for_react.backend_for_react.controller.response.PermissionResponse;
import backend_for_react.backend_for_react.controller.response.RoleResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.RoleMapper;
import backend_for_react.backend_for_react.model.Permission;
import backend_for_react.backend_for_react.model.Role;
import backend_for_react.backend_for_react.repository.PermissionRepository;
import backend_for_react.backend_for_react.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j(topic = "ROLE-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    PermissionService permissionService;

    public Long save(RoleCreationRequest req){
        log.info("Save role");
        Role role = new Role();
        role.setName(req.getName());
        role.setStatus(Status.ACTIVE);
        role.setDescription(req.getDescription());

        List<Permission> permissions = permissionRepository.findAllByStatusActive(req.getPermissions() , Status.ACTIVE);
        System.out.println(permissions);
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        return role.getId();
    }
    public List<RoleResponse> findAll(){
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(RoleMapper::getRoleResponse).toList();
    }

    public void delete(Long id){
        Role role = roleRepository.findById(id).orElseThrow(()-> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.ROLE_NOT_FOUND));
        role.setStatus(Status.INACTIVE);
        roleRepository.save(role);
    }
}
