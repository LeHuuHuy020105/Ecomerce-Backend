package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.Attribute;
import backend_for_react.backend_for_react.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Attr;

import java.util.List;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute,Long> {
    List<Attribute> findAllByProduct(Product product);

    Attribute findByName(String name);
}
