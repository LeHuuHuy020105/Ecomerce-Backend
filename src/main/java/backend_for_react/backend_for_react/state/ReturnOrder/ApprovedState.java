package backend_for_react.backend_for_react.state.ReturnOrder;

import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.ReturnOrder;
import backend_for_react.backend_for_react.service.GhnService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class ApprovedState implements ReturnOrderState {
    private final GhnService ghnService;

    @Override
    public void handle(ReturnOrderContext context, ReturnStatus newStatus) {
        ReturnOrder order = context.getReturnOrder();
        switch (newStatus) {
            case SHIPPING_BACK -> {
                order.setStatus(ReturnStatus.SHIPPING_BACK);
                // Gọi GHN tạo đơn hoàn
                ghnService.createShippingReturnOrder(order.getId(), "CHOTHUHANG");
            }
            default -> throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid transition from APPROVED to " + newStatus);
        }
    }
}

