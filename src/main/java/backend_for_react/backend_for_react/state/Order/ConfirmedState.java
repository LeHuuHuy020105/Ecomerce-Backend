package backend_for_react.backend_for_react.state.Order;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.Order;

public class ConfirmedState implements OrderState
{
    @Override
    public void changeState(Order order, DeliveryStatus nextStatus) {
        switch (nextStatus){
            case PACKED:
                order.setOrderStatus(nextStatus);
                break;
            default:
                throw new BusinessException(ErrorCode.BAD_REQUEST,"Invalid transition from CONFIRM to " + nextStatus);
        }
    }
}
