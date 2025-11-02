package backend_for_react.backend_for_react.controller;

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
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","product list");
        result.put("data",productService.findAllByAdmin(keyword,sort,page,size));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }



    @PostMapping("/add")
    public ApiResponse<Void> createProduct(@RequestBody @Valid ProductCreationRequest req) throws IOException {
        productService.save(req);
        return ApiResponse.<Void>builder().build();
    }
    @PutMapping("/{productId}/update/quantity")
    public ResponseEntity<String>updateProductQuantity(@RequestBody List<VariantQuantityUpdateRequest> req , @PathVariable Long productId) {
        productService.updateVariantQuantity(req,productId);
        return new ResponseEntity<>("",HttpStatus.OK);
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

    @PutMapping("/{productId}/attributes/{attributeId}/update")
    public ApiResponse<Void> updateAttribute(@PathVariable Long productId ,
                                             @PathVariable Long attributeId ,
                                             @RequestBody AttributeCreationRequest request){
        productService.updateAttribute(productId,attributeId,request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("attribute updated")
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

    @PutMapping("/{productId}/attributeValue/{attributeValueId}/update")
    public ApiResponse<Void> updateAttributeValue(@PathVariable Long productId ,
                                             @PathVariable Long attributeValueId ,
                                             @RequestBody AttributeValueCreationRequest request){
        productService.updateAttributeValue(productId,attributeValueId,request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("attribute value updated")
                .build();
    }

    @DeleteMapping("/{productId}/attributes/{attributeId}/delete")
    public ApiResponse<Void> deleteAttribute(@PathVariable Long productId ,
                                             @PathVariable Long attributeId){
        productService.deleteAttribute(productId,attributeId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("attribute deleted")
                .build();
    }

    @DeleteMapping("/{productId}/attributeValues/{attributeValueId}/delete")
    public ApiResponse<Void> deleteAttributeValue(@PathVariable Long productId ,
                                             @PathVariable Long attributeValueId){
        productService.deleteAttributeValue(productId,attributeValueId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("attribute value deleted")
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
