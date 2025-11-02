package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Rank;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserRank;
import backend_for_react.backend_for_react.model.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

@Repository
public interface UserRankRepository extends JpaRepository<UserRank, Long> {

    @Query("SELECT r FROM UserRank r WHERE :totalSpent >= r.minSpent ORDER BY r.minSpent DESC LIMIT 1")
    UserRank findTopEligibleRank(@Param("totalSpent") BigDecimal totalSpent);


    @Query(value = "SELECT COALESCE(MAX(r.level), -1) FROM UserRank r" , nativeQuery = true)
    Integer findMaxLevel();

    @Query(value = "select u from UserRank u where u.name like:keyword")
    Page<UserRank> searchByKeyword(String keyword, Pageable pageable);

    boolean existsByName(String name);

    Optional<UserRank> findByName(String name);

    Optional<UserRank> findByIdAndStatus(Long id, Status status);
}
