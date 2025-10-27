package backend_for_react.backend_for_react.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "images_product_description")
public class ImageProductDescription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_image_description" )
    private String urlImage;

    @ManyToOne
    @JoinColumn(name = "product_Id")
    private Product product;

}
