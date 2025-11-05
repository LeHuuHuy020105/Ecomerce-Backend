package backend_for_react.backend_for_react.controller.request.ProductPackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ProductPackageResponse {
    private int lengthTotal;
    private int widthTotal;
    private int heightTotal;
    private int weightTotal;
    private int serviceTypeId;
    private List<ItemResponse> itemResponses;

    @Data
    @AllArgsConstructor
    @Builder
    public static class ItemResponse{
        private String nameProduct;
        private int length;
        private int width;
        private int height;
        private int weight;
        private int quantity;
    }
}
