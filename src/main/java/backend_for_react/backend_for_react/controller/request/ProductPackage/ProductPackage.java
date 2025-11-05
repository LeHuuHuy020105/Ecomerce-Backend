package backend_for_react.backend_for_react.controller.request.ProductPackage;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPackage {
    @NotNull(message = "product name not be null")
    private String nameProduct;
    @NotNull(message = "length not be null")
    private int length;  // cm
    @NotNull(message = "width not be null")
    private int width;   // cm
    @NotNull(message = "height not be null")
    private int height;  // cm
    @NotNull(message = "weight not be null")
    private int weight;  // gram
    @NotNull(message = "quantity not be null")
    private int quantity;
}
