package backend_for_react.backend_for_react.state.Order;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.PaymentStatus;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.Order;

import java.time.LocalDateTime;

public class DeliveredState implements OrderState{
    @Override
    public void changeState(Order order, DeliveryStatus nextStatus) {
        switch (nextStatus){
            case COMPLETED:
                if (order.getOrderTrackingCode() == null) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST,
                            "The order has not been delivered to the shipping unit yet");
                }
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setCompletedAt(LocalDateTime.now());
                order.setOrderStatus(DeliveryStatus.COMPLETED);
                break;

            default:
                throw new BusinessException(ErrorCode.BAD_REQUEST,
                        "Invalid transition from DELIVERED to " + nextStatus);
        }
    }
}
