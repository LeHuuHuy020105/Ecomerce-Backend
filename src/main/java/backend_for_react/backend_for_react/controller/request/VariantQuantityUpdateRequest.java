package backend_for_react.backend_for_react.controller.request;

import backend_for_react.backend_for_react.controller.request.VariantAttribute.VariantAttributeRequest;
import lombok.Data;
import java.util.List;

@Data
public class VariantQuantityUpdateRequest {
    private List<VariantAttributeRequest> variantAttributes;
    private Integer quantityChange;
}
