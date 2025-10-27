package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.ImageProductDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageProductDescriptionRepository extends JpaRepository<ImageProductDescription,Long> {
}
