package backend_for_react.backend_for_react.state.ReturnOrder;

import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.model.ReturnOrder;

public interface ReturnOrderState {
    void handle(ReturnOrderContext context, ReturnStatus newStatus);
}

