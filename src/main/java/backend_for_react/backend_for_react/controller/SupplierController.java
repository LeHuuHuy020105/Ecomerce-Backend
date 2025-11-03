package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.Product.ProductCreationRequest;
import backend_for_react.backend_for_react.controller.request.Product.ProductUpdateRequest;
import backend_for_react.backend_for_react.controller.request.Supplier.SupplierCreationRequest;
import backend_for_react.backend_for_react.controller.request.Supplier.SupplierUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.service.SupplierService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
@Slf4j(topic = "SUPPLIER-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplierController {
    SupplierService supplierService;
    @GetMapping("/list")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(required = false) Status status,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","supplier list");
        result.put("data",supplierService.findAll(keyword,sort,status,page,size));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @PostMapping("/{supplierId}/restore")
    public ApiResponse<Void> restore(@PathVariable Long supplierId){
        supplierService.restoreSupplier(supplierId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Supplier restored")
                .build();
    }
    @PostMapping("/add")
    public ApiResponse<Void> createProduct(@RequestBody @Valid SupplierCreationRequest req) throws IOException {
        supplierService.save(req);
        return ApiResponse.<Void>builder().build();
    }
    @PutMapping("/update")
    public ResponseEntity<String> updateSupplier (@RequestBody SupplierUpdateRequest req){
        supplierService.update(req);
        return new ResponseEntity<>("",HttpStatus.OK);
    }
    @DeleteMapping("/{supplierId}/delete")
    public ResponseEntity<String> deleteSupplier (@PathVariable Long supplierId){
        supplierService.delete(supplierId);
        return new ResponseEntity<>("",HttpStatus.OK);
    }
    @GetMapping("/{supplierId}")
    public ResponseEntity<Object> getDetailSupplier (@PathVariable Long supplierId){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","product detail");
        result.put("data",supplierService.getSupplierById(supplierId));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
