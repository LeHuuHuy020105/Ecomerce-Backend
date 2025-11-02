package backend_for_react.backend_for_react.service;


import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.ImageProduct.ImageProductCreationRequest;
import backend_for_react.backend_for_react.controller.request.ImageProduct.ImageProductDeleteRequest;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.ImageProduct;
import backend_for_react.backend_for_react.model.Product;
import backend_for_react.backend_for_react.repository.ImageProductRepository;
import backend_for_react.backend_for_react.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "IMAGE - PRODUCT - SERVCIE")
@RequiredArgsConstructor
public class ImageProductService {
    private final ImageProductRepository imageProductRepository;
    private final ProductRepository productRepository;

    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_IMAGE_PRODUCT')")
    public void addImageProduct(ImageProductCreationRequest request) {
        log.info("addImageProduct");
        Product product = productRepository.findByIdAndProductStatus(request.getProductId(), ProductStatus.ACTIVE).orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.PRODUCT_NOT_FOUND));
        List<ImageProduct> imageProducts = request.getUrlImages().stream()
                .map(url -> {
                    ImageProduct imageProduct = new ImageProduct();
                    imageProduct.setProduct(product);
                    imageProduct.setUrl(url);
                    imageProduct.setStatus(Status.ACTIVE);
                    return imageProduct;
                })
                .collect(Collectors.toList());

        imageProductRepository.saveAll(imageProducts);
    }

    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_IMAGE_PRODUCT')")
    public void deleteImageProduct(ImageProductDeleteRequest request) {
        log.info("deleteImageProduct");
        Product product = productRepository.findByIdAndProductStatus(request.getProductId(), ProductStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED, MessageError.PRODUCT_NOT_FOUND));
        imageProductRepository.deleteAllByProductIdAndUrls(product, request.getUrlImages());
    }
}
