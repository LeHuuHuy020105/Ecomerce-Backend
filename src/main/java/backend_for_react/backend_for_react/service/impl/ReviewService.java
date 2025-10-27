package backend_for_react.backend_for_react.service.impl;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.utils.CloudinaryHelper;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.controller.request.Review.ReviewCreationRequest;
import backend_for_react.backend_for_react.controller.request.Review.ReviewUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ImageResponse;
import backend_for_react.backend_for_react.controller.response.ReviewResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.*;
import backend_for_react.backend_for_react.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j(topic = "REVIEW - SERVICE")
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ImageReviewRepository imageReviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final SecurityUtils securityUtils;


    public Long save(ReviewCreationRequest req) {
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHENTICATED, "You must be logged in to perform this action.");
        }
        log.info("currentUserId = {}, orderItemId = {}", currentUser.getId(), req.getOrderItemId());
        // Truy vấn OrderItem đảm bảo thuộc về user
        OrderItem orderItem = orderItemRepository.findByIdAndUserId(req.getOrderItemId(), currentUser.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "You are not allowed to review this item"));
        if(!orderItem.getOrder().getOrderStatus().equals(DeliveryStatus.CONFIRMED)){
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "You can only review after delivery");
        }
        if (reviewRepository.existsByOrderItemId(orderItem.getId())) {
            throw new BusinessException(ErrorCode.DUPLICATE, "You have already reviewed this item");
        }
        Review review = new Review();
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setUser(orderItem.getOrder().getUser());
        review.setProduct(orderItem.getProductVariant().getProduct());
        review.setOrderItem(orderItem);
        review.setStatus(Status.ACTIVE);
        reviewRepository.save(review);

        for(String url : req.getImageUrl()){
            ImageReview newImageReview = new ImageReview();
            newImageReview.setReview(review);
            newImageReview.setStatus(Status.ACTIVE);
            newImageReview.setUrlImage(url);
        }
        return review.getId();
    }

    @Transactional
    public void update(ReviewUpdateRequest req) {
        Review review = reviewRepository.findById(req.getId()).orElseThrow(()->new EntityNotFoundException("Review not found"));
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteImage(List<Long> imageDelete) {
        for(Long id : imageDelete) {
            ImageReview imageReview = imageReviewRepository.findById(id).orElseThrow(()->new EntityNotFoundException("ReviewImage not found"));
            imageReview.setStatus(Status.INACTIVE);
            imageReviewRepository.save(imageReview);
        }
    }

    @Transactional
    public void addImage(List<String> imageAdd , Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()->new EntityNotFoundException("Review not found"));
        for(String url : imageAdd) {
           ImageReview imageReview = new ImageReview();
           imageReview.setUrlImage(url);
           imageReview.setReview(review);
           imageReview.setStatus(Status.ACTIVE);
           imageReviewRepository.save(imageReview);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Review not found"));
        review.setStatus(Status.INACTIVE);
        reviewRepository.save(review);
    }


    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()->new EntityNotFoundException("Review not found"));
        return getReviewResponse(review);
    }

    private ReviewResponse getReviewResponse(Review review) {
        List<ImageResponse> imageReviewDTOS = review.getImages().stream()
                .map(img -> ImageResponse.builder()
                        .id(img.getId())
                        .url(img.getUrlImage())
                        .build())
                .toList();
        String avatarUser = review.getUser().getAvatarImage();
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .fullName(review.getUser().getFullName())
                .rating(review.getRating())
                .avatarUser(avatarUser)
                .comment(review.getComment())
                .images(imageReviewDTOS)
                .createdDate(review.getCreatedAt())
                .build();
    }

}
