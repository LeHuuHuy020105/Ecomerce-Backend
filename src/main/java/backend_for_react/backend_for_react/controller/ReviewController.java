package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.Review.ReviewCreationRequest;
import backend_for_react.backend_for_react.controller.request.Review.ReviewUpdateRequest;
import backend_for_react.backend_for_react.service.impl.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<Object> addReview(
           @RequestBody ReviewCreationRequest req) {
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

    @PutMapping("/delete-image")
    public void deleteImage(@RequestPart List<Long> imageDelete) {
        reviewService.deleteImage(imageDelete);
    }

    @PutMapping("/{reviewid}/add-image")
    public void deleteImage(@RequestPart List<String> imageAdd , @PathVariable Long reviewId) {
        reviewService.addImage(imageAdd,reviewId);
    }

    @DeleteMapping("/{reviewId}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);
        return new ResponseEntity<>("", HttpStatus.OK);
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
