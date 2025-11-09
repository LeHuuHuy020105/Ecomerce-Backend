package backend_for_react.backend_for_react.service.impl;

import backend_for_react.backend_for_react.common.enums.DeliveryStatus;
import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.utils.CloudinaryHelper;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.controller.request.Review.ReviewCreationRequest;
import backend_for_react.backend_for_react.controller.request.Review.ReviewUpdateRequest;
import backend_for_react.backend_for_react.controller.response.ImageResponse;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.ProductBaseResponse;
import backend_for_react.backend_for_react.controller.response.ReviewResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.UserMapper;
import backend_for_react.backend_for_react.model.*;
import backend_for_react.backend_for_react.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j(topic = "REVIEW - SERVICE")
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ImageReviewRepository imageReviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;
    private final CloudinaryHelper cloudinaryHelper;

    public PageResponse<ProductBaseResponse> findAll(Long productId, String sort, int page, int size) {
        Product product = productRepository.findByIdAndProductStatus(productId,ProductStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,MessageError.PRODUCT_NOT_FOUND));
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = Sort.by(Sort.Direction.ASC, columnName);
                } else {
                    order = Sort.by(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<Review> reviews = reviewRepository.findAllByProduct(product,pageable);

        PageResponse response = getReviewPageResponse(pageNo, size, reviews);
        return response;
    }

    public PageResponse<ProductBaseResponse> findAllForFilter(Long productVariantId, Boolean hasImage,String sort, int page, int size) {
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = Sort.by(Sort.Direction.ASC, columnName);
                } else {
                    order = Sort.by(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<Review> reviews = reviewRepository.findAllByFilter(productVariantId, hasImage, pageable);

        PageResponse response = getReviewPageResponse(pageNo, size, reviews);
        return response;
    }
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
        if(!orderItem.getOrder().isConfirmed()){
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "You can only review after confirmed");
        }
        if (reviewRepository.existsByOrderItemIdAndUser(orderItem.getId(),currentUser)) {
            throw new BusinessException(ErrorCode.DUPLICATE, "You have already reviewed this item");
        }
        Review review = new Review();
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setUser(orderItem.getOrder().getUser());
        review.setProduct(orderItem.getProductVariant().getProduct());
        review.setOrderItem(orderItem);
        review.setStatus(Status.ACTIVE);

        boolean hasImage = false;
        List<ImageReview> imageReviews = new ArrayList<>();
       if(req.getImageUrl() != null){
           hasImage = true;
           for(String url : req.getImageUrl()){
               ImageReview newImageReview = new ImageReview();
               newImageReview.setReview(review);
               newImageReview.setStatus(Status.ACTIVE);
               newImageReview.setUrlImage(url);
               imageReviews.add(newImageReview);
           }
       }
       review.setImages(imageReviews);
        reviewRepository.save(review);
        int point = calculateRewardPoints(req.getRating(),hasImage);
        currentUser.setPoint(currentUser.getPoint() + point);
        userRepository.save(currentUser);
        updateProductAvgRating(review.getProduct().getId());

        return review.getId();
    }

    public List<ReviewResponse> getReviewMeByProduct(Long productId){
        User currentUser = securityUtils.getCurrentUser();
        Product product = productRepository.findByIdAndProductStatus(productId,ProductStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,MessageError.PRODUCT_NOT_FOUND));
        List<Review> reviews = reviewRepository.findByProductAndStatusAndUser(product,Status.ACTIVE,currentUser);
        List<ReviewResponse> reviewResponses = null;
        if(reviews != null){
            reviewResponses = reviews.stream().map(review -> getReviewResponse(review)).toList();
        }
        return reviewResponses;
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
        User currentUser = securityUtils.getCurrentUser();
        Review review = reviewRepository.findByIdAndStatus(req.getId(),Status.ACTIVE)
                .orElseThrow(()->new BusinessException(ErrorCode.BAD_REQUEST,"Review not found"));
        if(review.getUser() != currentUser){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Review does not belong to this user");
        }
        if(req.getRating() != null){
            review.setRating(req.getRating());
        }
        if(req.getComment() != null){
            review.setComment(req.getComment());
        }
        updateProductAvgRating(review.getProduct().getId());
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteImage(List<Long> imageDelete) throws IOException {
        User currentUser = securityUtils.getCurrentUser();
        List<String> urls = new ArrayList<>();
        for(Long id : imageDelete) {
            ImageReview imageReview = imageReviewRepository.findByIdAndStatus(id,Status.ACTIVE)
                    .orElseThrow(()->new BusinessException(ErrorCode.BAD_REQUEST,"ReviewImage not found"));
            if(imageReview.getReview().getUser() != currentUser){
                throw new BusinessException(ErrorCode.BAD_REQUEST,"Review does not belong to this user");
            }
            urls.add(imageReview.getUrlImage());
            imageReview.setStatus(Status.INACTIVE);
            imageReviewRepository.delete(imageReview);
        }
        cloudinaryHelper. deleteByUrl(urls);
    }

    @Transactional
    public void addImage(List<String> imageAdd , Long reviewId) {
        User currentUser = securityUtils.getCurrentUser();
        Review review = reviewRepository.findByIdAndStatus(reviewId,Status.ACTIVE)
                .orElseThrow(()->new BusinessException(ErrorCode.BAD_REQUEST,"Review not found"));
        if(review.getUser() != currentUser){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Review does not belong to this user");
        }
        for(String url : imageAdd) {
           ImageReview imageReview = new ImageReview();
           imageReview.setUrlImage(url);
           imageReview.setReview(review);
           imageReview.setStatus(Status.ACTIVE);
           imageReviewRepository.save(imageReview);
        }
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
                .userResponse(UserMapper.getPublicUserResponse(review.getUser()))
                .fullName(review.getUser().getFullName())
                .rating(review.getRating())
                .avatarUser(avatarUser)
                .comment(review.getComment())
                .images(imageReviewDTOS)
                .createdDate(review.getCreatedAt())
                .build();
    }

    public int calculateRewardPoints(int rating, boolean hasImage) {
        int points = switch (rating) {
            case 5 -> 10;
            case 4 -> 5;
            case 3 -> 2;
            default -> 0;
        };
        if (hasImage) points += 2; // bonus nếu có ảnh thật
        return points;
    }

    private PageResponse<ReviewResponse> getReviewPageResponse(int page, int size, Page<Review> reviews) {
        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(review -> getReviewResponse(review))
                .toList();
        PageResponse<ReviewResponse> response = new PageResponse<>();
        response.setPageNumber(page + 1);
        response.setPageSize(size);
        response.setTotalElements(reviews.getTotalElements());
        response.setTotalPages(reviews.getTotalPages());
        response.setData(reviewResponses);
        return response;
    }

}
