package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.Review.ReviewCreationRequest;
import backend_for_react.backend_for_react.controller.request.Review.ReviewUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.CalculatorReviewRatingResponse;
import backend_for_react.backend_for_react.controller.response.ReviewResponse;
import backend_for_react.backend_for_react.service.impl.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/product/{productId}/list")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @PathVariable Long productId){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","review list");
        result.put("data",reviewService.findAll(productId,sort,page,size));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/productVariant/{productVariantId}/list")
    public ResponseEntity<Object> findAll(@PathVariable Long productVariantId,
                                          @RequestParam(required = false) Integer rating,
                                          @RequestParam(required = false) Boolean hasImage,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","review list");
        result.put("data",reviewService.findAllForFilter(productVariantId,rating,hasImage,sort,page,size));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PostMapping("/calculator/{productId}/rating")
    public ApiResponse<List<CalculatorReviewRatingResponse>> calculateRating(@PathVariable Long productId){
        List<CalculatorReviewRatingResponse> result = reviewService.calculatorReviewForFilterRating(productId);
        return ApiResponse.<List<CalculatorReviewRatingResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("calculating rating")
                .data(result)
                .build();
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addReview(
           @RequestBody @Valid ReviewCreationRequest req) {
        Long reviewId = reviewService.save(req);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "user created successfull");
        result.put("data", reviewId);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateReview(
            @RequestBody ReviewUpdateRequest req) {
        reviewService.update(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ReviewResponse>> getReviewMeByProduct(@PathVariable Long productId) {
        var result = reviewService.getReviewMeByProduct(productId);
        return ApiResponse.<List<ReviewResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Get review me by product")
                .data(result)
                .build();
    }

    @PutMapping("/delete-image")
    public void deleteImage(@RequestPart List<Long> imageDelete) throws IOException {
        reviewService.deleteImage(imageDelete);
    }

    @PutMapping("/{reviewid}/add-image")
    public void deleteImage(@RequestPart List<String> imageAdd , @PathVariable Long reviewId) {
        reviewService.addImage(imageAdd,reviewId);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Object> getDetailReview(@PathVariable Long reviewId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "user created successfull");
        result.put("data", reviewService.getReviewById(reviewId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
