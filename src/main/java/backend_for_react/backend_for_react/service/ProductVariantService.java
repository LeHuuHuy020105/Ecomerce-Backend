package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.controller.request.Category.CategoryCreationRequest;
import backend_for_react.backend_for_react.controller.request.Category.CategoryUpdateRequest;
import backend_for_react.backend_for_react.controller.request.Category.ImageProductVariantUpdateRequest;
import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantCreationRequest;
import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantUpdateRequest;
import backend_for_react.backend_for_react.controller.response.CategoryResponse;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.ProductVariantResponse;
import backend_for_react.backend_for_react.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductVariantService {
    PageResponse<ProductVariantResponse> findAll(String keyword , String sort, int page, int size);
    Long save(ProductVariantCreationRequest req ,  List<MultipartFile> images);
    void update(ProductVariantUpdateRequest req);
    void delete(Long id);
    ProductVariantResponse getProductVariantById(Long productVariantId);
    List<ProductVariantResponse> getProductVariantByProductId (Product product);
}
