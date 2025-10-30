package backend_for_react.backend_for_react.controller;


import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantCreationRequest;
import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.model.ProductVariant;
import backend_for_react.backend_for_react.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productVariant")
@RequiredArgsConstructor
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @PostMapping("/add")
    public ApiResponse<Void> add(@RequestBody ProductVariantCreationRequest request) {
        productVariantService.add(request);
        return ApiResponse.<Void>builder()
                .message("Successfully added product variant.")
                .status(HttpStatus.OK.value())
                .build();
    }

    @PutMapping("update")
    public ApiResponse<Void> update(@RequestBody ProductVariantUpdateRequest request) {
        productVariantService.update(request);
        return ApiResponse.<Void>builder()
                .message("Successfully updated product variant.")
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/delete/{productVariantId}")
    public ApiResponse<Void> delete(@PathVariable Long productVariantId) {
        productVariantService.delete(productVariantId);
        return ApiResponse.<Void>builder()
                .message("Successfully deleted product variant.")
                .status(HttpStatus.OK.value())
                .build();
    }
}
