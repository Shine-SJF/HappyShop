package ci553.happyshop;

import ci553.happyshop.client.Main;
import javafx.application.Application;

/**
 * The Launcher class serves as the main entry point of the system.
 *
 * UPDATED: Now launches Main which shows LOGIN FIRST
 *
 * Security Flow:
 * 1. Launcher → Main.java
 * 2. Main.java → Login screen ONLY
 * 3. Login screen → Appropriate client after authentication
 */
public class Launcher {
    /**
     * The main method to start the full system.
     * Launches Main which enforces authentication before any features load.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     🛍️  HappyShop System Launch      ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
        System.out.println("🔐 Security: Authentication Required First");
        System.out.println();

        Application.launch(Main.class, args);
    }
}