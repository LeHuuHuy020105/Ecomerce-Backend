package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Query(value = "select u from Category u where u.status= :status AND (u.name like:keyword) ")
    Page<Category> searchByKeyword(String keyword, Status status, Pageable pageable);

    @Query(value = "select u from Category u where (u.name like:keyword) ")
    Page<Category> searchByKeyword(String keyword,Pageable pageable);

    List<Category> findCategoriesByParentAndStatus(Category parent , Status status);

    List<Category> findByParent_Id(Long parentId);

    Page<Category> findAllByStatus(Status status, Pageable pageable);

    Optional<Category> findByIdAndStatus(Long categoryId , Status status);
}
