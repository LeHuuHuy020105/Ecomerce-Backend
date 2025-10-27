package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String urlvideo;

    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private String urlCoverImage;
    private BigDecimal listPrice;
    private BigDecimal salePrice;
    private Integer soldQuantity;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Attribute> attributes;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ImageProductDescription> imageProductDescriptions;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ImageProduct> imageProducts;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ProductVariant> variants;

    @ManyToMany(mappedBy = "favoriteProducts")
    List<User> likedByUsers;
}