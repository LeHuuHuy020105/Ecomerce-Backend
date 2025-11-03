package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.OTP.OTPRequest;
import backend_for_react.backend_for_react.controller.request.User.*;
import backend_for_react.backend_for_react.controller.response.AddressResponse;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.UserResponse;
import backend_for_react.backend_for_react.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@Tag(name = "USER CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @Operation(summary = "List user" , description = "API view list user")
    @GetMapping("/list")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", userService.findAll(keyword, sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Get detail user" , description = "API view detail user")
    @GetMapping("/{userId}")
    public ApiResponse<Object> getDetailUser(@PathVariable Long userId) {
        UserResponse result = userService.getUserById(userId);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Get detail user")
                .data(result)
                .build();
    }

    @Operation(summary = "Get my profile" , description = "API view my profile")
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {
        UserResponse result = userService.getMyInfo();
        return ApiResponse.<UserResponse>builder()
                .data(result)
                .build();
    }

    @Operation(summary = "Add user" , description = "API create user")
    @PostMapping("/add")
    public ApiResponse<Object> createUser(@RequestBody @Valid UserCreationRequest req) throws IOException {
        log.info("Controller create user");
        Long userId = userService.save(req);
        return new ApiResponse<Object>(HttpStatus.OK.value(), "create user succesfull", userId);
    }

//    @Operation(summary = "Set avatar user" , description = "API set avatar user")
//    @PostMapping("/{userId}/avatar")
//    public ResponseEntity<String> uploadAvatar(@PathVariable Long userId,
//                                               @RequestParam("avater") String url) throws IOException {
//        userService.updateUserAvatar(userId, url);
//        return new ResponseEntity<>("", HttpStatus.OK);
//    }


    @PostMapping("/add/address")
    public ResponseEntity<String> createAddress(@RequestBody @Valid UserCreationAddressRequest req) {
        userService.addAddress(req);
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @GetMapping("/address/list")
    public ApiResponse<PageResponse<AddressResponse>> getAllAddresses(
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<AddressResponse> result = userService.findAllAddressUser(sort, page, size);
        return ApiResponse.<PageResponse<AddressResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Get addresses succesfull")
                .data(result)
                .build();
    }

    @PutMapping("/address/default/{userHasAddressId}")
    public ApiResponse<Void> updateDefaultAddress(@PathVariable Long userHasAddressId) {
        userService.setDefaultAddress(userHasAddressId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Updated default address succesfull")
                .build();
    }

    @PutMapping("/address/update/{userHasAddressId}")
    public ApiResponse<Void> updateAddress(@PathVariable Long userHasAddressId, @RequestBody UserCreationAddressRequest request) {
        userService.updateAddress(userHasAddressId,request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Updated address succesfull")
                .build();
    }

    @DeleteMapping("/address/delete/{userHasAddressId}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long userHasAddressId) {
        userService.deleteAddress(userHasAddressId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Deleted address succesfull")
                .build();
    }



    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody @Valid UserUpdateRequest req) {
        userService.update(req);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping("/verify-account")
    public ApiResponse<Void> verifyAccount(@RequestParam Long userId , @RequestParam String resetToken) {
        userService.verifyAccount(userId , resetToken);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Verify account")
                .build();
    }

    @PostMapping("/change-email")
    public ApiResponse<Void> changeEmail(@RequestParam String newEmail, @RequestParam String resetToken) {
        userService.changeEmail(newEmail, resetToken);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Verify account")
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody @Valid UserPasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Change password")
                .build();
    }

    @GetMapping("/email")
    public ApiResponse<List<UserResponse>> getUserByEmail(@RequestParam String email) {
        var result = userService.getAllUserByEmail(email);
        return  ApiResponse.<List<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Change password")
                .data(result)
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request, @RequestParam String resetToken) {
        userService.forgotPassword(request, resetToken);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Forgot password")
                .build();
    }

    @GetMapping("/listAllByEmail")
    public ApiResponse<List<UserResponse>> getAllByEmail(@RequestParam String email) {
        List<UserResponse> result = userService.getAllUserByEmail(email);
        return ApiResponse.<List<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("List of users by email")
                .data(result)
                .build();
    }

}
