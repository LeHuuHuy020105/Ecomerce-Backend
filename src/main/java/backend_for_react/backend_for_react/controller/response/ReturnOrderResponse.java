package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.ImageReturnOrder;
import backend_for_react.backend_for_react.model.ReturnOrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ReturnOrderResponse {
    private Long id;
    private Integer totalWeight; // gram
    private Integer totalWidth;  // cm
    private Integer totalHeight; // cm
    private Integer totalLength; // cm
    private ReturnStatus status;
    private BigDecimal refundAmount;
    private OrderResponse orderResponse;
    private UserResponse userResponse;
    private String reason;
    private List<ImageReturnOrderResponse> imageReturnOrders;
    private List<ReturnOrderItemResponse> returnOrderItemResponses;
    private String returnTrackingCode;
    private BigDecimal returnShippingFee;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime paymentAt;

    @Data
    @Builder
    @AllArgsConstructor
    public static class ImageReturnOrderResponse{
        private Long id;
        private String imageUrl;
        private Status status;
    }
}
