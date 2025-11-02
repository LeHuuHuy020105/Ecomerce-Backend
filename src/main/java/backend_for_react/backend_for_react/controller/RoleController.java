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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Slf4j(topic = "ROLE-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @GetMapping("/list")
    public ResponseEntity<Object> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "role list");
        result.put("data", roleService.findAll(keyword, sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
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
