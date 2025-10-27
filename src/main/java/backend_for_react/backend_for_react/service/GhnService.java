package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.controller.FeeResponse;
import backend_for_react.backend_for_react.controller.request.Shipping.FeeRequest;
import backend_for_react.backend_for_react.controller.request.Shipping.ShippingOrderRequest;
import backend_for_react.backend_for_react.controller.response.ShippingOrderDetailResponse;
import backend_for_react.backend_for_react.model.Order;
import backend_for_react.backend_for_react.model.ReturnOrder;

import java.math.BigDecimal;
import java.util.Map;

public interface GhnService {
    ShippingOrderDetailResponse createShippingOrder(Long orderId , String requiredNote);
    ShippingOrderRequest toShippingOrderRequest(Order order);
    Map<String, String> resolveAddress(Integer districtId, String wardCode);
    ShippingOrderDetailResponse getShippingDetail(String ghnOrderCode);
    FeeRequest toFeeRequest(Order order);
    FeeResponse calculateShippingFee(FeeRequest req);
    void cancelShippingOrder(String ghnOrderCode);

    FeeRequest toFeeRequest(ReturnOrder returnOrder);
    ShippingOrderRequest toShippingReturnOrderRequest(ReturnOrder returnOrder);
    ShippingOrderDetailResponse createShippingReturnOrder(Long returnOrderId , String requiredNote);
}
