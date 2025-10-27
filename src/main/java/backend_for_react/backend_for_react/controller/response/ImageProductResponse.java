package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageProductResponse implements Serializable {
    private String color;
    private List<String> urlImage;
}

