package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.Cart.CartCreationRequest;
import backend_for_react.backend_for_react.controller.request.Cart.CartUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.CartResponse;
import backend_for_react.backend_for_react.controller.response.ProductBaseResponse;
import backend_for_react.backend_for_react.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j(topic = "CART-CONTROLLER")
@Tag(name = "CART CONTROLLER")
public class CartController {
    private final CartService cartService;

    @GetMapping("/listForMe")
    public ResponseEntity<Object> getCartsForMe(@RequestParam(required = false) String sort,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "cart list");
        result.put("data", cartService.getCarts(sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ApiResponse<Void> add(@RequestBody CartCreationRequest request){
        cartService.add(request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Add cart successfully")
                .build();
    }


    @PutMapping("/{cartId}/update")
    public ApiResponse<Void> update(@RequestBody CartUpdateRequest request , @PathVariable Long cartId){
        cartService.update(cartId,request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Update cart successfully")
                .build();
    }

    @DeleteMapping("/{cartId}/delete")
    public ApiResponse<Void> delete(@PathVariable Long cartId){
        cartService.delete(cartId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Delete cart successfully")
                .build();
    }

}
