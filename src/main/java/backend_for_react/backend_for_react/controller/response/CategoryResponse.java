package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse implements Serializable {
    private Long id;
    private String name;
    private List<CategoryResponse> childCategory;
    private Status status;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

}
