package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.OrderAfterSaleStatus;
import backend_for_react.backend_for_react.common.enums.PaymentStatus;
import backend_for_react.backend_for_react.common.enums.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String customerName;

    private String customerPhone;

    @NotNull
    private String deliveryWardName;

    @NotNull
    private String deliveryWardCode;

    @NotNull
    private Integer deliveryDistrictId;

    @NotNull
    private Integer deliveryProvinceId;

    @NotNull
    private String deliveryDistrictName;

    @NotNull
    private String deliveryProvinceName;

    @NotBlank
    private String deliveryAddress;

    @NotNull
    private Integer serviceTypeId;


    private BigDecimal originalOrderAmount;

    private Integer weight;              // gram (GHN expects grams)

    private Integer length;              // cm (optional)

    private Integer width;               // cm (optional)

    private Integer height;

    private BigDecimal totalFeeForShip = BigDecimal.ZERO;

    private String orderTrackingCode;

    private String note;

    boolean isPaidForShip = false;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<OrderItem>orderItems;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    private BigDecimal voucherDiscountValue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private OrderAfterSaleStatus afterSaleStatus;

    private boolean isConfirmed = false;

    private LocalDateTime deliveredAt;
    private LocalDateTime completedAt;
    private LocalDateTime paymentAt;
    private LocalDateTime refundAt;

}
