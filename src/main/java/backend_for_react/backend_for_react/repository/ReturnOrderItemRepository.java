package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.ReturnOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnOrderItemRepository extends JpaRepository<ReturnOrderItem, Long> {
}
