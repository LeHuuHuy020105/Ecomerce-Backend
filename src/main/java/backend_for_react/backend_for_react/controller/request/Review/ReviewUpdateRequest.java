package backend_for_react.backend_for_react.controller.request.Review;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReviewUpdateRequest implements Serializable {
    private Long id;
    private Integer rating;
    private String comment;
}
