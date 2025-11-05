package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.Order;
import backend_for_react.backend_for_react.model.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
    Integer countByVoucherIdAndUserId(Long voucherId, Long userId);

    Optional<VoucherUsage> findByOrder(Order order);
}

