package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.controller.FeeResponse;
import backend_for_react.backend_for_react.controller.request.ProductPackage.ProductPackage;
import backend_for_react.backend_for_react.controller.request.ProductPackage.ProductPackageResponse;
import backend_for_react.backend_for_react.controller.request.Shipping.FeeRequest;
import backend_for_react.backend_for_react.controller.request.Shipping.ShippingOrderRequest;
import backend_for_react.backend_for_react.controller.response.ShippingOrderDetailResponse;
import backend_for_react.backend_for_react.model.Order;
import backend_for_react.backend_for_react.model.ReturnOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static backend_for_react.backend_for_react.common.utils.ShippingHelper.*;

public interface GhnService {
    ShippingOrderDetailResponse createShippingOrder(Long orderId , String requiredNote);
    ShippingOrderRequest toShippingOrderRequest(Order order);
    Map<String, String> resolveAddress(Integer districtId, String wardCode);
    ShippingOrderDetailResponse getShippingDetail(String ghnOrderCode);
    FeeResponse calculateShippingFee(FeeRequest req);
    void cancelShippingOrder(String ghnOrderCode);
    FeeRequest toFeeRequest(Order order);
    ProductPackageResponse getProductPackage(List<ProductPackage> packages);
    FeeRequest toFeeRequest(ReturnOrder returnOrder);
    ShippingOrderRequest toShippingReturnOrderRequest(ReturnOrder returnOrder);
    ShippingOrderDetailResponse createShippingReturnOrder(Long returnOrderId , String requiredNote);
}
