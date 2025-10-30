package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.model.Order;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserAndOrderStatus(User user, DeliveryStatus orderStatus, Pageable pageable);
    @Query("""
    SELECT o FROM Order o
    LEFT JOIN o.orderItems i
    WHERE LOWER(i.nameProduct) LIKE :keyword
    """)
    Page<Order> searchByKeywordAndUser(@Param("keyword") String keyword, Pageable pageable, User user);

    @Query("""
        SELECT DISTINCT o FROM Order o
        WHERE
            (
                LOWER(o.customerName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(o.orderTrackingCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:orderStatus IS NULL OR o.orderStatus = :orderStatus)
            AND (:startDate IS NULL OR o.createdAt >= :startDate)
            AND (:endDate IS NULL OR o.createdAt <= :endDate)
        """)
    Page<Order> searchByKeywordAndFilter(
            @Param("keyword") String keyword,
            @Param("orderStatus") DeliveryStatus orderStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    Optional<Order> findOrderByIdAndUser(Long id, User user);
    List<Order> findAllByOrderStatusAndDeliveredAtBefore(DeliveryStatus status, LocalDateTime dateTime);
}
