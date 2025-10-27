package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.model.Cart;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.ProductVariant;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Page<Cart> findAllByUser(User user, Pageable pageable);
    Optional<Cart> findByUserAndProductVariant(User user, ProductVariant productVariant);
}
