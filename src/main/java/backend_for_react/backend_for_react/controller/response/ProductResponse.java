package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse implements Serializable {
    private Long id;
    private String name;
    private String description;
    private BigDecimal listPrice;
    private BigDecimal salePrice;
    private ProductStatus productStatus;
    private Long categoryId;
    private String video;
    private String coverImage;
    private List<String> imageProduct;
    private Integer soldQuantity;
    private Double avgRating;
    private List<AttributeResponse>attributes;
    private List<ProductVariantResponse>productVariant;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
