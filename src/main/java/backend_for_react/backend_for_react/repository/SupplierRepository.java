package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.Supplier;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier,Long> {
    @Query(value = "select u from Supplier u where u.status= :status AND (u.name like:keyword  or " +
            "u.phone like:keyword or u.address like:keyword) ")
    Page<Supplier> searchByKeyword(String keyword, Status status, Pageable pageable);

    @Query(value = "select u from Supplier u where (u.name like:keyword  or " +
            "u.phone like:keyword or u.address like:keyword) ")
    Page<Supplier> searchByKeyword(String keyword,Pageable pageable);

    Page<Supplier> findAllByStatus(Status status , Pageable pageable);

    Optional<Supplier>findByIdAndStatus(Long id, Status status);
}
