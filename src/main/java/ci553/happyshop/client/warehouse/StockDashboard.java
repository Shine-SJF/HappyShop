package ci553.happyshop.client.warehouse;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.catalogue.StockLevelIndicator;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.storageAccess.DatabaseRWFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Stock Dashboard - Visual overview of inventory status
 * Shows all products with their stock levels and status indicators
 * Key Features:
 * - Real-time inventory statistics
 * - Visual stock level indicators
 * - Search and filter functionality
 * - Responsive UI with product cards
 */

public class StockDashboard {
    private Stage dashboardWindow;
    private WarehouseView warehouseView;
    private DatabaseRW databaseRW;

    private VBox contentArea;
    private Label totalProductsLabel;
    private Label lowStockCountLabel;
    private Label outOfStockCountLabel;

    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    public StockDashboard(WarehouseView warehouseView) {
        this.warehouseView = warehouseView;
        this.databaseRW = DatabaseRWFactory.createDatabaseRW();
        createDashboard();
    }

    private void createDashboard() {
        dashboardWindow = new Stage();
        dashboardWindow.initModality(Modality.NONE);
        dashboardWindow.setTitle("📊 Stock Dashboard");

        // Main container
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header with statistics
        HBox header = createHeader();

        // Filter controls
        HBox filterBar = createFilterBar();

        // Content area (scrollable)
        ScrollPane scrollPane = createContentArea();
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Refresh button at bottom
        Button btnRefresh = new Button("🔄 Refresh Dashboard");
        btnRefresh.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px;");
        btnRefresh.setOnAction(e -> refresh());

        HBox buttonBar = new HBox(btnRefresh);
        buttonBar.setAlignment(Pos.CENTER);

        root.getChildren().addAll(header, filterBar, scrollPane, buttonBar);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        dashboardWindow.setScene(scene);

        // Load initial data
        refresh();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Total products stat
        VBox totalBox = createStatBox("📦 Total Products", "0", "#4A90E2");
        totalProductsLabel = (Label) ((VBox) totalBox.getChildren().get(1)).getChildren().get(0);

        // Low stock stat
        VBox lowStockBox = createStatBox("⚠️ Low Stock", "0", "#FF9500");
        lowStockCountLabel = (Label) ((VBox) lowStockBox.getChildren().get(1)).getChildren().get(0);

        // Out of stock stat
        VBox outOfStockBox = createStatBox("❌ Out of Stock", "0", "#FF3B30");
        outOfStockCountLabel = (Label) ((VBox) outOfStockBox.getChildren().get(1)).getChildren().get(0);

        header.getChildren().addAll(totalBox, lowStockBox, outOfStockBox);
        return header;
    }

    private VBox createStatBox(String title, String value, String color) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: " + color + "15; -fx-background-radius: 8px;");
        box.setPrefWidth(220);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #666;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        VBox valueBox = new VBox(valueLabel);
        valueBox.setAlignment(Pos.CENTER);

        box.getChildren().addAll(titleLabel, valueBox);
        return box;
    }

    private HBox createFilterBar() {
        HBox filterBar = new HBox(15);
        filterBar.setPadding(new Insets(10));
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setStyle("-fx-background-color: white; -fx-background-radius: 8px;");

        Label filterLabel = new Label("Filter:");
        filterLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All Products", "In Stock", "Low Stock", "Out of Stock");
        filterCombo.setValue("All Products");
        filterCombo.setStyle("-fx-font-size: 13px;");
        filterCombo.setOnAction(e -> filterProducts(filterCombo.getValue()));

        TextField searchField = new TextField();
        searchField.setPromptText("Search by ID or name...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-font-size: 13px;");
        searchField.textProperty().addListener((obs, old, newVal) -> searchProducts(newVal));

        filterBar.getChildren().addAll(filterLabel, filterCombo, new Region(), searchField);
        HBox.setHgrow(filterBar.getChildren().get(2), Priority.ALWAYS);

        return filterBar;
    }

    private ScrollPane createContentArea() {
        contentArea = new VBox(10);
        contentArea.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        return scrollPane;
    }

    private void loadProducts() {
        contentArea.getChildren().clear();

        try {
            // Search for all products (empty keyword returns all in some implementations,
            // or you could add a getAllProducts() method to DatabaseRW)
            ArrayList<Product> allProducts = databaseRW.searchProduct("");

            if (allProducts.isEmpty()) {
                // If empty search doesn't work, try common IDs
                allProducts = new ArrayList<>();
                for (int i = 1; i <= 20; i++) {
                    String id = String.format("%04d", i);
                    Product p = databaseRW.searchByProductId(id);
                    if (p != null) {
                        allProducts.add(p);
                    }
                }
            }

            updateStatistics(allProducts);

            for (Product product : allProducts) {
                HBox productCard = createProductCard(product);
                contentArea.getChildren().add(productCard);
            }

            if (allProducts.isEmpty()) {
                Label noProducts = new Label("No products found in database");
                noProducts.setStyle("-fx-font-size: 16px; -fx-text-fill: #999; -fx-padding: 50px;");
                contentArea.getChildren().add(noProducts);
                contentArea.setAlignment(Pos.CENTER);
            } else {
                contentArea.setAlignment(Pos.TOP_CENTER);
            }

        } catch (SQLException e) {
            showError("Database Error", "Failed to load products: " + e.getMessage());
        }
    }

    private HBox createProductCard(Product product) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        // Product ID
        Label idLabel = new Label(product.getProductId());
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-min-width: 80px;");

        // Product description
        VBox infoBox = new VBox(5);
        Label nameLabel = new Label(product.getProductDescription());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(300);

        Label priceLabel = new Label(String.format("£%.2f", product.getUnitPrice()));
        priceLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        infoBox.getChildren().addAll(nameLabel, priceLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Stock quantity
        VBox stockBox = new VBox(3);
        stockBox.setAlignment(Pos.CENTER);
        stockBox.setMinWidth(100);

        Label stockLabel = new Label(String.valueOf(product.getStockQuantity()));
        stockLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label stockText = new Label("units");
        stockText.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");

        stockBox.getChildren().addAll(stockLabel, stockText);

        // Stock level indicator using the existing StockLevelIndicator
        int currentStock = product.getStockQuantity();
        int maxStock = currentStock > 0 ? currentStock * 2 : 100; // Approximate max

        StockLevelIndicator.StockLevel level = StockLevelIndicator.determineStockLevel(currentStock, maxStock);

        Label statusBadge = new Label(level.getDisplayText());
        statusBadge.setStyle(StockLevelIndicator.getStyleForLevel(level));
        statusBadge.setMinWidth(120);
        statusBadge.setAlignment(Pos.CENTER);

        // Stock bar
        ProgressBar stockBar = new ProgressBar(currentStock > 0 ? Math.min(currentStock / 100.0, 1.0) : 0);
        stockBar.setPrefWidth(150);
        stockBar.setStyle(getProgressBarStyle(level));

        VBox statusBox = new VBox(5, statusBadge, stockBar);
        statusBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(idLabel, infoBox, stockBox, statusBox);

        return card;
    }

    private String getProgressBarStyle(StockLevelIndicator.StockLevel level) {
        String color;
        switch (level) {
            case CRITICAL:
                color = "#FF3B30";
                break;
            case LOW:
                color = "#FF9500";
                break;
            case MEDIUM:
                color = "#FFCC00";
                break;
            default:
                color = "#34C759";
        }

        return String.format(
                ".progress-bar { -fx-accent: %s; } " +
                        ".progress-bar .bar { -fx-background-color: %s; -fx-background-radius: 4px; }",
                color, color
        );
    }

    private void updateStatistics(ArrayList<Product> products) {
        int total = products.size();
        int lowStock = 0;
        int outOfStock = 0;

        for (Product p : products) {
            int stock = p.getStockQuantity();
            if (stock == 0) {
                outOfStock++;
            } else if (stock < 10) {
                lowStock++;
            }
        }

        totalProductsLabel.setText(String.valueOf(total));
        lowStockCountLabel.setText(String.valueOf(lowStock));
        outOfStockCountLabel.setText(String.valueOf(outOfStock));
    }

    private void filterProducts(String filter) {
        // Reload with filter applied
        refresh();
        // TODO: Implement actual filtering logic
    }

    private void searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            refresh();
            return;
        }

        contentArea.getChildren().clear();

        try {
            ArrayList<Product> results = databaseRW.searchProduct(query.trim());
            updateStatistics(results);

            for (Product product : results) {
                HBox productCard = createProductCard(product);
                contentArea.getChildren().add(productCard);
            }

            if (results.isEmpty()) {
                Label noResults = new Label("No products match your search");
                noResults.setStyle("-fx-font-size: 16px; -fx-text-fill: #999; -fx-padding: 50px;");
                contentArea.getChildren().add(noResults);
                contentArea.setAlignment(Pos.CENTER);
            }

        } catch (SQLException e) {
            showError("Search Error", "Failed to search products: " + e.getMessage());
        }
    }

    public void show() {
        if (dashboardWindow != null) {
            refresh();
            dashboardWindow.show();
            dashboardWindow.toFront();
        }
    }

    public void refresh() {
        loadProducts();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}