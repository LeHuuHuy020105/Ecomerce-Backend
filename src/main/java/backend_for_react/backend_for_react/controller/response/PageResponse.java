package backend_for_react.backend_for_react.controller.response;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageResponse<T> implements Serializable {
    private List<T> data;
    private int pageNumber;
    private int PageSize;
    private int totalPages;
    private Long totalElements;
}
