package ci553.happyshop.client.picker;

import ci553.happyshop.utility.WinPosManager;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The Order Picker window is for staff to prepare customer orders.
 * It contains two root views:
 * 1. vbOrderMapRoot - the default view, displaying available orders awaiting assignment.
 * 2. vbOrderDetailRoot - displayed once a picker is assigned an order, allowing them to view
 *    and prepare the order.
 *
 * The window initially shows the orderMapRoot.
 * Once an orrder is assigned to a picker, the view switches to orderDetailRoot.
 * The view switches to orderMapRoot for the next task after the order is prepared and collected by customer.
 *
 * Design Features:
 * - Modern card-based layout
 * - Clear visual hierarchy with status indicators
 * - Professional amber/orange color theme for action/fulfillment
 * - Consistent with CustomerView and WarehouseView design patterns
 *
 */
public class PickerView {
    public PickerController pickerController;

    private final int WIDTH = 800;
    private final int HEIGHT = 650;

    private Scene scene;
    private VBox vbOrderMapRoot;
    private VBox vbOrderDetailRoot;

    // UI Components for Order Map View
    private TextArea taOrderMap;
    private Label laOrderMapTitle;
    private Label laOrderMapSubtitle;
    private Button btnProgressing;

    // UI Components for Order Detail View
    private TextArea taOrderDetail;
    private Label laDetailRootTitle;
    private Label laDetailSubtitle;
    private Button btnCollected;

    // Reference to UI Styles
    private static final PickerUIStyles.Colors COLORS = new PickerUIStyles.Colors();
    private static final PickerUIStyles.Typography TYPO = new PickerUIStyles.Typography();
    private static final PickerUIStyles.Spacing SPACE = new PickerUIStyles.Spacing();
    private static final PickerUIStyles.Components COMPS = new PickerUIStyles.Components();

    public void start(Stage window) {
        vbOrderMapRoot = createModernOrderMapRoot();
        vbOrderDetailRoot = createModernOrderDetailRoot();

        scene = new Scene(vbOrderMapRoot, WIDTH, HEIGHT);
        window.setScene(scene);
        window.setTitle("📦 HappyShop Order Picker");
        WinPosManager.registerWindow(window, WIDTH, HEIGHT);
        window.show();

        // Set the window close request to prevent closing if the order is not collected
        window.setOnCloseRequest(event -> {
            if (!taOrderDetail.getText().isEmpty()) {
                event.consume(); // Prevent window from closing
                laDetailRootTitle.setText("⚠️ Please complete the order before closing");
                laDetailRootTitle.setStyle(COMPS.getHeading2() + "-fx-text-fill: " + COLORS.ERROR + ";");
            }
        });
    }


     //Creates the modern Order Map view showing available orders

    private VBox createModernOrderMapRoot() {
        // Main container with modern background
        VBox container = new VBox();
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle(COMPS.getOrderMapPageBackground());
        container.setSpacing(SPACE.LG);

        // Header section
        VBox header = createOrderMapHeader();

        // Order list card
        VBox orderCard = createOrderMapCard();

        // Action button section
        HBox actionSection = createOrderMapActions();

        container.getChildren().addAll(header, orderCard, actionSection);
        return container;
    }

    /**
     * Creates the header for Order Map view
     */
    private VBox createOrderMapHeader() {
        VBox header = new VBox(SPACE.SM);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(SPACE.LG, 0, 0, 0));

        // Main title with icon
        laOrderMapTitle = new Label("📋 Orders Waiting for Processing");
        laOrderMapTitle.setStyle(COMPS.getHeading1());

        // Subtitle
        laOrderMapSubtitle = new Label("Select an order to begin preparation");
        laOrderMapSubtitle.setStyle(COMPS.getSubtitleLabel());

        header.getChildren().addAll(laOrderMapTitle, laOrderMapSubtitle);
        return header;
    }


     //Creates the card containing the order list

    private VBox createOrderMapCard() {
        VBox card = new VBox(SPACE.MD);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(COMPS.getCard());
        card.setMaxWidth(1200);
        card.setPrefHeight(HEIGHT - 300);

        // Card title
        Label cardTitle = new Label("Available Orders");
        cardTitle.setStyle(COMPS.getHeading3());

        // Order list text area
        taOrderMap = new TextArea();
        taOrderMap.setEditable(false);
        taOrderMap.setWrapText(true);
        taOrderMap.setStyle(COMPS.getOrderTextArea());
        VBox.setVgrow(taOrderMap, Priority.ALWAYS);

        // Info label
        Label infoLabel = new Label("💡 Orders are shown with their current status (Order ID → Status)");
        infoLabel.setStyle(
                "-fx-font-family: " + TYPO.FONT_PRIMARY + "; " +
                        "-fx-font-size: " + TYPO.BODY_SMALL + "px; " +
                        "-fx-text-fill: " + COLORS.TEXT_SECONDARY + "; " +
                        "-fx-padding: " + SPACE.SM + "px 0 0 0;"
        );

        card.getChildren().addAll(cardTitle, taOrderMap, infoLabel);
        return card;
    }


     //Creates the action button section for Order Map view

    private HBox createOrderMapActions() {
        HBox actionBox = new HBox(SPACE.MD);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(SPACE.MD, 0, SPACE.LG, 0));

        btnProgressing = new Button("🚀 Start Processing Order");
        btnProgressing.setStyle(COMPS.getPrimaryButton());
        btnProgressing.setPrefWidth(300);
        btnProgressing.setPrefHeight(50);
        btnProgressing.setOnAction(this::buttonClicked);

        // Add hover effect
        btnProgressing.setOnMouseEntered(e -> btnProgressing.setStyle(COMPS.getPrimaryButtonHover()));
        btnProgressing.setOnMouseExited(e -> btnProgressing.setStyle(COMPS.getPrimaryButton()));

        actionBox.getChildren().add(btnProgressing);
        return actionBox;
    }

    /**
     * Creates the modern Order Detail view for preparing the order
     */
    private VBox createModernOrderDetailRoot() {
        // Main container with modern background
        VBox container = new VBox();
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle(COMPS.getOrderDetailPageBackground());
        container.setSpacing(SPACE.LG);

        // Header section
        VBox header = createOrderDetailHeader();

        // Order detail card
        VBox detailCard = createOrderDetailCard();

        // Action button section
        HBox actionSection = createOrderDetailActions();

        container.getChildren().addAll(header, detailCard, actionSection);
        return container;
    }


     //Creates the header for Order Detail view

    private VBox createOrderDetailHeader() {
        VBox header = new VBox(SPACE.SM);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(SPACE.LG, 0, 0, 0));

        // Main title with icon
        laDetailRootTitle = new Label("⚙️ Processing Order Details");
        laDetailRootTitle.setStyle(COMPS.getHeading1());

        // Subtitle
        laDetailSubtitle = new Label("Prepare this order for customer collection");
        laDetailSubtitle.setStyle(COMPS.getSubtitleLabel());

        // Status badge
        HBox statusBox = new HBox(SPACE.SM);
        statusBox.setAlignment(Pos.CENTER);

        Label statusBadge = new Label("IN PROGRESS");
        statusBadge.setStyle(COMPS.getStatusBadge(COLORS.STATUS_PROGRESSING));

        statusBox.getChildren().add(statusBadge);

        header.getChildren().addAll(laDetailRootTitle, laDetailSubtitle, statusBox);
        return header;
    }


     //Creates the card containing the order details

    private VBox createOrderDetailCard() {
        VBox card = new VBox(SPACE.MD);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(COMPS.getCard());
        card.setMaxWidth(1200);
        card.setPrefHeight(HEIGHT - 300);

        // Card title
        Label cardTitle = new Label("Order Information");
        cardTitle.setStyle(COMPS.getHeading3());

        // Order detail text area
        taOrderDetail = new TextArea();
        taOrderDetail.setEditable(false);
        taOrderDetail.setWrapText(true);
        taOrderDetail.setText("Order details will appear here...");
        taOrderDetail.setStyle(COMPS.getOrderTextArea());
        VBox.setVgrow(taOrderDetail, Priority.ALWAYS);

        // Warning label
        Label warningLabel = new Label("⚠️ Do not close this window until the customer has collected the order");
        warningLabel.setStyle(
                "-fx-font-family: " + TYPO.FONT_PRIMARY + "; " +
                        "-fx-font-size: " + TYPO.BODY_SMALL + "px; " +
                        "-fx-font-weight: " + TYPO.SEMIBOLD + "; " +
                        "-fx-text-fill: " + COLORS.WARNING + "; " +
                        "-fx-padding: " + SPACE.SM + "px 0 0 0;"
        );

        card.getChildren().addAll(cardTitle, taOrderDetail, warningLabel);
        return card;
    }


     //Creates the action button section for Order Detail view

    private HBox createOrderDetailActions() {
        HBox actionBox = new HBox(SPACE.MD);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(SPACE.MD, 0, SPACE.LG, 0));

        btnCollected = new Button("✅ Customer Collected Order");
        btnCollected.setStyle(COMPS.getSuccessButton());
        btnCollected.setPrefWidth(300);
        btnCollected.setPrefHeight(50);
        btnCollected.setOnAction(this::buttonClicked);

        // Add hover effect
        btnCollected.setOnMouseEntered(e -> btnCollected.setStyle(COMPS.getSuccessButtonHover()));
        btnCollected.setOnMouseExited(e -> btnCollected.setStyle(COMPS.getSuccessButton()));

        actionBox.getChildren().add(btnCollected);
        return actionBox;
    }


     //Handles button clicks for order processing actions

    private void buttonClicked(ActionEvent event) {
        Button button = (Button) event.getSource();
        String btnText = button.getText();

        try {
            // Based on the button's text, performs the appropriate action and switches the displayed root.
            if (btnText.contains("Start Processing") || btnText.contains("Progressing")) {
                // Switch to OrderDetailRoot
                scene.setRoot(vbOrderDetailRoot);
                pickerController.doProgressing();
            }
            else if (btnText.contains("Customer Collected")) {
                // Complete the order and switch back to orderMapRoot
                pickerController.doCollected();
                scene.setRoot(vbOrderMapRoot);
            }
        } catch (IOException e) {
            showErrorAlert("Order Processing Error",
                    "Failed to handle order action: " + btnText,
                    e.getMessage());
        }
    }


     //Updates the view with new order information

    void update(String strOrderMap, String strOrderDetail) {
        taOrderMap.setText(strOrderMap);
        taOrderDetail.setText(strOrderDetail);
        laDetailRootTitle.setText("⚙️ Processing Order Details");
        laDetailRootTitle.setStyle(COMPS.getHeading1());
    }


     //Shows an error alert dialog with modern styling

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: " + COLORS.SURFACE + "; " +
                        "-fx-font-family: " + TYPO.FONT_PRIMARY + ";"
        );

        alert.showAndWait();
    }
}