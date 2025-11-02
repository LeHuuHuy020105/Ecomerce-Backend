package backend_for_react.backend_for_react.controller.request.Product;

import backend_for_react.backend_for_react.common.enums.Gender;
import backend_for_react.backend_for_react.common.enums.ProductStatus;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductUpdateRequest implements Serializable {
    private Long id;
    private String name;
    private BigDecimal listPrice;
    private BigDecimal salePrice;
    private String description;
    private String video;
    private String coverImage;
    private ProductStatus status;
    private Long categoryId;

    private boolean removeVideo;
    private boolean removeCoverImage;


    @AssertTrue(message = "List price must be greater than sale price")
    public boolean isListPriceGreaterThanSalePrice() {
        if (listPrice == null || salePrice == null) {
            return true; // bỏ qua nếu không cập nhật giá
        }
        return listPrice.compareTo(salePrice) > 0;
    }
}
