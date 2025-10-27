package backend_for_react.backend_for_react.model;

import backend_for_react.backend_for_react.common.enums.AddressType;
import backend_for_react.backend_for_react.common.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "user_has_address")
public class UserHasAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 50)
    private AddressType addressType = AddressType.HOME;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Enumerated(EnumType.STRING)
    private Status status;
}
