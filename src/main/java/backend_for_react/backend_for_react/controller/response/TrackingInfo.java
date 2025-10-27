package backend_for_react.backend_for_react.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TrackingInfo {
    // Trạng thái đơn hàng hiện tại
    private String status;

    // Vị trí hiện tại của đơn hàng
    private String currentLocation;

    // Thời gian cập nhật trạng thái cuối cùng
    private LocalDateTime lastUpdated;
}
