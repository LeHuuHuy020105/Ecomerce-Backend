package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.Category.CategoryCreationRequest;
import backend_for_react.backend_for_react.controller.request.Category.CategoryUpdateRequest;
import backend_for_react.backend_for_react.controller.request.Category.ImageProductVariantUpdateRequest;
import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantCreationRequest;
import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantUpdateRequest;
import backend_for_react.backend_for_react.controller.response.CategoryResponse;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.ProductVariantResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.model.ProductVariant;
import backend_for_react.backend_for_react.repository.ProductRepository;
import backend_for_react.backend_for_react.repository.ProductVariantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static backend_for_react.backend_for_react.service.impl.ProductService.generateSku;

@Slf4j(topic = "PERMISSION-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantService {
    ProductVariantRepository productVariantRepository;
    ProductRepository productRepository;

    public void add(ProductVariantCreationRequest request) {
        Product product = productRepository.findByIdAndProductStatus(request.getProductId(), ProductStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.PRODUCT_NOT_FOUND));
        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .height(request.getHeight())
                .width(request.getWidth())
                .length(request.getLength())
                .weight(request.getWeight())
                .price(request.getPrice())
                .quantity(0)
                .status(Status.ACTIVE)
                .sku(generateSku(product, request))
                .build();
        productVariantRepository.save(productVariant);
    }

    public void update(ProductVariantUpdateRequest request) {

    }

    public void delete(Long productVariantId) {
        ProductVariant productVariant = productVariantRepository.findByIdAndStatus(productVariantId,Status.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,MessageError.PRODUCT_VARIANT_NOT_FOUND));
        productVariant.setStatus(Status.INACTIVE);
        productVariantRepository.save(productVariant);
    }
}
