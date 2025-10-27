package backend_for_react.backend_for_react.controller.request.Category;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.response.CategoryResponse;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryCreationRequest implements Serializable {
    private String name;
    private Long parentId; // null nếu là category cha
    private Status status;
    private List<CategoryCreationRequest> childCategories;
}
