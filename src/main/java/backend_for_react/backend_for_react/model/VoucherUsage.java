package backend_for_react.backend_for_react.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher_usage")
@Data
public class VoucherUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Voucher voucher;

    @ManyToOne
    private User user;

    @CreationTimestamp
    private LocalDateTime usedAt;
}
