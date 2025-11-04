package backend_for_react.backend_for_react.state.Order;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.model.Order;

public interface OrderState {
    void changeState(Order order, DeliveryStatus nextStatus);
}
