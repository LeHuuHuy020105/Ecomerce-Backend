package backend_for_react.backend_for_react.controller.request.ProductVariant;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVariantUpdateRequest implements Serializable {
    private Long id;
    private String size;
    private String color;
    private Integer stock;
    private List<Long> deletedImageIds;
    private List<MultipartFile>newImages;
}
