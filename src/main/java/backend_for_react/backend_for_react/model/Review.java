package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "review")
public class Review extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orderItem_id")
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private Integer rating;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @OneToMany(mappedBy = "review",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ImageReview> images;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
