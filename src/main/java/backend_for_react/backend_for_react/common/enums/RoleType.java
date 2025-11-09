package backend_for_react.backend_for_react.common.enums;

import java.math.BigDecimal;

public enum RoleType {
    ADMIN("Administrator"),
    USER("User"),
    WAREHOUSE_STAFF("Warehouse staff"),
    ORDER_STAFF("Order staff");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
