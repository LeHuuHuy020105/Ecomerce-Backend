package backend_for_react.backend_for_react.controller.request.Review;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
public class ReviewCreationRequest implements Serializable {
    @NotNull(message = "Order item not null")
    private Long orderItemId;

    @NotNull(message = "Rating not null")
    @Min(1)
    private Integer rating;
    private String comment;
    private List<String> imageUrl;
}
