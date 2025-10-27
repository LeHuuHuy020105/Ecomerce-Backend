package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.ImageReview;
import backend_for_react.backend_for_react.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageReviewRepository extends JpaRepository<ImageReview, Long> {

}
