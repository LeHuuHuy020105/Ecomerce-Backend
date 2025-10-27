package backend_for_react.backend_for_react.controller.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributeResponse {
    Long id;
    String name;
    List<AttributeValueResponse>attributeValue;
}
