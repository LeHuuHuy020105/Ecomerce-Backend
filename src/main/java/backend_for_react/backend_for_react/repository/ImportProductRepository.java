package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.model.ImportProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ImportProductRepository
        extends JpaRepository<ImportProduct, Long>, JpaSpecificationExecutor<ImportProduct> {

    // Tìm kiếm theo mã phiếu nhập (tìm gần đúng)
    Page<ImportProduct> findByImportCodeContainingIgnoreCase(String importCode, Pageable pageable);

    // Tìm kiếm theo nhà cung cấp
    Page<ImportProduct> findBySupplierId(Long supplierId, Pageable pageable);

    // Tìm kiếm theo khoảng thời gian
    Page<ImportProduct> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Tìm kiếm theo trạng thái
    Page<ImportProduct> findByStatus(DeliveryStatus status, Pageable pageable);

    // Tìm kiếm theo SKU hoặc Tên sản phẩm (thông qua ImportDetail -> ProductVariant -> Product)
    @Query("SELECT DISTINCT ip FROM ImportProduct ip " +
            "JOIN ip.importDetails id " +
            "JOIN id.productVariant pv " +
            "JOIN pv.product p " +
            "WHERE pv.sku LIKE %:keyword% OR p.name LIKE %:keyword%")
    Page<ImportProduct> findBySkuOrProductName(@Param("keyword") String keyword, Pageable pageable);
}