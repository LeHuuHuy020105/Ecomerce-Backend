package backend_for_react.backend_for_react.controller.request.Category;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.response.CategoryResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryCreationRequest implements Serializable {
    @NotBlank(message = "Name category not blank")
    private String name;
    private Long parentId; // null nếu là category cha

    @Valid
    private List<CategoryCreationRequest> childCategories;
}
