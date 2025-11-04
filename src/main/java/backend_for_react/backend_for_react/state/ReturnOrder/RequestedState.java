package backend_for_react.backend_for_react.state.ReturnOrder;

import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.ReturnOrder;

import java.time.LocalDateTime;


public class RequestedState implements ReturnOrderState {
    @Override
    public void handle(ReturnOrderContext context, ReturnStatus newStatus) {
        ReturnOrder order = context.getReturnOrder();
        switch (newStatus) {
            case APPROVED -> {
                order.setStatus(ReturnStatus.APPROVED);
                order.setApprovedAt(LocalDateTime.now());
            }
            case REJECTED -> order.setStatus(ReturnStatus.REJECTED);
            default -> throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid transition from REQUESTED to " + newStatus);
        }
    }
}

