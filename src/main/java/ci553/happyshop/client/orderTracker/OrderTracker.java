package ci553.happyshop.client.orderTracker;

import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.orderManagement.OrderState;
import ci553.happyshop.utility.WinPosManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.util.Map;
import java.util.TreeMap;

/**
 * Design Features:
 * - Card-based layout matching PickerView style
 * - Color-coded status badges for visual clarity
 * - Real-time updates from OrderHub
 * - Professional dashboard appearance
 */
public class OrderTracker {
    private final int WIDTH = 500;
    private final int HEIGHT = 700;

    // TreeMap (orderID, state) holding order IDs and their corresponding states
    private static final TreeMap<Integer, OrderState> ordersMap = new TreeMap<>();
    private VBox orderListContainer; // Container for order cards
    private Label laOrderCount; // Shows total number of orders

    // Reference to PickerUIStyles (shared design system)
    private static final String PRIMARY = "#F59E0B";
    private static final String SUCCESS = "#10B981";
    private static final String INFO = "#3B82F6";
    private static final String BACKGROUND = "#F9FAFB";
    private static final String SURFACE = "#FFFFFF";
    private static final String BORDER = "#E5E7EB";
    private static final String TEXT_PRIMARY = "#111827";
    private static final String TEXT_SECONDARY = "#6B7280";
    private static final String FONT_PRIMARY = "'Segoe UI', -apple-system, BlinkMacSystemFont, 'Roboto', sans-serif";

     //Constructor initializes the modern UI with header, order list, and status indicators

    public OrderTracker() {
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

        // Order list container (scrollable)
        ScrollPane scrollPane = createOrderListSection();

        mainContainer.getChildren().addAll(header, scrollPane);

        // Create scene and stage
        Scene scene = new Scene(mainContainer, WIDTH, HEIGHT);
        Stage window = new Stage();
        window.setScene(scene);
        window.setTitle("📊 Order Tracker");

        // Register the window's position with WinPosManager
        WinPosManager.registerWindow(window, WIDTH, HEIGHT);
        window.show();
    }


     //Creates the modern header section

    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);

        // Main title
        Label laTitle = new Label("📊 Order Tracker");
        laTitle.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 28px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );

        // Subtitle
        Label laSubtitle = new Label("Real-time order monitoring");
        laSubtitle.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: 500; " +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";"
        );

        // Order count badge
        laOrderCount = new Label("0 Active Orders");
        laOrderCount.setStyle(
                "-fx-background-color: " + INFO + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-padding: 6px 16px;"
        );

        // Status legend
        HBox legend = createStatusLegend();

        header.getChildren().addAll(laTitle, laSubtitle, laOrderCount, legend);
        return header;
    }

     //Creates a status legend showing what each color means

    private HBox createStatusLegend() {
        HBox legend = new HBox(15);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 0, 0));

        legend.getChildren().addAll(
                createLegendItem("Ordered", INFO),
                createLegendItem("Progressing", PRIMARY),
                createLegendItem("Collected", SUCCESS)
        );

        return legend;
    }

     //Creates a single legend item

    private HBox createLegendItem(String label, String color) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER);

        // Color indicator
        Region colorBox = new Region();
        colorBox.setPrefSize(12, 12);
        colorBox.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-background-radius: 3px;"
        );

        // Label
        Label textLabel = new Label(label);
        textLabel.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 11px; " +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";"
        );

        item.getChildren().addAll(colorBox, textLabel);
        return item;
    }

     //Creates the scrollable order list section

    private ScrollPane createOrderListSection() {
        orderListContainer = new VBox(12);
        orderListContainer.setAlignment(Pos.TOP_CENTER);
        orderListContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(orderListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background: transparent; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent;"
        );
        scrollPane.setPrefHeight(HEIGHT - 250);

        return scrollPane;
    }

     //Creates a modern order card

    private HBox createOrderCard(int orderId, OrderState state) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle(
                "-fx-background-color: " + SURFACE + "; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-border-color: " + BORDER + "; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 12px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0, 0, 2);"
        );

        // Order ID section
        VBox orderInfo = new VBox(4);
        orderInfo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(orderInfo, Priority.ALWAYS);

        Label orderIdLabel = new Label("Order #" + orderId);
        orderIdLabel.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );

        Label orderSubLabel = new Label("Order ID: " + orderId);
        orderSubLabel.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 12px; " +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";"
        );

        orderInfo.getChildren().addAll(orderIdLabel, orderSubLabel);

        // Status badge
        Label statusBadge = createStatusBadge(state);

        card.getChildren().addAll(orderInfo, statusBadge);
        return card;
    }

     //Creates a color-coded status badge

    private Label createStatusBadge(OrderState state) {
        String color = getColorForState(state);
        String icon = getIconForState(state);

        Label badge = new Label(icon + " " + state.toString());
        badge.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-background-radius: 16px; " +
                        "-fx-padding: 6px 14px; " +
                        "-fx-min-width: 120px; " +
                        "-fx-alignment: center;"
        );

        return badge;
    }

     //Gets the color for a specific order state

    private String getColorForState(OrderState state) {
        switch (state) {
            case Ordered:
                return INFO;        // Blue
            case Progressing:
                return PRIMARY;     // Amber
            case Collected:
                return SUCCESS;     // Green
            default:
                return TEXT_SECONDARY;
        }
    }


     //Gets the icon for a specific order state

    private String getIconForState(OrderState state) {
        switch (state) {
            case Ordered:
                return "🆕";
            case Progressing:
                return "⚙️";
            case Collected:
                return "✅";
            default:
                return "📦";
        }
    }

    /**
     * Registers this OrderTracker instance with the OrderHub.
     * This allows the OrderTracker to receive updates on order state changes.
     */
    public void registerWithOrderHub() {
        OrderHub orderHub = OrderHub.getOrderHub();
        orderHub.registerOrderTracker(this);
    }

    /**
     * Sets the order map with new data and refreshes the display.
     * This method is called by OrderHub when order states are updated.
     */
    public void setOrderMap(TreeMap<Integer, OrderState> om) {
        ordersMap.clear(); // Clears the current map to replace it with the new data
        ordersMap.putAll(om); // Adds all new order data to the map
        displayOrderMap(); // Updates the display with the new order map
    }

    /**
     * Displays the current order map with modern card-based UI
     * Iterates over the ordersMap and creates a card for each order
     */
    private void displayOrderMap() {
        // Clear existing cards
        orderListContainer.getChildren().clear();

        // Update order count
        int orderCount = ordersMap.size();
        laOrderCount.setText(orderCount + " Active Order" + (orderCount != 1 ? "s" : ""));

        if (ordersMap.isEmpty()) {
            // Show empty state
            VBox emptyState = createEmptyState();
            orderListContainer.getChildren().add(emptyState);
        } else {
            // Create a card for each order
            for (Map.Entry<Integer, OrderState> entry : ordersMap.entrySet()) {
                int orderId = entry.getKey();
                OrderState orderState = entry.getValue();
                HBox orderCard = createOrderCard(orderId, orderState);
                orderListContainer.getChildren().add(orderCard);
            }
        }
    }

    //Creates an empty state view when no orders exist

    private VBox createEmptyState() {
        VBox emptyState = new VBox(10);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(40));
        emptyState.setStyle(
                "-fx-background-color: " + SURFACE + "; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-border-color: " + BORDER + "; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 12px;"
        );

        Label icon = new Label("📭");
        icon.setStyle("-fx-font-size: 48px;");

        Label title = new Label("No Active Orders");
        title.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );

        Label subtitle = new Label("Orders will appear here when customers place them");
        subtitle.setStyle(
                "-fx-font-family: " + FONT_PRIMARY + "; " +
                        "-fx-font-size: 13px; " +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";"
        );

        emptyState.getChildren().addAll(icon, title, subtitle);
        return emptyState;
    }
}