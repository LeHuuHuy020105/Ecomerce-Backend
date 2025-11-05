package backend_for_react.backend_for_react.controller;



import backend_for_react.backend_for_react.controller.request.ProductPackage.ProductPackage;
import backend_for_react.backend_for_react.controller.request.ProductPackage.ProductPackageResponse;
import backend_for_react.backend_for_react.controller.request.Shipping.FeeRequest;
import backend_for_react.backend_for_react.controller.request.Shipping.ShippingOrderRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.ShippingOrderDetailResponse;
import backend_for_react.backend_for_react.service.GhnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final GhnService ghnService;

    @PostMapping("/fee")
    public ApiResponse<FeeResponse> getFee(@RequestBody FeeRequest request) {
        FeeResponse result = ghnService.calculateShippingFee(request);
        return ApiResponse.<FeeResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Caculate fee ship")
                .data(result)
                .build();
    }

    @PostMapping("/{orderId}/add")
    public ApiResponse<ShippingOrderDetailResponse> create(@PathVariable Long orderId , @RequestParam String requiredNote ) {
        ShippingOrderDetailResponse result = ghnService.createShippingOrder(orderId, requiredNote);
        return ApiResponse.<ShippingOrderDetailResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Create shipping order")
                .data(result)
                .build();
    }

    @GetMapping("/detail/{orderCode}")
    public ApiResponse<ShippingOrderDetailResponse> detail(@PathVariable String orderCode) {
        ShippingOrderDetailResponse result =  ghnService.getShippingDetail(orderCode);
        return ApiResponse.<ShippingOrderDetailResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Shipping order detail")
                .data(result)
                .build();
    }

    @PostMapping("/estimate-dimensions")
    public ApiResponse<ProductPackageResponse> getEstimateDimensions(@RequestBody List<@Valid ProductPackage> productPackages) {
        ProductPackageResponse result = ghnService.getProductPackage(productPackages);
        return ApiResponse.<ProductPackageResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Estimate dimensions")
                .data(result)
                .build();
    }
}
