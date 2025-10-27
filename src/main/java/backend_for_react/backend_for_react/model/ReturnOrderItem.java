package backend_for_react.backend_for_react.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "return_order_items")
@Getter
@Setter
public class ReturnOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Đơn hoàn chứa sản phẩm này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_order_id", nullable = false)
    private ReturnOrder returnOrder;

    // Sản phẩm trong đơn hàng gốc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    // Số lượng hoàn
    @Column(nullable = false)
    private Integer quantity;

    // Tiền hoàn cho sản phẩm này
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal itemRefundAmount = BigDecimal.ZERO;

}
