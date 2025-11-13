package backend_for_react.backend_for_react.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CalculatorReviewRatingResponse {
    Integer rating;
    Integer totalReview;
}
