package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.UserStatus;
import backend_for_react.backend_for_react.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query(value = "select u from User u where (u.fullName like:keyword  or " +
            "u.phone like:keyword or u.email like:keyword or u.username like:keyword) ")
    Page<User> searchByKeyword(String keyword, Pageable pageable);

    boolean existsByUsername(String userName);
    Optional<User> findByUsername(String userName);
    Optional<User> findByProviderAndEmail(String provider, String email);

    Optional<User>findByProviderAndProviderId(String provider, String providerId);

    List<User> findAllByEmail(String email);

    Optional<User>findByIdAndStatus(Long id , UserStatus status);


}
