package backend_for_react.backend_for_react.state.ReturnOrder;

import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.ProductVariant;
import backend_for_react.backend_for_react.model.ReturnOrder;
import backend_for_react.backend_for_react.model.ReturnOrderItem;
import backend_for_react.backend_for_react.repository.ProductVariantRepository;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class CompletedState implements ReturnOrderState {
    private final ProductVariantRepository productVariantRepository;
    @Override
    public void handle(ReturnOrderContext context, ReturnStatus newStatus) {
        ReturnOrder order = context.getReturnOrder();
        if (newStatus == ReturnStatus.PAYMENTED) {
            order.setStatus(ReturnStatus.PAYMENTED);
            order.setPaymentAt(LocalDateTime.now());

            // Hoàn lại hàng về kho
            for (ReturnOrderItem item : order.getReturnOrderItems()) {
                ProductVariant pv = item.getOrderItem().getProductVariant();
                pv.setQuantity(pv.getQuantity() + item.getQuantity());
                productVariantRepository.save(pv);
            }
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid transition from COMPLETED to " + newStatus);
        }
    }
}

