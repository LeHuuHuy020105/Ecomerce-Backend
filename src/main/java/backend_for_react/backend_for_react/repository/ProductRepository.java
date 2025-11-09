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
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("select u from Product u where u.productStatus = :status AND (lower(u.name) like :keyword or lower(u.description) like :keyword)")
    Page<Product> searchByKeyword(String keyword, ProductStatus status, Pageable pageable);

    @Query("select u from Product u where lower(u.name) like :keyword or lower(u.description) like :keyword")
    Page<Product> searchByKeyword(String keyword, Pageable pageable);

    Page<Product> findAllByProductStatus(ProductStatus status, Pageable pageable);


    @Query("""
    SELECT p FROM User u
    JOIN u.favoriteProducts p
    WHERE u.id = :userId
    AND p.productStatus = backend_for_react.backend_for_react.common.enums.ProductStatus.ACTIVE
    AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
    Page<Product> findFavoriteProductsByUserIdAndKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    @Query("""
    SELECT p FROM User u
    JOIN u.favoriteProducts p
    WHERE u.id = :userId
    AND p.productStatus = backend_for_react.backend_for_react.common.enums.ProductStatus.ACTIVE
""")
    Page<Product> findFavoriteProductsByUserId(@Param("userId") Long userId, Pageable pageable);



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

    Optional<Product> findByIdAndProductStatus(Long id, ProductStatus productStatus);

    // 🔹 Dành cho khách (chưa đăng nhập)
    @Query("""
           SELECT p FROM Product p
           ORDER BY p.soldQuantity DESC, p.avgRating DESC
           """)
    Page<Product> findRecommendedForGuest(Pageable pageable);

    // Cac san pham cung loai va co so luong ban ra va danh gia cao cao - thap
    @Query("""
           SELECT p FROM Product p
           WHERE p.category.name IN :categories
           ORDER BY p.avgRating DESC, p.soldQuantity DESC
           """)
    Page<Product> findRecommendedForUser(List<String> categories, Pageable pageable);

    // 🔹 Lấy danh sách tên category từ danh sách sản phẩm
    @Query("SELECT DISTINCT p.category.name FROM Product p WHERE p.id IN :productIds")
    List<String> findCategoryNamesByProductIds(Set<Long> productIds);

    // Lay top 20 san pham ban chay va danh gia cao - thap
    List<Product> findTop20ByOrderBySoldQuantityDescAvgRatingDesc();

    // Lay 10 san pham ban ra va danh gia thap - cao
    List<Product> findTop10ByOrderBySoldQuantityAscAvgRatingAsc();

    // 🔹 Custom query (nếu có filter tương tự)
    @Query("""
           SELECT p FROM Product p
           WHERE p.category.name IN :categories
           ORDER BY p.avgRating DESC, p.soldQuantity DESC
           """)
    List<Product> findTop50ByCategoryNamesAndSimilarPrice(List<String> categories);
}
