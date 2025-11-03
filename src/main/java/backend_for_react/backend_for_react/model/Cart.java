package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    ProductVariant productVariant;

    Integer quantity;

    private BigDecimal listPriceSnapShot;
    private String nameProductSnapShot;
    private String urlImageSnapShot;
    private String variantAttributesSnapshot;

    @Enumerated(EnumType.STRING)
    Status status;
}
