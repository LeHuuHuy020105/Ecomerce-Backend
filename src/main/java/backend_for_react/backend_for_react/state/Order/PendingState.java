package backend_for_react.backend_for_react.state.Order;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.PaymentStatus;
import backend_for_react.backend_for_react.common.enums.PaymentType;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.Order;

public class PendingState implements OrderState
{
    @Override
    public void changeState(Order order, DeliveryStatus nextStatus) {
        if(order.getPaymentType().equals(PaymentType.BANK_TRANSFER) && order.getPaymentStatus().equals(PaymentStatus.UNPAID)){
            throw new BusinessException(ErrorCode.BAD_REQUEST," You can't change status with order bank tranfer unpaid");
        }
        switch (nextStatus){
            case CONFIRMED:
                order.setOrderStatus(nextStatus);
                break;
            case CANCELLED:
                order.setOrderStatus(nextStatus);
                break;
            default:
                throw new BusinessException(ErrorCode.BAD_REQUEST,"Invalid transition from PENDING to " + nextStatus);
        }
    }
}
