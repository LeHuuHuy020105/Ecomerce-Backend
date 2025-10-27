package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.Role.RoleCreationRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.RoleResponse;
import backend_for_react.backend_for_react.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Slf4j(topic = "ROLE-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @GetMapping("/list")
    public ApiResponse<List<RoleResponse>> findAll(){
        List<RoleResponse> result = roleService.findAll();
        return ApiResponse.<List<RoleResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Role list")
                .data(result)
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<Long> createRole(@RequestBody RoleCreationRequest req){
        Long roleId = roleService.save(req);
        return ApiResponse.<Long>builder()
                .status(HttpStatus.OK.value())
                .message("Create role")
                .data(roleId)
                .build();
    }

    @DeleteMapping("/{roleId}")
    public void delete(@PathVariable Long roleId){
        roleService.delete(roleId);
    }
}
