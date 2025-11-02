package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.controller.request.ImportProduct.ImportProductCreationRequest;
import backend_for_react.backend_for_react.controller.request.ImportProductDetail.UpdateImportDetailRequest;
import backend_for_react.backend_for_react.controller.request.User.UserCreationRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.ImportProductResponse;
import backend_for_react.backend_for_react.service.ImportProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/import_product")
@RequiredArgsConstructor
@Slf4j(topic = "IMPORT - PRODUCT -CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportProductController {
    ImportProductService importProductService;

    @GetMapping("/list")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(required = false) Long supplierId,
                                          @RequestParam(required = false) DeliveryStatus deliveryStatus,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                          @RequestParam(required = false) String timeRange, // Nhận giá trị "week", "month", "quarter"
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","category list");
        result.put("data",importProductService.findAll(keyword,sort,page,size,timeRange,startDate,endDate,supplierId,deliveryStatus));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/{importProductId}")
    public ApiResponse<Object> findById(@PathVariable("importProductId") Long importProductId){
        ImportProductResponse result = importProductService.getImportProductById(importProductId);
        return new ApiResponse<>(HttpStatus.OK.value(),"Get import product successfull",result);
    }

    @PostMapping("/add")
    public ApiResponse<Object> createImportProduct(@RequestBody @Valid ImportProductCreationRequest req) {
        ImportProductResponse result = importProductService.save(req);
        return new ApiResponse<Object>(HttpStatus.CREATED.value(),"Create import product successfull !",result);
    }

    @PostMapping("/{importProductId}/confirm")
    public void confirmImport(@PathVariable Long importProductId) {
        importProductService.confirmImport(importProductId);
    }

    @PostMapping("/{importProductId}/cancel")
    public void cancleImport(@PathVariable Long importProductId) {
        importProductService.cancelImport(importProductId);
    }

    @PutMapping("/{importProductId}/details/update_quantity")
    public void updateQuantityDetail(@PathVariable Long importProductId, @RequestBody List< @Valid UpdateImportDetailRequest> req) {
        importProductService.updateQuantityDetailFromPendingImport(req,importProductId);
    }

    @DeleteMapping("{importProductId}/details/{detailId}")
    public void removeDetail(@PathVariable Long importProductId, @PathVariable Long detailId) {
        importProductService.removeDetailFromPendingImport(importProductId,detailId);
    }

}
