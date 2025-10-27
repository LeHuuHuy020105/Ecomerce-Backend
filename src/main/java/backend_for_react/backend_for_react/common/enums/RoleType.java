package backend_for_react.backend_for_react.common.enums;

import java.math.BigDecimal;

public enum RoleType {
    ADMIN("Administrator"),
    USER("User"),
    SALES_MANAGER("Sales Manager"),
    WAREHOUSE_MANAGER("Warehouse Manager");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
