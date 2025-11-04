package backend_for_react.backend_for_react.service.impl;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.utils.CloudinaryHelper;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.controller.request.Review.ReviewCreationRequest;
import backend_for_react.backend_for_react.controller.request.Review.ReviewUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ImageResponse;
import backend_for_react.backend_for_react.controller.response.ReviewResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
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
    private final ProductRepository productRepository;
    private final SecurityUtils securityUtils;


    public Long  save(ReviewCreationRequest req) {
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNAUTHENTICATED, "You must be logged in to perform this action.");
        }
        log.info("currentUserId = {}, orderItemId = {}", currentUser.getId(), req.getOrderItemId());
        // Truy vấn OrderItem đảm bảo thuộc về user
        OrderItem orderItem = orderItemRepository.findByIdAndUserId(req.getOrderItemId(), currentUser.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "You are not allowed to review this item"));
        if(!orderItem.getOrder().getOrderStatus().equals(DeliveryStatus.COMPLETED)){
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

       if(req.getImageUrl() != null){
           for(String url : req.getImageUrl()){
               ImageReview newImageReview = new ImageReview();
               newImageReview.setReview(review);
               newImageReview.setStatus(Status.ACTIVE);
               newImageReview.setUrlImage(url);
           }
       }

        updateProductAvgRating(review.getProduct().getId());

        return review.getId();
    }

    public ReviewResponse getReviewMeByProduct(Long productId){
        User currentUser = securityUtils.getCurrentUser();
        Product product = productRepository.findByIdAndProductStatus(productId,ProductStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,MessageError.PRODUCT_NOT_FOUND));
        Review review = reviewRepository.findByProductAndStatus(product,Status.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"Review not found"));
        if(review.getUser() != currentUser){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Review does not belong to this user");
        }
        return getReviewResponse(review);
    }
    private void updateProductAvgRating(Long productId) {
        Double avgRating = reviewRepository.findAverageRatingByProductId(productId);
        Product product = productRepository.findByIdAndProductStatus(productId, ProductStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.PRODUCT_NOT_FOUND));
        product.setAvgRating(avgRating != null ? avgRating : 0.0);
        productRepository.save(product);
    }

    @Transactional
    public void update(ReviewUpdateRequest req) {
        Review review = reviewRepository.findByIdAndStatus(req.getId(),Status.ACTIVE)
                .orElseThrow(()->new BusinessException(ErrorCode.BAD_REQUEST,"Review not found"));
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        updateProductAvgRating(review.getProduct().getId());
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteImage(List<Long> imageDelete) {
        for(Long id : imageDelete) {
            ImageReview imageReview = imageReviewRepository.findByIdAndStatus(id,Status.ACTIVE)
                    .orElseThrow(()->new BusinessException(ErrorCode.BAD_REQUEST,"ReviewImage not found"));
            imageReview.setStatus(Status.INACTIVE);
            imageReviewRepository.save(imageReview);
        }
    }

    @Transactional
    public void addImage(List<String> imageAdd , Long reviewId) {
        Review review = reviewRepository.findByIdAndStatus(reviewId,Status.ACTIVE)
                .orElseThrow(()->new BusinessException(ErrorCode.BAD_REQUEST,"Review not found"));
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
        Review review = reviewRepository.findByIdAndStatus(id,Status.ACTIVE)
                .orElseThrow(()->new BusinessException(ErrorCode.BAD_REQUEST,"Review not found"));
        review.setStatus(Status.INACTIVE);
        updateProductAvgRating(review.getProduct().getId());
        reviewRepository.save(review);
    }


    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findByIdAndStatus(reviewId,Status.ACTIVE)
                .orElseThrow(()->new BusinessException(ErrorCode.BAD_REQUEST,"Review not found"));
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
