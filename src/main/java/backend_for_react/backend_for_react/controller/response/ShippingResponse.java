package backend_for_react.backend_for_react.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ShippingResponse {
    // Mã vận đơn
    private String trackingCode;

    // Ngày dự kiến giao hàng
    private LocalDate estimatedDeliveryDate;
}
