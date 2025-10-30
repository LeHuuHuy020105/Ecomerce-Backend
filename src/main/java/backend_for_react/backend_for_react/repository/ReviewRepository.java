package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    boolean existsByOrderItemId(Long orderItemId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.status = backend_for_react.backend_for_react.common.enums.Status.ACTIVE")
    Double findAverageRatingByProductId(Long productId);

}
