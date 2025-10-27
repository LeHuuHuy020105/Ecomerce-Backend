package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    @Query("SELECT oi FROM OrderItem oi WHERE oi.id = :orderItemId AND oi.order.user.id = :userId")
    Optional<OrderItem> findByIdAndUserId(Long orderItemId, Long userId);
}
