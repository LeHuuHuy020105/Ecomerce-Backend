package backend_for_react.backend_for_react.common.utils;

import backend_for_react.backend_for_react.controller.request.ProductPackage.ProductPackage;

import java.util.List;

public class ShippingHelper {
    /** Tổng khối lượng (gram) = sum(weight × quantity) */
    public static int calculateTotalWeight(List<ProductPackage> packages) {
        int total = 0;
        for (ProductPackage pkg : packages) {
            total += pkg.getWeight() * pkg.getQuantity();
        }
        return total;
    }

    /**
     * Chiều dài trung bình có trọng số (theo quantity)
     * - Thay vì lấy max, ta lấy trung bình có trọng số để phản ánh kiện hỗn hợp
     */
    public static int calculateAverageLength(List<ProductPackage> packages) {
        int total = 0, totalQty = 0;
        for (ProductPackage pkg : packages) {
            total += pkg.getLength() * pkg.getQuantity();
            totalQty += pkg.getQuantity();
        }
        return totalQty == 0 ? 0 : Math.round((float) total / totalQty);
    }

    /** Chiều rộng trung bình có trọng số */
    public static int calculateAverageWidth(List<ProductPackage> packages) {
        int total = 0, totalQty = 0;
        for (ProductPackage pkg : packages) {
            total += pkg.getWidth() * pkg.getQuantity();
            totalQty += pkg.getQuantity();
        }
        return totalQty == 0 ? 0 : Math.round((float) total / totalQty);
    }

    /** Chiều cao trung bình có trọng số */
    public static int calculateAverageHeight(List<ProductPackage> packages) {
        int total = 0, totalQty = 0;
        for (ProductPackage pkg : packages) {
            total += pkg.getHeight() * pkg.getQuantity();
            totalQty += pkg.getQuantity();
        }
        return totalQty == 0 ? 0 : Math.round((float) total / totalQty);
    }

    /**
     * Xác định loại dịch vụ GHN (2 = hàng nhẹ, 5 = hàng nặng)
     * Dựa vào quy tắc của GHN (cân nặng & thể tích quy đổi)
     */
    public static int determineServiceTypeId(int weightGram, int lengthCm, int widthCm, int heightCm) {
        // Quy đổi thể tích sang kg
        double volumetricWeightKg = (lengthCm * widthCm * heightCm) / 5000.0;
        double actualWeightKg = weightGram / 1000.0;

        // GHN quy định: >15kg là hàng nặng
        if (actualWeightKg > 15.0 || volumetricWeightKg > 15.0) {
            return 5; // hàng nặng
        } else {
            return 2; // hàng nhẹ
        }
    }
}
