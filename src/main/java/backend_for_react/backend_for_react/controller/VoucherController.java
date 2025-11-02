package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.Voucher.VoucherCreationRequest;
import backend_for_react.backend_for_react.controller.request.Voucher.VoucherUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.VoucherResponse;
import backend_for_react.backend_for_react.model.Voucher;
import backend_for_react.backend_for_react.service.VoucherService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/voucher")
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@Tag(name = "USER CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherController {
    VoucherService voucherService;

    @GetMapping("/listForMe")
    public ResponseEntity<Object> findAllForMe() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", voucherService.getAvailableVouchersForUser());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

//    @GetMapping("/list")
//    public ResponseEntity<Object> findAll(@RequestParam(required = false) String keyword,
//                                          @RequestParam(required = false) String sort,
//                                          @RequestParam(defaultValue = "0") int page,
//                                          @RequestParam(defaultValue = "10") int size) {
//        Map<String, Object> result = new LinkedHashMap<>();
//        result.put("status", HttpStatus.OK.value());
//        result.put("message", "voucher list");
//        result.put("data", voucherService.findAll(keyword, sort, page, size));
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

    @GetMapping("/admin/list")
    public ResponseEntity<Object> findAllByAdmin(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(required = false) String rank,
                                          @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", voucherService.findAllByAdmin(keyword, rank, sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ApiResponse<Void> add(@RequestBody @Valid VoucherCreationRequest request) {
        voucherService.add(request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Successfully added voucher")
                .build();
    }

    @PutMapping("/update")
    public ApiResponse<Void> update(@RequestBody VoucherUpdateRequest request) {
        voucherService.update(request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Successfully updated voucher")
                .build();
    }

    @DeleteMapping("/{voucherId}/delete")
    public ApiResponse<Void> delete(@RequestParam Long voucherId) {
        voucherService.delete(voucherId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Successfully deleted voucher")
                .build();
    }

    @GetMapping("/{voucherId}")
    public ApiResponse<VoucherResponse> getDetailVoucher(@PathVariable Long voucherId) {
        var result = voucherService.getVoucherById(voucherId);
        return ApiResponse.<VoucherResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Successfully get detail voucher")
                .data(result)
                .build();
    }

}
