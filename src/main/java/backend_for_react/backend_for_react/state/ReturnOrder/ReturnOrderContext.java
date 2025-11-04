package backend_for_react.backend_for_react.state.ReturnOrder;

import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.model.ReturnOrder;
import backend_for_react.backend_for_react.repository.ProductVariantRepository;
import backend_for_react.backend_for_react.service.GhnService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnOrderContext {
    private ReturnOrder returnOrder;
    private final ProductVariantRepository productVariantRepository;
    private final GhnService ghnService;
    private ReturnOrderState currentState;

    public ReturnOrderContext(ReturnOrder returnOrder , ProductVariantRepository productVariantRepository, GhnService ghnService) {
        this.returnOrder = returnOrder;
        this.productVariantRepository = productVariantRepository;
        this.ghnService = ghnService;
        this.currentState = switch (returnOrder.getStatus()) {
            case REQUESTED -> new RequestedState();
            case APPROVED -> new ApprovedState(ghnService);
            case SHIPPING_BACK -> new ShippingBackState();
            case COMPLETED -> new CompletedState(productVariantRepository);
            case PAYMENTED -> new PaymentedState();
            case REJECTED -> new RejectedState();
            case CANCEL -> null;
        };
    }

    public void changeState(ReturnStatus newStatus) {
        currentState.handle(this, newStatus);
    }
}

