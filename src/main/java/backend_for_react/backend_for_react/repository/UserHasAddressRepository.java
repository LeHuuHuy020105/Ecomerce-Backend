package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserHasAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserHasAddressRepository extends JpaRepository<UserHasAddress,Long> {
    Optional<UserHasAddress> findByIdAndUser(Long userHassAddressId, User user);

    @Modifying
    @Query("UPDATE UserHasAddress u SET u.isDefault = false WHERE u.user.id = :userId")
    void updateAllIsDefaultFalse(@Param("userId") Long userId);

    Page<UserHasAddress> findAllByUserAndStatus(User user, Pageable pageable , Status status);

}
