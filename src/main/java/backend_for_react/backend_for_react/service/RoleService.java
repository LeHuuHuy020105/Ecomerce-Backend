package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.enums.VoucherStatus;
import backend_for_react.backend_for_react.controller.request.Role.RoleCreationRequest;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.PermissionResponse;
import backend_for_react.backend_for_react.controller.response.RoleResponse;
import backend_for_react.backend_for_react.controller.response.VoucherResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.RoleMapper;
import backend_for_react.backend_for_react.mapper.VoucherMapper;
import backend_for_react.backend_for_react.model.Permission;
import backend_for_react.backend_for_react.model.Role;
import backend_for_react.backend_for_react.model.Voucher;
import backend_for_react.backend_for_react.repository.PermissionRepository;
import backend_for_react.backend_for_react.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "ROLE-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    PermissionService permissionService;


    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_ALL_ROLE')")
    public PageResponse<VoucherResponse> findAll(String keyword, String sort, int page, int size){
        log.info("Find all vouchers ");

        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); //tencot:asc||desc
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = Sort.by(Sort.Direction.ASC, columnName);
                } else {
                    order = Sort.by(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<Role> roles = null;
        if (keyword == null || keyword.isEmpty()) {
            roles = roleRepository.findAll(pageable);
        } else {
            keyword = "%" + keyword.toLowerCase() + "%";
            roles = roleRepository.searchByKeyword(keyword, pageable);
        }
        PageResponse response = getVoucherPageResponse(pageNo, size, roles);
        return response;
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_ROLE')")
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

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_ROLE')")
    public void delete(Long id){
        Role role = roleRepository.findByIdAndStatus(id,Status.ACTIVE).orElseThrow(()-> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.ROLE_NOT_FOUND));
        role.setStatus(Status.INACTIVE);
        roleRepository.save(role);
    }

    private PageResponse<RoleResponse> getVoucherPageResponse(int page, int size, Page<Role> roles) {
        List<RoleResponse> roleResponses = roles.stream()
                .map(RoleMapper::getRoleResponse)
                .toList();

        PageResponse<RoleResponse> response = new PageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(roles.getTotalElements());
        response.setTotalPages(roles.getTotalPages());
        response.setData(roleResponses);
        return response;
    }
}
