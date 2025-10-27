package backend_for_react.backend_for_react.controller.request.ReturnOrder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReturnOrderCreationRequest {
    @NotNull
    private Long orderId;

    @NotBlank
    private String reason;

    @NotBlank
    private boolean isReturnShippingPaidByUser;

    private List<String> imageReturnOrder;

    private List<ReturnItemRequest> items; // Có thể trả 1 phần hoặc toàn bộ

    @Data
    public static class ReturnItemRequest {
        private Long orderItemId;
        private Integer quantity;
    }
}

