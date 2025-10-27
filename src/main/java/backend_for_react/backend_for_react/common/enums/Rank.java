package backend_for_react.backend_for_react.common.enums;

import java.math.BigDecimal;

public enum Rank {
    BRONZE(1, new BigDecimal("0")),              // mặc định
    SILVER(2, new BigDecimal("1000000")),        // từ 1 triệu
    GOLD(3, new BigDecimal("5000000")),          // từ 5 triệu
    PLATINUM(4, new BigDecimal("10000000"));     // từ 10 triệu

    private final int level;
    private final BigDecimal minSpent;

    Rank(int level, BigDecimal minSpent) {
        this.level = level;
        this.minSpent = minSpent;
    }

    public BigDecimal getMinSpent() {
        return minSpent;
    }

    public int getLevel() {
        return level;
    }

    // 🔹 Xác định rank dựa trên tổng chi tiêu (BigDecimal)
    public static Rank fromTotalSpent(BigDecimal totalSpent) {
        Rank result = BRONZE;
        for (Rank rank : Rank.values()) {
            if (totalSpent.compareTo(rank.getMinSpent()) >= 0) {
                result = rank;
            }
        }
        return result;
    }
}
