package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.ReturnOrder;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long> {
    Optional<ReturnOrder>findByIdAndUser(Long returnOrderId, User user);

    Page<ReturnOrder> findAllByUser(User user , Pageable pageable);
}
