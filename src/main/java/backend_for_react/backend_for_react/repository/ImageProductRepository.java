package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.ImageProduct;
import backend_for_react.backend_for_react.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageProductRepository extends JpaRepository<ImageProduct,Long> {
    @Query("SELECT i FROM ImageProduct i WHERE i.product = :product and i.status = :status")
    List<ImageProduct> findAllByStatusAndProduct(Status status, Product product);

    // Xóa nhiều ảnh cùng lúc
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE ImageProduct ip WHERE ip.product = :product AND ip.url IN :urls")
    void deleteAllByProductIdAndUrls(@Param("product") Product product,
                                     @Param("urls") List<String> urls);

}
