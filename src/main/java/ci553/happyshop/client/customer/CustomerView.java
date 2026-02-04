package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.utility.WinPosManager;
import ci553.happyshop.utility.WindowBounds;
import ci553.happyshop.utility.StorageLocation;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;


//Modern HappyShop Customer View using CustomerUIStyles

public class CustomerView {
    public CustomerController cusController;

    // Screen dimensions
    private final int WIDTH = 1400;
    private final int HEIGHT = 850;
    private final int COLUMN_WIDTH = (WIDTH / 2) - 30;

    private HBox hbRoot;
    private VBox vbTrolleyPage;
    private VBox vbReceiptPage;

    // UI Components
    private TextField tfId;
    private TextField tfName;
    private ImageView ivProduct;
    private Label lbProductInfo;
    private TextArea taReceipt;
    private ListView<Product> lvTrolley;
    private Label laTotalPrice;
    private Label laItemCount;
    private Stage viewWindow;

    // ========== Reference to UI Styles ==========
    // Static import for easy access (optional but convenient)
    private static final CustomerUIStyles.Colors COLORS = new CustomerUIStyles.Colors();
    private static final CustomerUIStyles.Typography TYPO = new CustomerUIStyles.Typography();
    private static final CustomerUIStyles.Spacing SPACE = new CustomerUIStyles.Spacing();
    private static final CustomerUIStyles.Borders BORDERS = new CustomerUIStyles.Borders();
    private static final CustomerUIStyles.Components COMPS = new CustomerUIStyles.Components();
    private static final CustomerUIStyles.Shadows SHADOWS = new CustomerUIStyles.Shadows();

    public void start(Stage window) {
        // Create modern pages
        VBox vbSearchPage = createModernSearchPage();
        vbTrolleyPage = createModernTrolleyPage();
        vbReceiptPage = createModernReceiptPage();

        // Modern subtle divider using styles
        Line divider = new Line(0, 0, 0, HEIGHT);
        divider.setStroke(javafx.scene.paint.Color.web(COLORS.DIVIDER));
        divider.setStrokeWidth(1);

        VBox dividerContainer = new VBox(divider);
        dividerContainer.setPrefWidth(1);
        dividerContainer.setAlignment(Pos.CENTER);

        // Main layout with modern background
        hbRoot = new HBox(0, vbSearchPage, dividerContainer, vbTrolleyPage);
        hbRoot.setAlignment(Pos.CENTER);

        // Root container with gradient background using styles
        StackPane rootContainer = new StackPane();
        rootContainer.setStyle("-fx-background-color: " + COLORS.BACKGROUND + ";");
        rootContainer.getChildren().add(hbRoot);

        // Create scene
        Scene scene = new Scene(rootContainer, WIDTH, HEIGHT);
        scene.getStylesheets().add("data:text/css," + COMPS.getScrollBar());

        window.setScene(scene);
        window.setTitle("🛍️ HappyShop | Modern Online Shopping");

        try {
            window.getIcons().add(new Image("cart_icon.png"));
        } catch (Exception e) {
            // Icon not found, continue without it
        }

        // Center window
        WinPosManager.registerWindow(window, WIDTH, HEIGHT);
        window.show();
        viewWindow = window;
    }

    // ==================== MODERN UI HELPER METHODS ====================

    private void setupTextFieldHover(TextField textField) {
        String originalStyle = textField.getStyle();
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textField.setStyle(COMPS.getTextFieldFocused());
            } else {
                textField.setStyle(originalStyle);
            }
        });
    }

    private void setupButtonHover(Button button, String hoverColor) {
        String originalStyle = button.getStyle();
        button.setOnMouseEntered(e -> {
            button.setStyle(originalStyle.replace(
                    "-fx-background-color: " + extractColorFromStyle(originalStyle),
                    "-fx-background-color: " + hoverColor + "; " +
                            "-fx-effect: " + SHADOWS.LG
            ));
        });
        button.setOnMouseExited(e -> {
            button.setStyle(originalStyle);
        });
    }

    private String extractColorFromStyle(String style) {
        // Simple extraction of background color from style
        if (style.contains("-fx-background-color: ")) {
            int start = style.indexOf("-fx-background-color: ") + 22;
            int end = style.indexOf(";", start);
            return style.substring(start, end).trim();
        }
        return COLORS.PRIMARY;
    }

    // ==================== SEARCH PAGE ====================
    private VBox createModernSearchPage() {
        VBox container = new VBox(SPACE.XL);
        container.setPrefWidth(COLUMN_WIDTH);
        container.setStyle("-fx-padding: " + SPACE.XL + "px;");

        // Header with logo
        HBox header = createHeader();

        // Search section
        VBox searchSection = createSearchSection();

        // Product display section
        VBox productSection = createProductSection();

        container.getChildren().addAll(header, searchSection, productSection);
        return container;
    }

    private HBox createHeader() {
        HBox header = new HBox(SPACE.MD);
        header.setAlignment(Pos.CENTER_LEFT);

        // Logo/Title
        VBox logoSection = new VBox(2);
        Label logo = new Label("🛍️ HappyShop");
        logo.setStyle(COMPS.getHeading1());

        Label tagline = new Label("Premium Online Shopping Experience");
        tagline.setStyle(COMPS.getCaptionText());

        logoSection.getChildren().addAll(logo, tagline);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // User stats (optional)
        VBox stats = new VBox(2);
        stats.setAlignment(Pos.CENTER_RIGHT);

        Label welcome = new Label("Welcome, Customer!");
        welcome.setStyle(COMPS.getBodyText());

        stats.getChildren().addAll(welcome);

        header.getChildren().addAll(logoSection, spacer, stats);
        return header;
    }

    private VBox createSearchSection() {
        VBox searchCard = new VBox(SPACE.LG);
        searchCard.setStyle(COMPS.getCard());

        // Title
        Label title = new Label("🔍 Find Products");
        title.setStyle(COMPS.getHeading2());

        // Search form
        VBox searchForm = new VBox(SPACE.MD);

        // Product ID field
        VBox idField = new VBox(SPACE.XS);
        Label idLabel = new Label("Product ID");
        idLabel.setStyle(COMPS.getCaptionText());

        tfId = new TextField();
        tfId.setPromptText("Enter product ID (e.g., P001)");
        tfId.setStyle(COMPS.getTextField());
        setupTextFieldHover(tfId);

        idField.getChildren().addAll(idLabel, tfId);

        // Product Name field
        VBox nameField = new VBox(SPACE.XS);
        Label nameLabel = new Label("Product Name (Optional)");
        nameLabel.setStyle(COMPS.getCaptionText());

        tfName = new TextField();
        tfName.setPromptText("Search by product name");
        tfName.setStyle(COMPS.getTextField());
        setupTextFieldHover(tfName);

        nameField.getChildren().addAll(nameLabel, tfName);

        // Action buttons
        HBox buttonRow = new HBox(SPACE.MD);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        Button btnSearch = new Button("🔎 Search Product");
        btnSearch.setStyle(COMPS.getPrimaryButton());
        btnSearch.setOnAction(this::buttonClicked);
        setupButtonHover(btnSearch, COLORS.PRIMARY_DARK);

        Button btnAddToCart = new Button("🛒 Add to Cart");
        btnAddToCart.setStyle(COMPS.getSuccessButton());
        btnAddToCart.setOnAction(this::buttonClicked);
        setupButtonHover(btnAddToCart, "#2AA44F"); // Darker green for hover

        buttonRow.getChildren().addAll(btnSearch, btnAddToCart);

        searchForm.getChildren().addAll(idField, nameField, buttonRow);
        searchCard.getChildren().addAll(title, searchForm);

        return searchCard;
    }

    private VBox createProductSection() {
        VBox productCard = new VBox(SPACE.LG);
        productCard.setStyle(COMPS.getCard());

        // Title
        Label title = new Label("📦 Product Details");
        title.setStyle(COMPS.getHeading2());

        // Product display area
        HBox productDisplay = new HBox(SPACE.LG);
        productDisplay.setAlignment(Pos.CENTER_LEFT);

        // Image container with modern frame
        StackPane imageContainer = new StackPane();
        imageContainer.setStyle(
                "-fx-background-color: " + COLORS.PRIMARY_LIGHT + "; " +
                        "-fx-background-radius: " + BORDERS.MD + "px; " +
                        "-fx-padding: " + SPACE.MD + "px;"
        );

        ivProduct = new ImageView("imageHolder.jpg");
        ivProduct.setFitHeight(140);
        ivProduct.setFitWidth(140);
        ivProduct.setPreserveRatio(true);
        imageContainer.getChildren().add(ivProduct);

        // Product info
        VBox productInfo = new VBox(SPACE.MD);
        productInfo.setMaxWidth(300);

        lbProductInfo = new Label("👋 Welcome to HappyShop!\n\nSearch for products using the form above to see detailed information, pricing, and availability.");
        lbProductInfo.setStyle(COMPS.getBodyText());
        lbProductInfo.setWrapText(true);
        lbProductInfo.setLineSpacing(4);

        // Stock status indicator (dynamic)
        HBox stockIndicator = new HBox(SPACE.SM);
        stockIndicator.setAlignment(Pos.CENTER_LEFT);

        Label stockDot = new Label("●");
        stockDot.setStyle("-fx-text-fill: " + COLORS.TEXT_TERTIARY + "; -fx-font-size: 12px;");

        Label stockText = new Label("Search a product to see stock status");
        stockText.setStyle(COMPS.getCaptionText());

        stockIndicator.getChildren().addAll(stockDot, stockText);

        productInfo.getChildren().addAll(lbProductInfo, stockIndicator);
        productDisplay.getChildren().addAll(imageContainer, productInfo);
        productCard.getChildren().addAll(title, productDisplay);

        return productCard;
    }

    // ==================== TROLLEY PAGE ====================
    private VBox createModernTrolleyPage() {
        VBox container = new VBox(SPACE.XL);
        container.setPrefWidth(COLUMN_WIDTH);
        container.setStyle("-fx-padding: " + SPACE.XL + "px;");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🛒 Your Shopping Cart");
        title.setStyle(COMPS.getHeading1());
        header.getChildren().add(title);

        // Cart content
        VBox cartContent = createCartContent();

        container.getChildren().addAll(header, cartContent);
        return container;
    }

    private VBox createCartContent() {
        VBox cartCard = new VBox(0);
        cartCard.setStyle(COMPS.getCard());

        // Column headers
        HBox headers = new HBox(SPACE.XL);
        headers.setStyle(
                "-fx-padding: " + SPACE.LG + "px; " +
                        "-fx-border-color: " + COLORS.DIVIDER + "; " +
                        "-fx-border-width: 0 0 1 0;"
        );

        Label productHeader = new Label("PRODUCT");
        productHeader.setStyle(COMPS.getBodyText() + " -fx-font-weight: " + TYPO.SEMIBOLD + ";");
        productHeader.setPrefWidth(250);

        Label priceHeader = new Label("PRICE");
        priceHeader.setStyle(COMPS.getBodyText() + " -fx-font-weight: " + TYPO.SEMIBOLD + ";");
        priceHeader.setPrefWidth(100);
        priceHeader.setAlignment(Pos.CENTER);

        Label qtyHeader = new Label("QUANTITY");
        qtyHeader.setStyle(COMPS.getBodyText() + " -fx-font-weight: " + TYPO.SEMIBOLD + ";");
        qtyHeader.setPrefWidth(150);
        qtyHeader.setAlignment(Pos.CENTER);

        headers.getChildren().addAll(productHeader, priceHeader, qtyHeader);

        // Create ListView for cart items
        if (lvTrolley == null) {
            lvTrolley = new ListView<>();
            lvTrolley.setPrefHeight(400);
            lvTrolley.setStyle(COMPS.getListView());

            // Modern cell factory
            lvTrolley.setCellFactory(param -> new ListCell<Product>() {
                @Override
                protected void updateItem(Product product, boolean empty) {
                    super.updateItem(product, empty);

                    if (empty || product == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        setGraphic(createCartItemCard(product));
                        setText(null);
                    }
                }
            });
        }

        // Summary section
        VBox summary = createCartSummary();

        cartCard.getChildren().addAll(headers, lvTrolley, summary);
        return cartCard;
    }

    private HBox createCartItemCard(Product product) {
        HBox itemCard = new HBox(SPACE.LG);
        itemCard.setStyle(
                "-fx-padding: " + SPACE.LG + "px; " +
                        "-fx-border-color: " + COLORS.DIVIDER + "; " +
                        "-fx-border-width: 0 0 1 0;"
        );
        itemCard.setAlignment(Pos.CENTER_LEFT);

        // Product image
        String imageName = product.getProductImageName();
        String relativeImageUrl = StorageLocation.imageFolder + imageName;
        Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
        String imageFullUri = imageFullPath.toUri().toString();

        ImageView productImage;
        try {
            productImage = new ImageView(new Image(imageFullUri, 80, 80, true, true));
        } catch (Exception e) {
            productImage = new ImageView(new Image("imageHolder.jpg", 80, 80, true, true));
        }
        productImage.setStyle("-fx-background-radius: " + BORDERS.MD + "px;");

        // Product info
        VBox productInfo = new VBox(SPACE.XS);
        productInfo.setMaxWidth(200);

        Label productName = new Label(product.getProductDescription());
        productName.setStyle(COMPS.getBodyText() + " -fx-font-weight: " + TYPO.SEMIBOLD + ";");
        productName.setWrapText(true);

        Label productId = new Label("SKU: " + product.getProductId());
        productId.setStyle(COMPS.getCaptionText());

        // Stock status badge using CustomerUIStyles helper
        String stockStatus = CustomerUIStyles.getStockStatusStyle(
                product.getStockQuantity(),
                product.getOrderedQuantity()
        );
        Label stockBadge = new Label(stockStatus.toUpperCase());
        stockBadge.setStyle(COMPS.getStatusBadge(stockStatus));

        productInfo.getChildren().addAll(productName, productId, stockBadge);

        HBox leftSection = new HBox(SPACE.MD, productImage, productInfo);
        leftSection.setPrefWidth(250);

        // Price display
        VBox priceSection = new VBox();
        Label price = new Label(String.format("£%.2f", product.getUnitPrice()));
        price.setStyle(COMPS.getPriceDisplay());
        priceSection.getChildren().add(price);
        priceSection.setAlignment(Pos.CENTER);
        priceSection.setPrefWidth(100);

        // Quantity controls
        HBox quantitySection = new HBox(SPACE.SM);
        quantitySection.setAlignment(Pos.CENTER);

        Button btnDecrease = new Button("−");
        btnDecrease.setStyle(COMPS.getIconButton(COLORS.ERROR));
        btnDecrease.setOnAction(e -> changeQuantity(product, -1));
        setupButtonHover(btnDecrease, "#D32F2F"); // Darker red

        Label quantity = new Label(String.valueOf(product.getOrderedQuantity()));
        quantity.setStyle(COMPS.getBodyText() + " -fx-font-weight: " + TYPO.SEMIBOLD + "; -fx-min-width: 30px; -fx-alignment: center;");

        Button btnIncrease = new Button("+");
        btnIncrease.setStyle(COMPS.getIconButton(COLORS.SUCCESS));
        btnIncrease.setOnAction(e -> changeQuantity(product, 1));
        setupButtonHover(btnIncrease, "#2AA44F"); // Darker green

        // Remove button
        Button btnRemove = new Button("×");
        btnRemove.setStyle(COMPS.getIconButton(COLORS.TEXT_TERTIARY));
        btnRemove.setOnAction(e -> changeQuantity(product, -product.getOrderedQuantity()));
        setupButtonHover(btnRemove, COLORS.ERROR);

        quantitySection.getChildren().addAll(btnDecrease, quantity, btnIncrease, btnRemove);
        quantitySection.setPrefWidth(150);

        itemCard.getChildren().addAll(leftSection, priceSection, quantitySection);
        return itemCard;
    }

    private VBox createCartSummary() {
        VBox summary = new VBox(SPACE.LG);
        summary.setStyle(
                "-fx-padding: " + SPACE.LG + "px; " +
                        "-fx-background-color: " + COLORS.PRIMARY_LIGHT + "; " +
                        "-fx-background-radius: 0 0 " + BORDERS.LG + "px " + BORDERS.LG + "px;"
        );

        // Item count
        HBox itemCountRow = new HBox();
        laItemCount = new Label("Items (0):");
        laItemCount.setStyle(COMPS.getBodyText());

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        itemCountRow.getChildren().addAll(laItemCount, spacer1);

        // Total price
        HBox totalRow = new HBox();
        Label totalLabel = new Label("Total:");
        totalLabel.setStyle(COMPS.getHeading2());

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        if (laTotalPrice == null) {
            laTotalPrice = new Label("£0.00");
        }
        laTotalPrice.setStyle(COMPS.getHeading1() + " -fx-text-fill: " + COLORS.PRIMARY + ";");

        totalRow.getChildren().addAll(totalLabel, spacer2, laTotalPrice);

        // Divider
        Pane divider = new Pane();
        divider.setStyle(COMPS.getDivider());

        // Action buttons
        HBox actionButtons = new HBox(SPACE.MD);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button btnClearCart = new Button("🗑️ Clear Cart");
        btnClearCart.setStyle(String.format(
                "-fx-font-family: %s; " +
                        "-fx-font-size: %f; " +
                        "-fx-font-weight: %s; " +
                        "-fx-background-color: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: %f; " +
                        "-fx-padding: %f %f; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: %s; " +
                        "-fx-border-color: transparent;",
                TYPO.FONT_PRIMARY, TYPO.BODY, TYPO.MEDIUM,
                COLORS.ERROR, BORDERS.MD,
                SPACE.SM, SPACE.LG, SHADOWS.SM
        ));
        btnClearCart.setOnAction(this::buttonClicked);
        setupButtonHover(btnClearCart, "#C62828"); // Darker red for hover

        Button btnContinueShopping = new Button("← Continue Shopping");
        btnContinueShopping.setStyle(COMPS.getSecondaryButton());
        btnContinueShopping.setOnAction(this::buttonClicked);

        Button btnCheckout = new Button("Secure Checkout →");
        btnCheckout.setStyle(COMPS.getSuccessButton());
        btnCheckout.setOnAction(this::buttonClicked);
        setupButtonHover(btnCheckout, "#2AA44F");

        actionButtons.getChildren().addAll(btnClearCart, btnContinueShopping, btnCheckout);

        summary.getChildren().addAll(itemCountRow, totalRow, divider, actionButtons);
        return summary;
    }

    // ==================== RECEIPT PAGE ====================
    private VBox createModernReceiptPage() {
        VBox container = new VBox();
        container.setPrefWidth(COLUMN_WIDTH);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-padding: " + SPACE.XL + "px;");

        VBox receiptCard = new VBox(SPACE.LG);
        receiptCard.setStyle(COMPS.getCard());
        receiptCard.setAlignment(Pos.CENTER);
        receiptCard.setMaxWidth(400);

        // Success icon
        Label successIcon = new Label("✅");
        successIcon.setStyle("-fx-font-size: 48px;");

        Label successTitle = new Label("Order Confirmed!");
        successTitle.setStyle(COMPS.getHeading2() + " -fx-text-fill: " + COLORS.SUCCESS + ";");

        Label successMessage = new Label("Thank you for your purchase. Your order has been successfully processed.");
        successMessage.setStyle(COMPS.getBodyText());
        successMessage.setWrapText(true);
        successMessage.setAlignment(Pos.CENTER);

        // Receipt display
        VBox receiptDisplay = new VBox(SPACE.MD);
        receiptDisplay.setStyle(
                "-fx-background-color: " + COLORS.PRIMARY_LIGHT + "; " +
                        "-fx-background-radius: " + BORDERS.MD + "px; " +
                        "-fx-padding: " + SPACE.MD + "px;"
        );

        Label receiptTitle = new Label("📄 Order Receipt");
        receiptTitle.setStyle(COMPS.getHeading3());

        taReceipt = new TextArea();
        taReceipt.setEditable(false);
        taReceipt.setWrapText(true);
        taReceipt.setStyle(
                "-fx-control-inner-background: " + COLORS.PRIMARY_LIGHT + "; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent; " +
                        "-fx-font-family: 'Consolas', monospace; " +
                        "-fx-font-size: 12px; " +
                        "-fx-text-fill: " + COLORS.TEXT_PRIMARY + ";"
        );
        taReceipt.setPrefHeight(150);

        receiptDisplay.getChildren().addAll(receiptTitle, taReceipt);

        // Action button
        Button btnCloseReceipt = new Button("← Back to Shopping");
        btnCloseReceipt.setStyle(COMPS.getPrimaryButton());
        btnCloseReceipt.setOnAction(this::buttonClicked);
        setupButtonHover(btnCloseReceipt, COLORS.PRIMARY_DARK);

        receiptCard.getChildren().addAll(
                successIcon, successTitle, successMessage,
                receiptDisplay, btnCloseReceipt
        );

        container.getChildren().add(receiptCard);
        return container;
    }

    // ==================== METHODS ====================

    private void buttonClicked(ActionEvent event) {
        try {
            Button btn = (Button) event.getSource();
            String buttonText = btn.getText();
            String action = mapButtonToAction(buttonText);

            if (action.equals("Add to Trolley")) {
                showTrolleyOrReceiptPage(vbTrolleyPage);
            } else if (action.equals("OK & Close") || buttonText.contains("Back to Shopping")) {
                showTrolleyOrReceiptPage(vbTrolleyPage);
            }

            if (action != null) {
                cusController.doAction(action);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred: " + e.getMessage());
        } catch (IOException e) {
            showAlert("IO Error", "An error occurred: " + e.getMessage());
        }
    }

    private String mapButtonToAction(String buttonText) {
        if (buttonText.contains("Search")) return "Search";
        if (buttonText.contains("Add to Cart")) return "Add to Trolley";
        if (buttonText.contains("Clear Cart")) return "Cancel";
        if (buttonText.contains("Continue Shopping")) return "Cancel";
        if (buttonText.contains("Checkout")) return "Check Out";
        if (buttonText.contains("Back to Shopping")) return "OK & Close";
        return null;
    }

    public void changeQuantity(Product product, int delta) {
        try {
            cusController.changeQuantity(product, delta);
            if (lvTrolley != null) {
                lvTrolley.refresh();
                updateTotalPrice();
                updateItemCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update quantity: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: " + COLORS.SURFACE + "; " +
                        "-fx-font-family: " + TYPO.FONT_PRIMARY + ";"
        );
        dialogPane.setPrefSize(400, 200);

        alert.showAndWait();
    }

    public void update(String imageName, String searchResult, List<Product> trolleyList, String receipt) {
        // Update product image
        try {
            if (imageName != null && !imageName.isEmpty()) {
                ivProduct.setImage(new Image(imageName));
            }
        } catch (Exception e) {
            ivProduct.setImage(new Image("imageHolder.jpg"));
        }

        // Update product info
        lbProductInfo.setText(searchResult);

        // Update trolley
        updateTrolley(trolleyList != null ? trolleyList : new ArrayList<>());

        // Show receipt if available
        if (receipt != null && !receipt.isEmpty()) {
            showTrolleyOrReceiptPage(vbReceiptPage);
            taReceipt.setText(receipt);
        }
    }

    public void updateTrolley(List<Product> products) {
        if (lvTrolley != null) {
            lvTrolley.getItems().setAll(products);
            lvTrolley.refresh();
            updateTotalPrice();
            updateItemCount();
        }
    }

    private void updateTotalPrice() {
        if (lvTrolley != null && laTotalPrice != null) {
            double total = 0.0;
            for (Product product : lvTrolley.getItems()) {
                total += product.getUnitPrice() * product.getOrderedQuantity();
            }
            laTotalPrice.setText(String.format("£%.2f", total));
        }
    }

    private void updateItemCount() {
        if (lvTrolley != null && laItemCount != null) {
            int itemCount = lvTrolley.getItems().size();
            laItemCount.setText("Items (" + itemCount + "):");
        }
    }

    private void showTrolleyOrReceiptPage(Node pageToShow) {
        int lastIndex = hbRoot.getChildren().size() - 1;
        if (lastIndex >= 0) {
            hbRoot.getChildren().set(lastIndex, pageToShow);
        }
    }

    WindowBounds getWindowBounds() {
        return new WindowBounds(
                viewWindow.getX(), viewWindow.getY(),
                viewWindow.getWidth(), viewWindow.getHeight()
        );
    }

    // ==================== GETTER METHODS FOR CustomerModel ====================
    /**
     * Get the search product ID from the text field
     * @return The trimmed product ID text, or empty string if field is null
     */
    public String getSearchProductId() {
        return tfId != null ? tfId.getText().trim() : "";
    }

    /**
     * Set the product ID field text
     * @param productId The product ID to set
     */
    public void setProductId(String productId) {
        if (tfId != null) {
            tfId.setText(productId);
        }
    }

    /**
     * Get the search product name from the text field
     * @return The trimmed product name text, or empty string if field is null
     */
    public String getSearchProductName() {
        return tfName != null ? tfName.getText().trim() : "";
    }

}