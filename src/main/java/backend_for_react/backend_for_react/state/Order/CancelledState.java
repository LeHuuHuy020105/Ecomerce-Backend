package backend_for_react.backend_for_react.state.Order;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.PaymentStatus;
import backend_for_react.backend_for_react.common.enums.PaymentType;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.Order;

public class CancelledState implements OrderState{
    @Override
    public void changeState(Order order, DeliveryStatus nextStatus) {
        if (nextStatus == DeliveryStatus.REFUNDED) {
            if (order.getPaymentType() == PaymentType.BANK_TRANSFER &&
                    order.getPaymentStatus() == PaymentStatus.PAID) {
                order.setOrderStatus(DeliveryStatus.REFUNDED);
                order.setPaymentStatus(PaymentStatus.REFUNDED);
            } else {
                throw new BusinessException(ErrorCode.BAD_REQUEST,
                        "Refund only allowed for paid bank transfer orders");
            }
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    "You can't change status from CANCELLED to " + nextStatus);
        }
    }
}
