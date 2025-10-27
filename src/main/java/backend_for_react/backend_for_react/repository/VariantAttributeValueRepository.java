package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.ProductVariant;
import backend_for_react.backend_for_react.model.VariantAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantAttributeValueRepository extends JpaRepository<VariantAttributeValue,Long> {
    List<VariantAttributeValue> findAllByProductVariant(ProductVariant productVariant);
}
