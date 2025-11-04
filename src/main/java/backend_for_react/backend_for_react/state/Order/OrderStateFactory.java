package backend_for_react.backend_for_react.state.Order;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;

public class OrderStateFactory {
    public static OrderState getState(DeliveryStatus status) {
        return switch (status) {
            case PENDING -> new PendingState();
            case CONFIRMED -> new ConfirmedState();
            case PACKED -> new PackedState();
            case SHIPPED -> new ShippedState();
            case DELIVERED -> new DeliveredState();
            case CANCELLED -> new CancelledState();
            case COMPLETED -> new CompletedState();
            case REFUNDED -> new RefundedState();
        };
    }
}
