package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ProductVariantResponse implements Serializable {
    private Long id;
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;
    private BigDecimal price;
    private Integer quantity;
    private String sku;
    private List<VariantAttributeResponse>variantAttributes;
}
