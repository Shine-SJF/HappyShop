package ci553.happyshop.client.auth;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Login Launcher - Two-Stage Authentication Entry Point
 *
 * Stage 1: User selects login type (Customer or Warehouse)
 * Stage 2: User enters credentials for selected type
 *
 * Flow:
 * 1. Main selection screen → Choose Customer or Warehouse
 * 2. Customer path → Customer login/registration → Customer interface only
 * 3. Warehouse path → Role selection (Staff/Admin) → Warehouse interface with permissions
 */
public class LoginLauncher extends Application {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   🛍️ HappyShop Authentication System");
        System.out.println("========================================");
        System.out.println();
        System.out.println("Starting two-stage login system...");
        System.out.println();
        System.out.println("Stage 1: Select login type (Customer/Warehouse)");
        System.out.println("Stage 2: Enter credentials");
        System.out.println();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create MVC components for login system
            LoginModel loginModel = new LoginModel();
            LoginView loginView = new LoginView();
            LoginController loginController = new LoginController();

            // Link components (MVC pattern)
            loginView.loginController = loginController;
            loginController.loginModel = loginModel;
            loginModel.loginView = loginView;

            // Start with main selection screen (Stage 1)
            loginView.start(primaryStage);

            System.out.println("✅ Login system ready");
            System.out.println();
            System.out.println("Default Admin Credentials:");
            System.out.println("  Username: admin");
            System.out.println("  Password: admin123");
            System.out.println();
            System.out.println("⚠️  Please change the admin password after first login!");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("❌ Failed to start login system");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}