package backend_for_react.backend_for_react.controller.request.Category;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
public class ImageProductVariantUpdateRequest implements Serializable {

    private Long productVarianId;
    
}
