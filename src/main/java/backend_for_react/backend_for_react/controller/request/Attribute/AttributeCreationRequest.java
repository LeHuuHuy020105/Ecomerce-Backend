package backend_for_react.backend_for_react.controller.request.Attribute;

import backend_for_react.backend_for_react.controller.request.AttributeValue.AttributeValueCreationRequest;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
public class AttributeCreationRequest implements Serializable {
    private String name;
    List<AttributeValueCreationRequest>attributeValue;
}
