package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
    Integer countByVoucherIdAndUserId(Long voucherId, Long userId);
}

