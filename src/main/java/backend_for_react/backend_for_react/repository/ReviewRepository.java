package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.Review;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    boolean existsByOrderItemIdAndUser(Long orderItemId, User user);

    List<Review> findByProductAndStatusAndUser(Product product, Status status, User user);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.status = backend_for_react.backend_for_react.common.enums.Status.ACTIVE")
    Double findAverageRatingByProductId(Long productId);

    Optional<Review> findByIdAndStatus(Long id, Status status);

        @Query("""
        SELECT DISTINCT r
        FROM Review r
        LEFT JOIN r.images i
        LEFT JOIN r.orderItem oi
        LEFT JOIN oi.productVariant pv
        WHERE (:productVariantId IS NULL OR pv.id = :productVariantId)
        AND (:hasImage IS NULL OR 
             (:hasImage = TRUE AND i.id IS NOT NULL) OR 
             (:hasImage = FALSE AND i.id IS NULL))
        """)
        Page<Review> findAllByFilter(
                @Param("productVariantId") Long productVariantId,
                @Param("hasImage") Boolean hasImage,
                Pageable pageable
        );


        Page<Review> findAllByProduct(Product product, Pageable pageable);

}
