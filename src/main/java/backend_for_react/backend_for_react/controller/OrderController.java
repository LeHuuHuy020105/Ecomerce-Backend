package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.controller.request.Order.OrderCreationRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.OrderResponse;
import backend_for_react.backend_for_react.service.impl.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/list")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(required = true) boolean isAll,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) DeliveryStatus deliveryStatus
    ){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","Order list");
        result.put("data",orderService.findAllByUser(keyword,sort,page,size,isAll,deliveryStatus));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/admin/list")
    public ResponseEntity<Object> findAllByAdmin(@RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String sort,
                                                 @RequestParam(required = true) boolean isAll,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(required = false) DeliveryStatus deliveryStatus,
                                                 @RequestParam(required = false) LocalDateTime startDate,
                                                 @RequestParam(required = false) LocalDateTime endDate

                                                 ){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","order list");
        result.put("data",orderService.findAllByAdmin(keyword,isAll,deliveryStatus,sort,page,size,startDate,endDate));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }


    @PostMapping("/add")
    public ResponseEntity<Object> createOrder(@RequestBody @Valid OrderCreationRequest req){
        Long orderId = orderService.save(req);
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message","order created successfull");
        result.put("data",orderId);
        return new ResponseEntity<>(result,HttpStatus.CREATED);
    }
    @PostMapping("/changestatus/{orderId}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long orderId , @RequestParam DeliveryStatus status){
        orderService.changeStatus(orderId,status);
        return new ResponseEntity<>("",HttpStatus.CREATED);
    }

    @PutMapping("/complete/{orderId}")
    public ApiResponse<Void> completeOrder(@PathVariable Long orderId){
        orderService.completeOrder(orderId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Order completed successfully")
                .build();
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long orderId){
        OrderResponse orderResponse = orderService.getOrderById(orderId);
        return new ApiResponse<OrderResponse>(HttpStatus.OK.value(), "Get order detail" , orderResponse);
    }

    @GetMapping("/admin/{orderId}")
    public ApiResponse<OrderResponse> getOrderByIdForAdmin(@PathVariable Long orderId){
        OrderResponse orderResponse = orderService.getOrderByIdForAdmin(orderId);
        return new ApiResponse<OrderResponse>(HttpStatus.OK.value(), "Get order detail" , orderResponse);
    }

    @DeleteMapping("/cancel")
    public ApiResponse<Void> cancelOrder(@RequestParam Long orderId){
        orderService.cancelOrder(orderId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Order cancelled successfully")
                .build();
    }

}
