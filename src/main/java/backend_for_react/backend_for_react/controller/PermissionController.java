package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.Permission.PermissionCreationRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.PermissionResponse;
import backend_for_react.backend_for_react.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@Slf4j(topic = "PERMISSION-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @GetMapping("/list")
    public ApiResponse<List<PermissionResponse>> findAll(){
        List<PermissionResponse> result = permissionService.findAll();
        return ApiResponse.<List<PermissionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Permission list")
                .data(result)
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<Long> create(@RequestBody PermissionCreationRequest req){
        Long permissionId = permissionService.save(req);
        return ApiResponse.<Long>builder()
                .status(HttpStatus.OK.value())
                .message("Create permission")
                .data(permissionId)
                .build();
    }

    @DeleteMapping("/{permissionId}")
    public void delete(@PathVariable Long permissionId){
        permissionService.delete(permissionId);
    }
}
