package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.Permission.PermissionCreationRequest;
import backend_for_react.backend_for_react.controller.response.PermissionResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.PermissionMapper;
import backend_for_react.backend_for_react.model.Permission;
import backend_for_react.backend_for_react.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Slf4j(topic = "PERMISSION-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;

    public List<PermissionResponse> findAll(){
        List<Permission> permissions = permissionRepository.findAllByStatus(Status.ACTIVE);
        return permissions.stream().map(PermissionMapper::getPermissionResponse).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Long save (PermissionCreationRequest req){
        Permission permission = new Permission();
        permission.setDescription(req.getDescription());
        permission.setName(req.getName());
        permissionRepository.save(permission);
        return permission.getId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id){
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(()->new BusinessException(ErrorCode.NOT_EXISTED, MessageError.PERMISSION_NOT_FOUND));
        permission.setStatus(Status.INACTIVE);
        permissionRepository.save(permission);
    }

}
