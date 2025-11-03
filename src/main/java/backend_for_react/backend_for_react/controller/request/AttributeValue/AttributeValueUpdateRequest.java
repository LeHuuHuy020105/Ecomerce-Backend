package backend_for_react.backend_for_react.controller.request.AttributeValue;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class AttributeValueUpdateRequest implements Serializable {
    private String image;
    private String value;
}
