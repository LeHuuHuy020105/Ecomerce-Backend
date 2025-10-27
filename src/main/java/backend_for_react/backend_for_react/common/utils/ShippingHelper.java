package backend_for_react.backend_for_react.common.utils;

import backend_for_react.backend_for_react.model.OrderItem;

import java.util.List;

public class ShippingHelper {
    public static int calculateTotalWeight(List<OrderItem> orderItems) {
        int totalWeight = 0;
        for (OrderItem item : orderItems) {
            totalWeight += item.getProductVariant().getWeight() * item.getQuantity();
        }
        return totalWeight;
    }

    public static int calculateTotalLength(List<OrderItem> orderItems) {
        int maxLength = 0;
        for (OrderItem item : orderItems) {
            maxLength = Math.max(maxLength, item.getProductVariant().getLength());
        }
        return maxLength;
    }

    public static int calculateTotalWidth(List<OrderItem> orderItems) {
        int totalWidth = 0;
        for (OrderItem item : orderItems) {
            totalWidth += item.getProductVariant().getWidth();
        }
        return totalWidth;
    }

    public static int calculateTotalHeight(List<OrderItem> orderItems) {
        int totalHeight = 0;
        for (OrderItem item : orderItems) {
            totalHeight += item.getProductVariant().getHeight();
        }
        return totalHeight;
    }
}
