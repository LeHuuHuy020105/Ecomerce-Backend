package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("select u from Product u where u.productStatus = :status AND (lower(u.name) like :keyword or lower(u.description) like :keyword)")
    Page<Product> searchByKeyword(String keyword, ProductStatus status, Pageable pageable);

    @Query("select u from Product u where lower(u.name) like :keyword or lower(u.description) like :keyword")
    Page<Product> searchByKeyword(String keyword, Pageable pageable);

    Page<Product> findAllByProductStatus(ProductStatus status, Pageable pageable);

    @Query("""
        SELECT p FROM Product p 
        WHERE p.productStatus = :status 
        AND p.category.id IN :categoryIds
        """)
    Page<Product> findByCategoryIds(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("status") ProductStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT p FROM Product p
        WHERE LOWER(p.name) LIKE :keyword 
        AND p.productStatus = :status 
        AND p.category.id IN :categoryIds
        """)
    Page<Product> searchByKeywordAndCategory(
            @Param("keyword") String keyword,
            @Param("status") ProductStatus status,
            @Param("categoryIds") List<Long> categoryIds,
            Pageable pageable
    );

}
