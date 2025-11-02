package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> {
    List<ProductVariant> findAllByProduct(Product product);
    Optional<ProductVariant> findByIdAndStatus(Long id, Status status);
}
