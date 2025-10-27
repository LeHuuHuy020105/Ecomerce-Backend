package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "images_review")
public class ImageReview extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reviews_id")
    private Review review;

    @Column()
    private String urlImage;

    @Enumerated(EnumType.STRING)
    private Status status;
}
