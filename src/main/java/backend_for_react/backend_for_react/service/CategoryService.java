package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.Category.CategoryCreationRequest;
import backend_for_react.backend_for_react.controller.request.Category.CategoryUpdateRequest;
import backend_for_react.backend_for_react.controller.request.Category.MoveCategoryRequest;
import backend_for_react.backend_for_react.controller.response.CategoryResponse;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.model.Category;

import java.util.List;

public interface CategoryService {
    PageResponse<CategoryResponse> findAll(String keyword , String sort, Status status, int page, int size);
    void save(List<CategoryCreationRequest> req);
    void update(CategoryUpdateRequest req);
    void restore(Long id);
    void delete(Long id);
    void moveCategory(MoveCategoryRequest req);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse>findAllWithouPagination();
    List<Category> getAllParentCategories(Long categoryId);
}
