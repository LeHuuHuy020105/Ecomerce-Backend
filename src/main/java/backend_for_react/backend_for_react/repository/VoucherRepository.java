package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.enums.VoucherStatus;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserRank;
import backend_for_react.backend_for_react.model.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    @Query(value = "SELECT * FROM voucher v " +
            "WHERE v.code LIKE %:keyword% " +
            "OR v.type LIKE %:keyword% " +
            "OR v.user_rank LIKE %:keyword%",
            nativeQuery = true)
    Page<Voucher> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT * FROM voucher v " +
            "WHERE v.code LIKE %:keyword% " +
            "OR v.type LIKE %:keyword% " +
            "OR v.user_rank LIKE %:keyword%" +
            "OR v.status =: status",
            nativeQuery = true)
    Page<Voucher> searchByKeyword(@Param("keyword") String keyword, Pageable pageable , VoucherStatus status);


    @Query("""
        SELECT v 
        FROM Voucher v 
        WHERE v.startDate <= :now 
            AND v.endDate >= :now
            AND v.totalQuantity >0 
            AND v.userRank.level <= :levelRank
            AND v.userRank = null
""")
    List<Voucher> findAllAvaiableForRank(@Param("now")LocalDateTime now , @Param("levelRank") Integer levelRank);


    boolean existsByUserRank(UserRank userRank);

    Page<Voucher> findAllByStatus(VoucherStatus status, Pageable pageable);


}
