package ci553.happyshop.client.warehouse;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * AlertSimulator for displaying alert messages
 */
public class AlertSimulator {
    public WarehouseView warehouseView;
    private Stage alertWindow;


    /**
     * Shows an error message alert
     */
    public void showErrorMsg(String message) {
        showAlert(message, "Error", Alert.AlertType.ERROR);
    }

    /**
     * Shows an informational alert message
     */
    public void showInfoMsg(String message, String title) {
        showAlert(message, title, Alert.AlertType.INFORMATION);
    }

    /**
     * Shows an alert with specified type
     */
    private void showAlert(String message, String title, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Apply modern styling
        alert.getDialogPane().setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-padding: 20px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        alert.showAndWait();
    }


    /**
     * Closes the alert simulator window if open
     */
    public void closeAlertSimulatorWindow() {
        if (alertWindow != null && alertWindow.isShowing()) {
            alertWindow.close();
        }
    }
}