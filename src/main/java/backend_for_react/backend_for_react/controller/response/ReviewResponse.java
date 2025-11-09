package backend_for_react.backend_for_react.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse implements Serializable {
    private Long id;
    private Long productId;
    private UserResponse userResponse;
    private String avatarUser;
    private String fullName;      // lấy từ User
    private Integer rating;
    private String comment;
    private String status;
    private List<ImageResponse> images; // trả danh sách URL hoặc base64
    private LocalDateTime createdDate; // lấy từ BaseEntity
}
