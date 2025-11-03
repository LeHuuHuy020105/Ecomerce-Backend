package backend_for_react.backend_for_react.controller;


import backend_for_react.backend_for_react.controller.request.Attribute.AttributeCreationRequest;
import backend_for_react.backend_for_react.controller.request.Attribute.AttributeUpdateRequest;
import backend_for_react.backend_for_react.controller.request.AttributeValue.AttributeValueCreationRequest;
import backend_for_react.backend_for_react.controller.request.AttributeValue.AttributeValueUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.service.AttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attribute")
@RequiredArgsConstructor
public class AttributeController {
    private final  AttributeService attributeService;

    @PostMapping("/add")
    public ApiResponse<Void>addAttribute(@RequestBody @Valid AttributeCreationRequest request , @RequestParam Long productId) {
        attributeService.add(request, productId);
        return ApiResponse.<Void>builder()
                .message("Successfully added attribute")
                .status(HttpStatus.CREATED.value())
                .build();
    }

    @PutMapping("/update/{attributeId}")
    public ApiResponse<Void>updateAttribute(@RequestBody @Valid AttributeUpdateRequest request ,@PathVariable Long attributeId, @RequestParam Long productId) {
        attributeService.update(productId,attributeId,request);
        return ApiResponse.<Void>builder()
                .message("Successfully updated attribute")
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/delete/{attributeId}")
    public ApiResponse<Void>deleteAttribute(@PathVariable Long attributeId, @RequestParam Long productId) {
        attributeService.delete(productId,attributeId);
        return ApiResponse.<Void>builder()
                .message("Successfully deleted attribute")
                .status(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/attributeValue/add")
    public ApiResponse<Void>addAttributeValue(@RequestBody @Valid AttributeValueCreationRequest request , @RequestParam Long productId , @RequestParam Long attributeId) {
        attributeService.addAttributeValue(productId,attributeId,request);
        return ApiResponse.<Void>builder()
                .message("Successfully added attribute value")
                .status(HttpStatus.CREATED.value())
                .build();
    }

    @PutMapping("/attributeValue/update/{attributeValueId}")
    public ApiResponse<Void>updateAttributeValue(@RequestBody @Valid AttributeValueUpdateRequest request ,
                                                 @PathVariable Long attributeValueId,
                                                 @RequestParam Long productId) {
        attributeService.updateAttributeValue(productId,attributeValueId,request);
        return ApiResponse.<Void>builder()
                .message("Successfully updated attribute value")
                .status(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/attributeValue/delete/{attributeValueId}")
    public ApiResponse<Void>deleteAttributeValue(@PathVariable Long attributeValueId, @RequestParam Long productId) {
        attributeService.deleteAttributeValue(productId,attributeValueId);
        return ApiResponse.<Void>builder()
                .message("Successfully deleted attribute")
                .status(HttpStatus.OK.value())
                .build();
    }
    @DeleteMapping("/attributeValue/deleteImage/{attributeValueId}")
    public ApiResponse<Void>deleteImageAttributeValue(@PathVariable Long attributeValueId, @RequestParam Long productId) {
        attributeService.deleteImageAttributeValue(productId,attributeValueId);
        return ApiResponse.<Void>builder()
                .message("Successfully deleted attribute value image")
                .status(HttpStatus.OK.value())
                .build();
    }

}
