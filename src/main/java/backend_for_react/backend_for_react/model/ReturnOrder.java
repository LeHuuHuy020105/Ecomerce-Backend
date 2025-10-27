package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "return_orders")
@Getter
@Setter
public class ReturnOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ơn hàng gốc cần hoàn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Người gửi yêu cầu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Thông tin kích thước / khối lượng để gửi lại hàng
    private Integer totalWeight; // gram
    private Integer totalWidth;  // cm
    private Integer totalHeight; // cm
    private Integer totalLength; // cm

    // Trạng thái xử lý hoàn hàng
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status = ReturnStatus.REQUESTED;

    // Tổng tiền hoàn
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String reason;

    // Ảnh minh chứng khi hoàn hàng
    @OneToMany(mappedBy = "returnOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageReturnOrder> imageReturnOrders;

    // Danh sách sản phẩm được hoàn
    @OneToMany(mappedBy = "returnOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnOrderItem> returnOrderItems;

    // Phí ship chiều trả hàng (nếu có)
    @Column(precision = 15, scale = 2)
    private BigDecimal returnShippingFee = BigDecimal.ZERO;

    // true = khách trả phí ship, false = shop trả
    private Boolean isReturnShippingPaidByUser = true;

    // Mã vận đơn trả hàng (GHN/GHTK)
    private String returnTrackingCode;

    // hời gian các giai đoạn
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime paymentAt;
}
