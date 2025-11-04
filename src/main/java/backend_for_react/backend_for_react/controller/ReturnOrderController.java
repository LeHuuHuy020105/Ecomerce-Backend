package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.controller.request.ReturnOrder.ReturnOrderCreationRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.service.ReturnOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/return_order")
@RequiredArgsConstructor
public class ReturnOrderController {
    private final ReturnOrderService returnOrderService;

    @PostMapping("/add")
    public ApiResponse<Void> add(@RequestBody @Valid ReturnOrderCreationRequest request){
        returnOrderService.save(request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.CREATED.value())
                .message("Successfully added new returnOrder")
                .build();
    }

    @PutMapping("/changeStatus/{returnOrderId}")
    public ApiResponse<Void> changeStatus(@PathVariable Long returnOrderId , @RequestParam ReturnStatus returnStatus){
        returnOrderService.changeStatus(returnOrderId, returnStatus);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Successfully changed status of returnOrder")
                .build();
    }

    @GetMapping("/list")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", returnOrderService.findAll(sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{returnOrderId}")
    public ApiResponse<Void> cancel(@PathVariable Long returnOrderId){
        returnOrderService.cancelReturnOrder(returnOrderId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Successfully cancelled returnOrder")
                .build();
    }
}
