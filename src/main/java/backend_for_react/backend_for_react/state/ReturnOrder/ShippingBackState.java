package backend_for_react.backend_for_react.state.ReturnOrder;

import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.ReturnOrder;
import backend_for_react.backend_for_react.model.ReturnOrderItem;

import java.math.BigDecimal;

public class ShippingBackState implements ReturnOrderState {
    @Override
    public void handle(ReturnOrderContext context, ReturnStatus newStatus) {
        ReturnOrder order = context.getReturnOrder();
        if (newStatus == ReturnStatus.COMPLETED) {
            // GHN báo nhận hàng thành công
            order.setStatus(ReturnStatus.COMPLETED);
            BigDecimal refundAmount = BigDecimal.ZERO;
            for (ReturnOrderItem item : order.getReturnOrderItems()) {
                refundAmount = refundAmount.add(
                        item.getOrderItem().getFinalPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                );
            }
            refundAmount = refundAmount.add(order.getReturnShippingFee());
            order.setRefundAmount(refundAmount);
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid transition from SHIPPING_BACK to " + newStatus);
        }
    }
}

