package backend_for_react.backend_for_react.state.ReturnOrder;

import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;

public class PaymentedState implements ReturnOrderState {
    @Override
    public void handle(ReturnOrderContext context, ReturnStatus newStatus) {
        throw new BusinessException(ErrorCode.BAD_REQUEST, "Paymented order cannot change status");
    }
}
