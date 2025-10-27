package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.model.Order;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOrderByIdAndUser(Long id, User user);
    List<Order> findAllByOrderStatusAndDeliveredAtBefore(DeliveryStatus status, LocalDateTime dateTime);
}
