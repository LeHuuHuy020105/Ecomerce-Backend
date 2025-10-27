package backend_for_react.backend_for_react.mapper;

import backend_for_react.backend_for_react.controller.response.ProductVariantResponse;
import backend_for_react.backend_for_react.controller.response.VariantAttributeResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.Attribute;
import backend_for_react.backend_for_react.model.ProductVariant;
import backend_for_react.backend_for_react.model.VariantAttributeValue;

import java.util.List;

public class ProductVariantMapper {

    public static ProductVariantResponse getProductVariantResponse(ProductVariant productVariant) {
        return ProductVariantResponse.builder()
                .id(productVariant.getId())
                .price(productVariant.getPrice())
                .quantity(productVariant.getQuantity())
                .sku(productVariant.getSku())
                .variantAttributes(
                        productVariant.getAttributeValues()
                                .stream()
                                .map(variantAttributeValue -> getVariantAttributeResponse(
                                        variantAttributeValue,
                                        variantAttributeValue.getAttributeValue().getAttribute()
                                ))
                                .toList()
                )
                .build();
    }

    public static VariantAttributeResponse getVariantAttributeResponse(
            VariantAttributeValue variantAttributeValue,
            Attribute attribute
    ) {
        return VariantAttributeResponse.builder()
                .id(variantAttributeValue.getAttributeValue().getId())
                .attribute(attribute.getName())
                .value(variantAttributeValue.getAttributeValue().getValue())
                .build();
    }
}

