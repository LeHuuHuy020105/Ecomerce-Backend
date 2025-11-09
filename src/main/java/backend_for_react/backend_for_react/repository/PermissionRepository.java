package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    @Query("SELECT p FROM Permission p WHERE p.id IN :ids AND p.status = :status")
    List<Permission> findAllByStatusActive(@Param("ids") Set<Long> ids, @Param("status") Status status);

    boolean existsByName(String name);

    List<Permission> findAllByStatus(Status status);
}
