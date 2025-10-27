package backend_for_react.backend_for_react.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Đơn hàng chứa sản phẩm này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Phiên bản sản phẩm (màu, size, v.v.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int returnedQuantity = 0;


    // Giá thực tế sau khi trừ khuyến mãi/voucher
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal finalPrice;

    public String buildName() {
        StringBuilder result = new StringBuilder();
        result.append(this.getProductVariant().getProduct().getName());

        List<AttributeValue> attributeValues = this.getProductVariant().getAttributeValues().stream()
                .map(VariantAttributeValue::getAttributeValue)
                .toList();

        if (attributeValues != null && !attributeValues.isEmpty()) {
            result.append(" (");
            for (int i = 0; i < attributeValues.size(); i++) {
                AttributeValue av = attributeValues.get(i);
                result.append(av.getAttribute().getName())
                        .append(": ")
                        .append(av.getValue());
                if (i < attributeValues.size() - 1) {
                    result.append(", ");
                }
            }
            result.append(")");
        }

        return result.toString();
    }
}
