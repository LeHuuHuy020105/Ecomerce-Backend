    package backend_for_react.backend_for_react.mapper;

    import backend_for_react.backend_for_react.controller.response.ProductBaseResponse;
    import backend_for_react.backend_for_react.model.Product;
    import backend_for_react.backend_for_react.model.Review;

    public class ProductMapper {
        public static ProductBaseResponse toBaseResponse(Product product) {
            double avgRating = 0.0;

            if (product.getReviews() != null && !product.getReviews().isEmpty()) {
                avgRating = product.getReviews().stream()
                        .filter(review -> review.getRating() != null)
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);
            }

            return ProductBaseResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .listPrice(product.getListPrice())
                    .salePrice(product.getSalePrice())
                    .description(product.getDescription())
                    .urlvideo(product.getUrlvideo())
                    .urlCoverImage(product.getUrlCoverImage())
                    .soldQuantity(product.getSoldQuantity())
                    .avgRating(avgRating)
                    .status(product.getProductStatus())
                    .createdAt(product.getCreatedAt())
                    .updateAt(product.getUpdatedAt())
                    .build();
        }
}


