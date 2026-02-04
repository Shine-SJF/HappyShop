package ci553.happyshop.client.warehouse;

import ci553.happyshop.auth.AuthenticationManager;
import ci553.happyshop.auth.User;

import java.io.IOException;
import java.sql.SQLException;

/**
 * STAFF Permissions:
 * - ✅ Search products
 * - ✅ View stock dashboard
 * - ✅ Add/subtract stock (canUpdateStock)
 * - ✅ Add new products
 * - ❌ CANNOT edit prices
 * - ❌ CANNOT delete products
 *
 * ADMIN Permissions:
 * - ✅ All staff permissions
 * - ✅ Edit prices
 * - ✅ Delete products
 * - ✅ Full control
 */
public class WarehouseController {
    public WarehouseModel model;

    /**
     * Process user actions with role-based permission checks
     */
    void process(String action) throws SQLException, IOException {
        if (model == null) {
            System.err.println("ERROR: Model is not initialized!");
            return;
        }

        // Get current user
        User currentUser = AuthenticationManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            System.err.println("ERROR: No user logged in!");
            showAccessDenied("You must be logged in to perform this action.");
            return;
        }

        // Normalize action string
        String normalizedAction = normalizeAction(action);
        System.out.println("Controller processing action: " + normalizedAction +
                " | User: " + currentUser.getUsername() +
                " | Role: " + currentUser.getRole());

        // Route action with permission checks
        switch (normalizedAction) {
            case "LOAD_ALL":
                // All warehouse users can load products
                model.doLoadAll();
                break;

            case "SEARCH":
                // All warehouse users can search
                model.doSearch();
                break;

            case "EDIT":
                // All warehouse users can initiate edit (but price editing is restricted in model)
                model.doEdit();
                break;

            case "DELETE":
                // ADMIN ONLY - Delete products
                if (currentUser.canDeleteProducts()) {
                    model.doDelete();
                } else {
                    showAccessDenied("Only administrators can delete products.");
                    System.out.println("⛔ Access denied: " + currentUser.getUsername() +
                            " (STAFF) cannot delete products");
                }
                break;

            case "ADD":
            case "SUB":
                // ⭐ FIXED: Both STAFF and ADMIN can modify stock using canUpdateStock()
                if (currentUser.canUpdateStock()) {
                    model.doChangeStockBy(normalizedAction.equals("ADD") ? "add" : "sub");
                    System.out.println("✅ Stock modification allowed for " + currentUser.getRole());
                } else {
                    showAccessDenied("You don't have permission to modify stock.");
                    System.out.println("⛔ Access denied: " + currentUser.getUsername() +
                            " cannot modify stock");
                }
                break;

            case "SUBMIT":
                // Submit with permission validation
                model.doSummit();

                // Refresh dashboard after submit
                if (model.view != null) {
                    model.view.refreshDashboard();
                }
                break;

            case "CANCEL":
                model.doCancel();
                break;

            case "DASHBOARD":
                // All warehouse users can view dashboard
                System.out.println("Dashboard opened by " + currentUser.getRole());
                break;

            default:
                System.out.println("Unknown action: " + action + " (normalized: " + normalizedAction + ")");
                break;
        }
    }

    /**
     * Normalize action strings to handle emoji buttons and variations
     */
    private String normalizeAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            return "UNKNOWN";
        }

        // Remove emojis and extra spaces, convert to uppercase
        String normalized = action.trim().toUpperCase();

        // Handle emoji buttons and text variations
        if (normalized.contains("🔎") || normalized.contains("🔍") || normalized.contains("SEARCH")) {
            return "SEARCH";
        }
        if (normalized.equals("➕") || normalized.equals("+")) {
            return "ADD";
        }
        if (normalized.equals("➖") || normalized.equals("-") || normalized.equals("−")) {
            return "SUB";
        }
        if (normalized.contains("📊") || normalized.contains("DASHBOARD")) {
            return "DASHBOARD";
        }
        if (normalized.contains("✏️") || normalized.contains("EDIT")) {
            return "EDIT";
        }
        if (normalized.contains("🗑️") || normalized.contains("DELETE")) {
            return "DELETE";
        }
        if (normalized.contains("💾") || normalized.contains("SUBMIT")) {
            return "SUBMIT";
        }
        if (normalized.contains("CANCEL")) {
            return "CANCEL";
        }
        if (normalized.equals("LOAD_ALL")) {
            return "LOAD_ALL";
        }

        // Return original if no match
        return normalized;
    }

    /**
     * Shows access denied message to user
     */
    private void showAccessDenied(String message) {
        if (model != null && model.alertSimulator != null) {
            model.alertSimulator.showErrorMsg("⛔ Access Denied\n\n" + message);
        } else {
            System.err.println("⛔ Access Denied: " + message);
        }
    }
}