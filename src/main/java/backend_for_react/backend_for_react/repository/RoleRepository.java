package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.Role;
import backend_for_react.backend_for_react.model.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.management.relation.RoleStatus;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String name);


    @Query("SELECT r FROM Role r WHERE " +
            "LOWER(r.name) LIKE :keyword OR LOWER(r.description) LIKE :keyword")
    Page<Role> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByName(String name);

    Optional<Role>findByIdAndStatus(Long id, Status status);
}
