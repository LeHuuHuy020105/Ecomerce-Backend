package backend_for_react.backend_for_react.controller.request.Attribute;

import lombok.Data;

import java.io.Serializable;

@Data
public class AttributeUpdateRequest implements Serializable {
    String name;
}
