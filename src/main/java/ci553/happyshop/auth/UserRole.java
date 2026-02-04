package ci553.happyshop.auth;


 //UserRole enum - Complete implementation with permissions

public enum UserRole {
    CUSTOMER("Customer", 1),
    STAFF("Staff", 2),
    ADMIN("Administrator", 3);

    private final String displayName;
    private final int accessLevel;

    UserRole(String displayName, int accessLevel) {
        this.displayName = displayName;
        this.accessLevel = accessLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canAccessWarehouse() {
        return this == STAFF || this == ADMIN;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean canModifyPrices() {
        return this == ADMIN;
    }

    public boolean canDeleteProducts() {
        return this == ADMIN;
    }

    public boolean canAddProducts() {
        return this == STAFF || this == ADMIN;
    }

    public boolean canUpdateStock() {
        return this == STAFF || this == ADMIN;
    }
}