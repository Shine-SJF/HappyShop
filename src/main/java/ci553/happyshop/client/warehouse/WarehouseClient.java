package ci553.happyshop.client.warehouse;

import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.storageAccess.DatabaseRWFactory;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Initialization Order:
 * 1. Create all objects (View, Controller, Model, DatabaseRW)
 * 2. Link them together (BEFORE starting view)
 * 3. Start the view window
 * 4. Create dependent windows (AFTER view is started)
 * 5. Link dependent windows
 */
public class WarehouseClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the Warehouse client's Model, View, and Controller
     * Links them together for communication.
     * Creates DatabaseRW instance and injects it into Model.
    */
    @Override
    public void start(Stage window) {
        System.out.println("=== Starting Warehouse Client ===");

        try {
            // STEP 1: Create all main objects
            System.out.println("Step 1: Creating main components...");
            WarehouseView view = new WarehouseView();
            WarehouseController controller = new WarehouseController();
            WarehouseModel model = new WarehouseModel();
            DatabaseRW databaseRW = DatabaseRWFactory.createDatabaseRW();

            System.out.println("  ✓ View created: " + (view != null));
            System.out.println("  ✓ Controller created: " + (controller != null));
            System.out.println("  ✓ Model created: " + (model != null));
            System.out.println("  ✓ DatabaseRW created: " + (databaseRW != null));

            // STEP 2: Link components together (CRITICAL ORDER)
            System.out.println("Step 2: Linking components...");
            // Link view to controller

            view.controller = controller;
            System.out.println("  ✓ View → Controller linked");

            // Link controller to model
            controller.model = model;
            System.out.println("  ✓ Controller → Model linked");

            // Link model to view
            model.view = view;
            System.out.println("  ✓ Model → View linked");

            // Link model to database
            model.databaseRW = databaseRW;
            System.out.println("  ✓ Model → Database linked");

            // STEP 3: Verify all links before starting
            if (!verifyLinks(view, controller, model)) {
                System.err.println("ERROR: Component linking failed!");
                return;
            }

            // STEP 4: Start the warehouse interface
            System.out.println("Step 3: Starting warehouse interface...");
            view.start(window);
            System.out.println("  ✓ Warehouse window displayed");

            // STEP 5: Create dependent windows (AFTER view is started)
            System.out.println("Step 4: Creating dependent windows...");
            HistoryWindow historyWindow = new HistoryWindow();
            AlertSimulator alertSimulator = new AlertSimulator();
            System.out.println("  ✓ HistoryWindow created");
            System.out.println("  ✓ AlertSimulator created");

            // STEP 6: Link dependent windows
            System.out.println("Step 5: Linking dependent windows...");
            model.historyWindow = historyWindow;
            model.alertSimulator = alertSimulator;
            historyWindow.warehouseView = view;
            alertSimulator.warehouseView = view;
            System.out.println("  ✓ Dependent windows linked");

            System.out.println("=== Warehouse Client Started Successfully ===");
            System.out.println("Ready to use!");
            System.out.println("-------------------------------------------");

        } catch (Exception e) {
            System.err.println("FATAL ERROR: Failed to start Warehouse Client");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

            // Show error dialog to user
            showErrorDialog("Warehouse Startup Failed",
                    "Failed to start Warehouse Client.\n\n" +
                            "Possible causes:\n" +
                            "- Database not initialized (run SetDatabase.java first)\n" +
                            "- Missing image files\n" +
                            "- Component initialization error\n\n" +
                            "Error: " + e.getMessage());
        }
    }

    /**
     * Verify all component links are properly established
     *
     *  view The warehouse view
     *  controller The warehouse controller
     *  model The warehouse model
     *  true if all links valid, false otherwise
     */
    private boolean verifyLinks(WarehouseView view, WarehouseController controller, WarehouseModel model) {
        boolean valid = true;

        if (view.controller == null) {
            System.err.println("ERROR: view.controller is NULL!");
            valid = false;
        }

        if (controller.model == null) {
            System.err.println("ERROR: controller.model is NULL!");
            valid = false;
        }

        if (model.view == null) {
            System.err.println("ERROR: model.view is NULL!");
            valid = false;
        }

        if (model.databaseRW == null) {
            System.err.println("ERROR: model.databaseRW is NULL!");
            valid = false;
        }

        if (valid) {
            System.out.println("  ✓ All component links verified");
        }

        return valid;
    }

     //Show error dialog to user

    private void showErrorDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}