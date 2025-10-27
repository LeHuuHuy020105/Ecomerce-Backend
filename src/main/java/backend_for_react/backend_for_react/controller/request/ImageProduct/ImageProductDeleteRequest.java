package backend_for_react.backend_for_react.controller.request.ImageProduct;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageProductDeleteRequest implements Serializable {
    List<String> urlImages;
    Long productId;
}
