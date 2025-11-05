package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.VoucherStatus;
import backend_for_react.backend_for_react.common.enums.VoucherType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "voucher")
@Setter
@Getter
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String discription;

    @Enumerated(EnumType.STRING)
    private VoucherType type;

    private Double discountValue;

    private Double maxDiscountValue;

    private Double minDiscountValue = 0.0;

    private Integer totalQuantity;

    private Boolean isShipping;

    @Enumerated(EnumType.STRING)
    private VoucherStatus status;

    private Integer usedQuantity;

    private Integer remainingQuantity;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer usageLimitPerUser = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserRank userRank;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    List<VoucherUsage> voucherUsages;

}
