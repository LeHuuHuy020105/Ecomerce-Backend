package backend_for_react.backend_for_react.controller.request.Review;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
public class ReviewCreationRequest implements Serializable {
    private Long orderItemId;
    private Integer rating;
    private String comment;
    private List<String> imageUrl;
}
