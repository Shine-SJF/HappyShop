package ci553.happyshop.client.emergency;

import ci553.happyshop.utility.WinPosManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Optional;

/**
 * Design Features:
 * - Professsional warrning interface matching PickerView style
 * - Clear visual hierarchy with danger indicators
 * - Confirmation dialog for safety
 * - Singleton pattern for single instance
 *
 * Security:
 * - Only accessible to admin users
 * - Requires confirmation before shutdown
 * - Shows clear warning messages
 *
 */
public class EmergencyExit {
    private final int WIDTH = 400;
    private final int HEIGHT = 550;
    private static EmergencyExit emergencyExit;

    // Reference to PickerUIStyles colors (shared design system)
    private static final String ERROR = "#EF4444";
    private static final String ERROR_DARK = "#DC2626";
    private static final String BACKGROUND = "#F9FAFB";
    private static final String SURFACE = "#FFFFFF";
    private static final String BORDER = "#E5E7EB";
    private static final String TEXT_PRIMARY = "#111827";
    private static final String TEXT_SECONDARY = "#6B7280";
    private static final String FONT_PRIMARY = "'Segoe UI', -apple-system, BlinkMacSystemFont, 'Roboto', sans-serif";


     //Gets the singleton instance of EmergencyExit
      //Used by Main class and admin controls

    public static EmergencyExit getEmergencyExit() {
        if (emergencyExit == null)
            emergencyExit = new EmergencyExit();
        return emergencyExit;
    }


     //Private constructor creates the modern emergency shutdown window.
     //Features confirmation dialog and professional warning UI.

    private EmergencyExit() {
        // Main container
        VBox mainContainer = new VBox();
        mainContainer.setStyle(
                "-fx-background-color: " + BACKGROUND + "; " +
                        "-fx-padding: 20px;"
        );
        mainContainer.setSpacing(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);

        // Header section
        VBox header = createHeader();

        // Warning card
        VBox warningCard = createWarningCard();

        // Action button
        Button btnEmergencyShutdown = createShutdownButton();

        // Footer info
        VBox footer = createFooter();

        mainContainer.getChildren().addAll(header, warningCard, btnEmergencyShutdown, footer);

        // Create scene and stage
        Scene scene = new Scene(mainContainer, WIDTH, HEIGHT);
        Stage window = new Stage();
        window.setScene(scene);
        window.setTitle("⚠️ Emergency Exit");

        // Prevent accidental closing
        window.setOnCloseRequest(event -> {
            event.consume(); // Don't close on X button
        });

        // Register with position manager
        WinPosManager.registerWindow(window, WIDTH, HEIGHT);
        window.show();
    }


     //creates the header section.

    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);

        // Main title with warning icon
        Label laTitle = new Label("⚠️ Emergency Exit");
        laTitle.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 28px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: " + ERROR + ";"
        );

        // Subtitle
        Label laSubtitle = new Label("Admin System Control");
        laSubtitle.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: 500; " +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";"
        );

        // Admin badge
        Label adminBadge = new Label("🔐 ADMIN ONLY");
        adminBadge.setStyle(
                "-fx-background-color: " + ERROR + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 11px; " +
                        "-fx-font-weight: 700; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-padding: 5px 14px;"
        );

        header.getChildren().addAll(laTitle, laSubtitle, adminBadge);
        return header;
    }


      //Creates the warning card with critical information.

    private VBox createWarningCard() {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: " + SURFACE + "; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-border-color: " + ERROR + "; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 12px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(239, 68, 68, 0.15), 8, 0, 0, 4);"
        );

        // Warning title
        Label warningTitle = new Label("⚠️ CRITICAL WARNING");
        warningTitle.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: " + ERROR + ";"
        );

        // Warning messages
        VBox warningMessages = new VBox(10);
        warningMessages.getChildren().addAll(
                createWarningItem("This will immediately shut down the entire HappyShop system"),
                createWarningItem("All active customer sessions will be terminated"),
                createWarningItem("Order processing will be interrupted"),
                createWarningItem("Warehouse operations will halt"),
                createWarningItem("Use only in emergency situations")
        );

        // Divider
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: " + BORDER + ";");

        // Confirmation note
        Label confirmNote = new Label("You will be asked to confirm before shutdown");
        confirmNote.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-style: italic; " +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";"
        );

        card.getChildren().addAll(warningTitle, warningMessages, divider, confirmNote);
        return card;
    }

     //Creates a single warning item.

    private HBox createWarningItem(String text) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.TOP_LEFT);

        // Bullet point
        Label bullet = new Label("•");
        bullet.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 16px; " +
                        "-fx-text-fill: " + ERROR + "; " +
                        "-fx-font-weight: bold;"
        );

        // Warning text
        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(300);
        textLabel.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 13px; " +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );

        item.getChildren().addAll(bullet, textLabel);
        return item;
    }

     //Creates the emergency shutdown button

    private Button createShutdownButton() {
        Button btn = new Button("🛑 EMERGENCY SHUTDOWN");
        btn.setPrefWidth(320);
        btn.setPrefHeight(60);
        btn.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: " + ERROR + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(239, 68, 68, 0.3), 8, 0, 0, 4);"
        );

        // Hover effect
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: " + ERROR_DARK + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(220, 38, 38, 0.4), 12, 0, 0, 6); " +
                        "-fx-scale-x: 1.02; " +
                        "-fx-scale-y: 1.02;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-color: " + ERROR + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(239, 68, 68, 0.3), 8, 0, 0, 4);"
        ));

        // Confirmation dialog on click
        btn.setOnAction(event -> showConfirmationDialog());

        return btn;
    }

     //Creates the footer section

    private VBox createFooter() {
        VBox footer = new VBox(8);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 0, 0));

        Label infoLabel = new Label("This control is only available to administrators");
        infoLabel.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 11px; " +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";"
        );

        Label tipLabel = new Label("💡 Close this window to keep the system running");
        tipLabel.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: 500; " +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";"
        );

        footer.getChildren().addAll(infoLabel, tipLabel);
        return footer;
    }

     //shows a confirmation dialog before shutting down

    private void showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("⚠️ Confirm Emergency Shutdown");
        alert.setHeaderText("Are you absolutely sure?");
        alert.setContentText(
                "This will immediately shut down the entire HappyShop system.\n\n" +
                        "• All customer sessions will end\n" +
                        "• Order processing will stop\n" +
                        "• All windows will close\n\n" +
                        "This action cannot be undone!"
        );

        // Style the alert
        alert.getDialogPane().setStyle(
                "-fx-background-color: " + SURFACE + "; " +
                        "-fx-font-family: " + FONT_PRIMARY + ";"
        );

        // Customize buttons
        ButtonType yesButton = new ButtonType("Yes, Shut Down Now");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());
        alert.getButtonTypes().setAll(yesButton, cancelButton);

        // Show confirmation and handle response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            performShutdown();
        }
    }

     //Performs the actual system shutdown

    private void performShutdown() {
        // Show final warning
        Alert finalAlert = new Alert(Alert.AlertType.WARNING);
        finalAlert.setTitle("🛑 Shutting Down");
        finalAlert.setHeaderText("Emergency shutdown initiated");
        finalAlert.setContentText("HappyShop system is shutting down now...");

        finalAlert.getDialogPane().setStyle(
                "-fx-background-color: " + SURFACE + "; " +
                        "-fx-font-family: " + FONT_PRIMARY + ";"
        );

        // Non-blocking alert
        finalAlert.show();

        // Delay shutdown slightly to show the message
        new Thread(() -> {
            try {
                Thread.sleep(1500); // 1.5 seconds delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Gracefully exit JavaFX
            Platform.exit();

            // Forcefully shut down JVM (in case there are non-JavaFX threads)
            System.exit(0);
        }).start();
    }
}