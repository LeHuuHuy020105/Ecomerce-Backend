package backend_for_react.backend_for_react.controller.request.ProductVariant;

import backend_for_react.backend_for_react.controller.request.VariantAttribute.VariantAttributeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVariantCreationRequest implements Serializable {
    private Long productId;

    @NotNull(message = "Weight must not be null")
    @Positive(message = "Weight must be greater than 0")
    private Integer weight;

    @NotNull(message = "Length must not be null")
    @Positive(message = "Length must be greater than 0")
    private Integer length;

    @NotNull(message = "Width must not be null")
    @Positive(message = "Width must be greater than 0")
    private Integer width;

    @NotNull(message = "Height must not be null")
    @Positive(message = "Height must be greater than 0")
    private Integer height;

    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price; // Giá


    @Valid
    private List<VariantAttributeRequest> variantAttributes; // Các thuộc tính
}
