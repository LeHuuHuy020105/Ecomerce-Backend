package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "import_products")
public class ImportProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String importCode;

    private String description;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // CascadeType.ALL để khi lưu ImportProduct thì các ImportDetail cũng được lưu theo
    @OneToMany(mappedBy = "importProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImportDetail> importDetails = new ArrayList<>();

}