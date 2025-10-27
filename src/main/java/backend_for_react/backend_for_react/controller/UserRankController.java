package backend_for_react.backend_for_react.controller;


import backend_for_react.backend_for_react.controller.request.UserRank.UserRankCreationRequest;
import backend_for_react.backend_for_react.controller.request.UserRank.UserRankUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.service.UserRankService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/userRank")
@RequiredArgsConstructor
@Slf4j(topic = "USERRANK-CONTROLLER")
@Tag(name = "USERRANK CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRankController {
    UserRankService userRankService;

    @GetMapping("/list")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", userRankService.findAll(keyword, sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ApiResponse<Void> add(@RequestBody UserRankCreationRequest request){
        log.info("Controller create user");
        userRankService.add(request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("User added successfully")
                .build();
    }
    @PutMapping("/update")
    public ApiResponse<Void> update(@RequestBody UserRankUpdateRequest request){
        log.info("Controller update user");
        userRankService.update(request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("User updated successfully")
                .build();
    }

    @DeleteMapping("/{userRankId}/delete")
    public ApiResponse<Void> delete(@PathVariable Long userRankId){
        userRankService.delete(userRankId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("User deleted successfully")
                .build();
    }

}
