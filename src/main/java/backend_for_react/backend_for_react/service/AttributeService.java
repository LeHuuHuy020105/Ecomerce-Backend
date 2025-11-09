package backend_for_react.backend_for_react.service;


import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.Attribute.AttributeCreationRequest;
import backend_for_react.backend_for_react.controller.request.Attribute.AttributeUpdateRequest;
import backend_for_react.backend_for_react.controller.request.AttributeValue.AttributeValueCreationRequest;
import backend_for_react.backend_for_react.controller.request.AttributeValue.AttributeValueUpdateRequest;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.model.*;
import backend_for_react.backend_for_react.repository.AttributeRepository;
import backend_for_react.backend_for_react.repository.AttributeValueRepository;
import backend_for_react.backend_for_react.repository.ProductRepository;
import backend_for_react.backend_for_react.repository.ProductVariantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j(topic = "ATTRIBUTE-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttributeService {
    AttributeRepository attributeRepository;
    AttributeValueRepository attributeValueRepository;
    ProductRepository productRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_PRODUCT_ATTRIBUTE')")
    public void add(AttributeCreationRequest request , Long productId){
        Product product = productRepository.findByIdAndProductStatus(productId, ProductStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.PRODUCT_NOT_FOUND));
        Attribute attribute = new Attribute();
        attribute.setName(request.getName());
        attribute.setProduct(product);
        attribute.setStatus(Status.ACTIVE);
        List<AttributeValue> attributeValues = request.getAttributeValue().stream()
                .map(attributeValueRequest -> {
                    AttributeValue attributeValue = AttributeValue.builder()
                            .attribute(attribute)
                            .value(attributeValueRequest.getValue())
                            .status(Status.ACTIVE)
                            .urlImage(attributeValueRequest.getImage())
                            .build();
                    return attributeValue;
                }).toList();
        attribute.setValues(attributeValues);
        attributeRepository.save(attribute);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PRODUCT_ATTRIBUTE')")
    public void update(Long productId, Long attributeId, AttributeUpdateRequest req) {
        Attribute attribute = attributeRepository.findByIdAndStatus(attributeId,Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST,"Attribute not found"));
        if (!attribute.getProduct().getId().equals(productId))
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Attribute not part of product");

        if(req.getName() != null) attribute.setName(req.getName());
        attributeRepository.save(attribute);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT_ATTRIBUTE')")
    public void delete(Long productId, Long attributeId) {
        Attribute attribute = attributeRepository.findByIdAndStatus(attributeId,Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Attribute not found"));
        if (!attribute.getProduct().getId().equals(productId))
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Attribute not part of product");

        attribute.setStatus(Status.INACTIVE);
        // Cập nhật các value bên trong
        List<AttributeValue> values = attributeValueRepository.findAllByAttribute(attribute);
        if (values != null && !values.isEmpty()) {
            values.forEach(v -> {
                v.setStatus(Status.INACTIVE);

                // Với mỗi AttributeValue, tìm tất cả VariantAttributeValue và set INACTIVE
                List<VariantAttributeValue> variantValues = v.getVariantAttributeValue();
                if (variantValues != null && !variantValues.isEmpty()) {
                    variantValues.forEach(vav -> vav.setStatus(Status.INACTIVE));
                }
            });
        }
        attributeRepository.save(attribute);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_PRODUCT_ATTRIBUTE_VALUE')")
    public void addAttributeValue(Long productId, Long attributeId, AttributeValueCreationRequest request){
        Product product = productRepository.findByIdAndProductStatus(productId, ProductStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.PRODUCT_NOT_FOUND));
        Attribute attribute = attributeRepository.findByIdAndStatus(attributeId,Status.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"Attribute not found"));
        if(attribute.getProduct() != product){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Attribute not part of the product");
        }
        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setStatus(Status.ACTIVE);
        attributeValue.setAttribute(attribute);
        attributeValue.setValue(request.getValue());
        attributeValue.setUrlImage(request.getImage());
        attributeValueRepository.save(attributeValue);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PRODUCT_ATTRIBUTE_VALUE')")
        public void updateAttributeValue(Long productId, Long attributeValueId, AttributeValueUpdateRequest req) {
        AttributeValue attributeValue = attributeValueRepository.findByIdAndStatus(attributeValueId,Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "Attribute value not found"));
        if (!attributeValue.getAttribute().getProduct().getId().equals(productId))
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Attribute value not part of product");

        if(req.getValue() != null) attributeValue.setValue(req.getValue());
        if(req.getImage() != null) attributeValue.setUrlImage(req.getImage());
        attributeValueRepository.save(attributeValue);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT_ATTRIBUTE_VALUE')")
    public void deleteAttributeValue(Long productId, Long attributeValueId) {
        AttributeValue attributeValue = attributeValueRepository.findByIdAndStatus(attributeValueId,Status.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "Attribute value not found"));
        if (!attributeValue.getAttribute().getProduct().getId().equals(productId))
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Variant không thuộc sản phẩm");

        attributeValue.setStatus(Status.INACTIVE);

        List<VariantAttributeValue> variantAttributeValues = attributeValue.getVariantAttributeValue();
        if (variantAttributeValues != null && !variantAttributeValues.isEmpty()) {
            variantAttributeValues.forEach(vav -> vav.setStatus(Status.INACTIVE));
        }
        attributeValueRepository.save(attributeValue);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT_ATTRIBUTE_VALUE_IMAGE')")
    @Transactional
    public void deleteImageAttributeValue(Long productId, Long attributeValueId){
        Product product = productRepository.findByIdAndProductStatus(productId, ProductStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, MessageError.PRODUCT_NOT_FOUND));
        AttributeValue attributeValue = attributeValueRepository.findByIdAndStatus(attributeValueId,Status.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"Attribute value not found"));
        if(attributeValue.getAttribute().getProduct() != product){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Attribute value not part of the product");
        }
        attributeValue.setUrlImage(null);
        attributeValueRepository.save(attributeValue);
    }
}
