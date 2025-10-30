package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBaseResponse {
    private Long id;
    private String name;
    private BigDecimal listPrice;
    private BigDecimal salePrice;
    private String description;
    private String urlvideo;
    private String urlCoverImage;
    private Integer soldQuantity;
    private Double avgRating;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}
