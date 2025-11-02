package backend_for_react.backend_for_react.mapper;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.response.ProductVariantResponse;
import backend_for_react.backend_for_react.controller.response.VariantAttributeResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.Attribute;
import backend_for_react.backend_for_react.model.AttributeValue;
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
                .weight(productVariant.getWeight())
                .width(productVariant.getWidth())
                .length(productVariant.getLength())
                .height(productVariant.getHeight())
                .variantAttributes(
                        productVariant.getAttributeValues().stream()
                                .filter(vav -> vav.getStatus() == Status.ACTIVE
                                        && vav.getAttributeValue().getStatus() == Status.ACTIVE)
                                .map(vav -> getVariantAttributeResponse(
                                        vav,
                                        vav.getAttributeValue().getAttribute()
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

    public static String buildVariantName(ProductVariant productVariant){
        StringBuilder result = new StringBuilder();
        for(VariantAttributeValue variantAttributeValue : productVariant.getAttributeValues()){
            result.append(variantAttributeValue.getAttributeValue().getAttribute().getName());
            result.append(":");
            result.append(variantAttributeValue.getAttributeValue().getValue());
            result.append(",");
        }
        return result.toString();
    }
}

