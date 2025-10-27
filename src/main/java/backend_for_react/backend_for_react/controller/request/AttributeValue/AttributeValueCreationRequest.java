package backend_for_react.backend_for_react.controller.request.AttributeValue;

import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantCreationRequest;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
public class AttributeValueCreationRequest implements Serializable {
    private String image;
    private String value;
}
