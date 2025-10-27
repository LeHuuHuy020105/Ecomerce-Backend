package backend_for_react.backend_for_react.controller.response;

import lombok.*;
import backend_for_react.backend_for_react.controller.request.Shipping.Receiver;
import backend_for_react.backend_for_react.controller.request.Shipping.Sender;
import backend_for_react.backend_for_react.controller.request.Shipping.ShippingOrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model đơn giản hoá, map dữ liệu cần dùng trong app
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingOrderDetailResponse {
    private String orderCode;        // mã trên merchant
    private String orderId;          // mã GHN (order_code hoặc order_id tu GHN)
    private String status;           // trạng thái (ví dụ: ready_to_pick, delivering, delivered, ...) - GHN trả về code
    private String note;
    private String estimatedDelivery; // nếu GHN trả
    private List<ShippingOrderItem> items;
    private Receiver receiver;
    private Sender sender;
    private String requireNote;
    // raw data
    private Object raw;              // giữ nguyên response raw nếu cần debug
}
