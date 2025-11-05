package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.BehaviorType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_behavior")
@Getter
@Setter
public class UserBehavior extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    @Enumerated(EnumType.STRING)
    private BehaviorType behaviorType; // VIEW, ADD_TO_CART, PURCHASE, SEARCH
}
