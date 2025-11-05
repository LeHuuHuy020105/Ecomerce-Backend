package backend_for_react.backend_for_react.controller.request.ReturnOrder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReturnOrderCreationRequest {
    @NotNull(message = "order id not blank")
    private Long orderId;

    @NotNull(message = "length not be null")
    private int length;
    @NotNull(message = "weight not be null")
    private int weight;
    @NotNull(message = "height not be null")
    private int height;
    @NotNull(message = "width not be null")
    private int width;

    @NotBlank(message = "reason not blank")
    private String reason;

    private List<String> imageReturnOrder;

    private List<ReturnItemRequest> items; // Có thể trả 1 phần hoặc toàn bộ

    @Data
    @Valid
    public static class ReturnItemRequest {
        @NotNull(message = "order item id not blank")
        private Long orderItemId;
        @NotNull(message = "order item quantity return not blank")
        private Integer quantity;
    }
}

