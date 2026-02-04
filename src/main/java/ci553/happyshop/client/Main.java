package ci553.happyshop.client;

import ci553.happyshop.client.auth.LoginController;
import ci553.happyshop.client.auth.LoginModel;
import ci553.happyshop.client.auth.LoginView;
import ci553.happyshop.orderManagement.OrderHub;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main Application Entry Point - Dual Login Display
 *
 * DUAL LOGIN FLOW:
 * 1. Initialize OrderHub (backend system)
 * 2. Show TWO login screens simultaneously:
 *    - Customer Login (left side)
 *    - Warehouse/Staff Login (right side)
 * 3. After successful login, appropriate client launches
 *
 */
public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   🛍️ HappyShop System Starting...");
        System.out.println("========================================");
        System.out.println();
        System.out.println("🔐 Dual Login Display");
        System.out.println("   Customer & Warehouse Logins");
        System.out.println();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("⚙️  Initializing system...");

            // Initialize backend OrderHub system
            initializeOrderHub();
            System.out.println("✅ Backend initialized");

            System.out.println();
            System.out.println("🔒 Starting dual login screens...");
            System.out.println();

            // Start TWO login screens
            startCustomerLoginScreen();
            startWarehouseLoginScreen();

            System.out.println("✅ Both login screens ready");
            System.out.println();
            System.out.println("📌 Default Accounts:");
            System.out.println("   Customer: customer / customer123");
            System.out.println("   Admin: admin / admin123");
            System.out.println("   Staff: staff / staff123");
            System.out.println("   Warehouse: warehouse1 / warehouse123");
            System.out.println("   Picker: picker / picker123");
            System.out.println();
            System.out.println("⚠️  IMPORTANT: Change default passwords after first login!");
            System.out.println("========================================");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start application", e);
            System.err.println("❌ FATAL ERROR: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * Initialize OrderHub backend system
     */
    private void initializeOrderHub() {
        try {
            OrderHub orderHub = OrderHub.getOrderHub();
            orderHub.initializeOrderMap();
            System.out.println("   ✓ OrderHub initialized");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to initialize OrderHub", e);
            System.err.println("   ⚠️  OrderHub initialization failed (non-critical)");
        }
    }

    /**
     * Start Customer Login Screen (LEFT SIDE)
     */
    private void startCustomerLoginScreen() {
        try {
            Stage customerStage = new Stage();
            customerStage.setTitle("HappyShop - Customer Login");

            // Create MVC components
            LoginModel loginModel = new LoginModel();
            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController();

            // Link components
            loginView.loginController = loginController;
            loginController.loginModel = loginModel;
            loginModel.loginView = loginView;

            // Start customer login
            loginView.start(customerStage);

            // Position on left side
            customerStage.setX(100);
            customerStage.setY(100);

            System.out.println("   ✓ Customer login screen launched (LEFT)");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start customer login", e);
            throw new RuntimeException("Cannot start customer login", e);
        }
    }

    /**
     * Start Warehouse/Staff Login Screen (RIGHT SIDE)
     */
    private void startWarehouseLoginScreen() {
        try {
            Stage warehouseStage = new Stage();
            warehouseStage.setTitle("HappyShop - Warehouse/Staff Login");

            // Create MVC components
            LoginModel loginModel = new LoginModel();
            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController();

            // Link components
            loginView.loginController = loginController;
            loginController.loginModel = loginModel;
            loginModel.loginView = loginView;

            // Start warehouse login
            loginView.start(warehouseStage);
            loginView.showWarehouseLoginScreen();

            // Position on right side
            warehouseStage.setX(650);
            warehouseStage.setY(100);

            System.out.println("   ✓ Warehouse login screen launched (RIGHT)");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start warehouse login", e);
            throw new RuntimeException("Cannot start warehouse login", e);
        }
    }
}