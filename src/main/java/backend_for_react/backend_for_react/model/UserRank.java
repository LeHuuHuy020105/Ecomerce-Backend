package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "user_rank")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    @Unique
    private BigDecimal minSpent;

    @OneToMany(mappedBy = "userRank", fetch = FetchType.LAZY)
    private List<Voucher> vouchers;

    @OneToMany(mappedBy = "userRank", fetch = FetchType.LAZY)
    private List<User> users;

    @Enumerated(EnumType.STRING)
    private Status status;
}
