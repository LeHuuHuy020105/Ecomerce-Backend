package backend_for_react.backend_for_react.controller.request.Category;

import lombok.Data;

@Data
public class MoveCategoryRequest {
    private Long categoryId;
    private Long categoryParentId;
}
