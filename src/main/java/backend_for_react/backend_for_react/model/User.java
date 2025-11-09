package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "full_name")
    String fullName;

    @Column(name = "avatar_image")
    String avatarImage;

    BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "email_verified")
    Boolean emailVerified = false;

    @Column(name = "phone_verified")
    Boolean phoneVerified = false;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(length = 100)
    String email;

    @Column()
    String password;

    @Column(length = 100)
    String phone;

    @Column(length = 20)
    String provider;

    @Column(name = "username")
    String username;

    @Column(name = "provider_id", length = 100)
    String providerId;

    @ManyToMany
    Set<Role> roles;


    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserHasAddress> userHasAddresses;

    @ManyToMany
    @JoinTable(
            name = "user_favorite_products", // tên bảng trung gian
            joinColumns = @JoinColumn(name = "user_id"), // khóa ngoại tới user
            inverseJoinColumns = @JoinColumn(name = "product_id") // khóa ngoại tới product
    )
    List<Product> favoriteProducts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Cart> cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<VoucherUsage> voucherUsages;


    Integer point = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    UserRank userRank;

}
