package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.controller.request.Attribute.AttributeCreationRequest;
import backend_for_react.backend_for_react.controller.request.AttributeValue.AttributeValueCreationRequest;
import backend_for_react.backend_for_react.controller.request.Product.ProductCreationRequest;
import backend_for_react.backend_for_react.controller.request.Product.ProductUpdateRequest;
import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantCreationRequest;
import backend_for_react.backend_for_react.controller.request.VariantQuantityUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.ProductBaseResponse;
import backend_for_react.backend_for_react.model.AttributeValue;
import backend_for_react.backend_for_react.service.impl.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/list")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","product list");
        result.put("data",productService.findAll(keyword,sort,page,size));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/products/category/{id}")
    public ResponseEntity<PageResponse<ProductBaseResponse>> getByCategory(
            @PathVariable Long id,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<ProductBaseResponse> response =
                productService.findAllByCategory(id, keyword, sort, page, size);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/admin/list")
    public ResponseEntity<Object> findAllByAdmin(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(required = false) ProductStatus status,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","product list");
        result.put("data",productService.findAllByAdmin(keyword,sort,status,page,size));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PostMapping("/{productId}/restore")
    public ApiResponse<Void> restore(@PathVariable Long productId){
        productService.restoreProduct(productId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Product restored")
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<Void> createProduct(@RequestBody @Valid ProductCreationRequest req) throws IOException {
        productService.save(req);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.CREATED.value())
                .message("Product created")
                .build();
    }
    
    @PutMapping("/update")
    public ResponseEntity<String> updateProduct (@RequestBody @Valid ProductUpdateRequest req){
        productService.update(req);
        return new ResponseEntity<>("",HttpStatus.OK);
    }
    @DeleteMapping("/{productId}/delete")
    public ResponseEntity<String> deleteProduct (@PathVariable Long productId){
        productService.delete(productId);
        return new ResponseEntity<>("",HttpStatus.OK);
    }

    @GetMapping("/detail/{productId}")
    public ResponseEntity<Object> getDetailProduct (@PathVariable Long productId){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","product detail");
        result.put("data",productService.getProductById(productId));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/admin/detail/{productId}")
    public ResponseEntity<Object> getDetailProductForAdmin (@PathVariable Long productId){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","product detail");
        result.put("data",productService.getProductByIdForAdmin(productId));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PostMapping("/{productId}/variants/add")
    public ApiResponse<Void> addVariant(@PathVariable Long productId ,
                                           @RequestBody @Valid ProductVariantCreationRequest request){
        productService.addVariant(productId,request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.CREATED.value())
                .message("variant added")
                .build();
    }

    @PutMapping("/{productId}/variants/{variantId}/update")
    public ApiResponse<Void> updateVariant(@PathVariable Long productId ,
                                             @PathVariable Long variantId ,
                                             @RequestBody ProductVariantCreationRequest request){
        productService.updateVariant(productId,variantId,request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("variant updated")
                .build();
    }

    @DeleteMapping("/{productId}/variants/{variantId}/delete")
    public ApiResponse<Void> deleteVariant(@PathVariable Long productId ,
                                             @PathVariable Long variantId){
        productService.deleteVariant(productId,variantId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("variant deleted")
                .build();
    }
}
