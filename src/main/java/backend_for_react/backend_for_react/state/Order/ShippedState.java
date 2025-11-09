package backend_for_react.backend_for_react.state.Order;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.Order;

import java.time.LocalDateTime;

public class ShippedState implements OrderState{
    @Override
    public void changeState(Order order, DeliveryStatus nextStatus) {
        switch (nextStatus){
            case DELIVERED:
                if(order.getOrderTrackingCode() == null){
                    throw new BusinessException(ErrorCode.BAD_REQUEST,"You must create shipping order with GHN");
                }
                order.setOrderStatus(nextStatus);
                order.setDeliveredAt(LocalDateTime.now());
                break;
            default:
                throw new BusinessException(ErrorCode.BAD_REQUEST,"Invalid transition from SHIPPED to " + nextStatus);
        }
    }
}
