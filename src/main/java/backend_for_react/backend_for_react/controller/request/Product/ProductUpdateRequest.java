package backend_for_react.backend_for_react.controller.request.Product;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.common.enums.ProductStatus;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductUpdateRequest implements Serializable {
    private Long id;
    private String description;
    private String video;
    private String coverImage;
    private ProductStatus status;
    private Long categoryId;
}
