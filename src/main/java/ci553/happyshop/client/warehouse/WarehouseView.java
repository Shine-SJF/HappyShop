package ci553.happyshop.client.warehouse;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.WinPosManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * - Clean, professional interface for warehouse management
 * - Consistent styling with modern design patterns
 * - All inline styles replaced with centralized style system
 */
public class WarehouseView {
    private final int WIDTH = 1400;
    private final int HEIGHT = 850;
    private final int COLUMN_WIDTH = (WIDTH / 2) - 30;

    public WarehouseController controller;
    private Stage viewWindow;
    private Scene mainScene;

    // Search Page Components
    TextField tfSearchKeyword;
    private Label laSearchSummary;
    private ObservableList<Product> obeProductList;
    ListView<Product> obrLvProducts;

    // Product Form Components
    ComboBox<String> cbProductFormMode;
    private VBox vbEditProduct;
    private VBox vbNewProduct;
    String theProFormMode = "EDIT";

    // Edit Product Components
    private TextField tfIdEdit;
    TextField tfPriceEdit;
    TextField tfStockEdit;
    TextField tfChangeByEdit;
    TextArea taDescriptionEdit;
    private ImageView ivProEdit;
    String userSelectedImageUriEdit;
    boolean isUserSelectedImageEdit = false;
    private Button btnAdd;
    private Button btnSub;
    private Button btnCancelEdit;
    private Button btnSubmitEdit;

    // New Product Components
    TextField tfIdNewPro;
    TextField tfPriceNewPro;
    TextField tfStockNewPro;
    TextArea taDescriptionNewPro;
    private ImageView ivProNewPro;
    String imageUriNewPro;

    // Stock Dashboard
    private StockDashboard stockDashboard;

    // Reference to UI Styles
    private static final WarehouseUIStyles.Colors COLORS = new WarehouseUIStyles.Colors();
    private static final WarehouseUIStyles.Typography TYPO = new WarehouseUIStyles.Typography();
    private static final WarehouseUIStyles.Spacing SPACE = new WarehouseUIStyles.Spacing();
    private static final WarehouseUIStyles.Borders BORDERS = new WarehouseUIStyles.Borders();
    private static final WarehouseUIStyles.Components COMPS = new WarehouseUIStyles.Components();

    public void start(Stage window) {
        VBox vbSearchPage = createModernSearchPage();
        VBox vbProductFormPage = createModernProductFormPage();

        // Modern divider
        Line line = new Line(0, 0, 0, HEIGHT);
        line.setStyle(COMPS.getDivider());
        VBox lineContainer = new VBox(line);
        lineContainer.setPrefWidth(4);
        lineContainer.setAlignment(Pos.CENTER);

        // Main layout
        HBox hbRoot = new HBox(15, vbSearchPage, lineContainer, vbProductFormPage);
        hbRoot.setStyle(COMPS.getRootStyle());

        mainScene = new Scene(hbRoot, WIDTH, HEIGHT);
        window.setScene(mainScene);
        window.setTitle("🏭 HappyShop Warehouse | Product Management System");

        WinPosManager.registerWindow(window, WIDTH, HEIGHT);
        window.show();
        viewWindow = window;

        // Initialize Stock Dashboard
        stockDashboard = new StockDashboard(this);
    }

    // ==================== SEARCH PAGE ====================
    private VBox createModernSearchPage() {
        VBox container = new VBox(SPACE.LG);
        container.setPrefWidth(COLUMN_WIDTH);
        container.setStyle(String.format("-fx-padding: %fpx;", SPACE.MD));

        // Header
        HBox header = createSearchHeader();

        // Search section
        VBox searchSection = createSearchSection();

        // Results section
        VBox resultsSection = createResultsSection();

        container.getChildren().addAll(header, searchSection, resultsSection);
        return container;
    }

    private HBox createSearchHeader() {
        HBox header = new HBox(SPACE.MD);
        header.setAlignment(Pos.CENTER_LEFT);

        // Title with icon
        Label title = new Label("🔍 Product Search");
        title.setStyle(COMPS.getHeading2());

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Warehouse badge
        Label badge = new Label("Warehouse Management");
        badge.setStyle(String.format(
                "-fx-font-size: 12px; " +
                        "-fx-font-weight: 600; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-color: %s; " +
                        "-fx-padding: 4px 12px; " +
                        "-fx-background-radius: 12px;",
                COLORS.PRIMARY
        ));

        header.getChildren().addAll(title, spacer, badge);
        return header;
    }

    private VBox createSearchSection() {
        VBox searchCard = new VBox(SPACE.MD);
        searchCard.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: %fpx; " +
                        "-fx-background-radius: %fpx; " +
                        "-fx-padding: %fpx;",
                COLORS.SURFACE, COLORS.BORDER, BORDERS.LG, BORDERS.LG, SPACE.LG
        ));

        // Search input
        Label searchLabel = new Label("Search by Product ID or Name");
        searchLabel.setStyle(COMPS.getBodyText());

        tfSearchKeyword = new TextField();
        tfSearchKeyword.setPromptText("Enter product ID or name...");
        tfSearchKeyword.setStyle(COMPS.getTextField());
        tfSearchKeyword.setOnAction(e -> performSearch());
        setupTextFieldHover(tfSearchKeyword);

        // Button row
        HBox buttonRow = new HBox(SPACE.SM);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        Button btnSearch = new Button("🔎 Search");
        btnSearch.setStyle(COMPS.getSearchButton());
        btnSearch.setOnAction(this::buttonClick);
        setupButtonHover(btnSearch, COLORS.PRIMARY, COLORS.PRIMARY_DARK);

        Button btnDashboard = new Button("📊 Dashboard");
        btnDashboard.setStyle(COMPS.getInfoButton());
        btnDashboard.setOnAction(e -> showStockDashboard());
        setupButtonHover(btnDashboard, COLORS.INFO, WarehouseUIStyles.darkenColor(COLORS.INFO, 0.15));

        buttonRow.getChildren().addAll(btnSearch, btnDashboard);

        searchCard.getChildren().addAll(searchLabel, tfSearchKeyword, buttonRow);
        return searchCard;
    }

    private VBox createResultsSection() {
        VBox resultsCard = new VBox(SPACE.MD);
        resultsCard.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: %fpx; " +
                        "-fx-background-radius: %fpx; " +
                        "-fx-padding: %fpx;",
                COLORS.SURFACE, COLORS.BORDER, BORDERS.LG, BORDERS.LG, SPACE.MD
        ));

        // Results header
        HBox resultsHeader = new HBox(SPACE.MD);
        resultsHeader.setAlignment(Pos.CENTER_LEFT);

        laSearchSummary = new Label("No search performed yet");
        laSearchSummary.setStyle(String.format(
                "-fx-font-size: %fpx; " +
                        "-fx-font-weight: %s; " +
                        "-fx-text-fill: %s;",
                TYPO.BODY, TYPO.SEMIBOLD, COLORS.TEXT_SECONDARY
        ));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEdit = new Button("✏️ Edit");
        btnEdit.setStyle(COMPS.getSuccessButton());
        btnEdit.setOnAction(this::buttonClick);
        setupButtonHover(btnEdit, COLORS.SUCCESS, COLORS.SUCCESS_DARK);

        Button btnDelete = new Button("🗑️ Delete");
        btnDelete.setStyle(COMPS.getErrorButton());
        btnDelete.setOnAction(this::buttonClick);
        setupButtonHover(btnDelete, COLORS.ERROR, COLORS.ERROR_DARK);

        resultsHeader.getChildren().addAll(laSearchSummary, spacer, btnEdit, btnDelete);

        // List view
        obeProductList = FXCollections.observableArrayList();
        obrLvProducts = new ListView<>(obeProductList);
        obrLvProducts.setPrefHeight(HEIGHT - 300);
        obrLvProducts.setStyle(COMPS.getListView());

        obrLvProducts.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);

                if (empty || product == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(createProductListItem(product));
                    setText(null);
                }
            }
        });

        resultsCard.getChildren().addAll(resultsHeader, obrLvProducts);
        return resultsCard;
    }

    private HBox createProductListItem(Product product) {
        HBox itemCard = new HBox(SPACE.MD);
        itemCard.setAlignment(Pos.CENTER_LEFT);
        itemCard.setStyle(String.format(
                "-fx-padding: %fpx; " +
                        "-fx-border-color: %s; " +
                        "-fx-border-width: 0 0 1 0;",
                SPACE.SM, COLORS.DIVIDER
        ));

        // Product image
        String imageName = product.getProductImageName();
        String relativeImageUrl = StorageLocation.imageFolder + imageName;
        Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
        String imageFullUri = imageFullPath.toUri().toString();

        ImageView ivPro;
        try {
            ivPro = new ImageView(new Image(imageFullUri, 60, 60, true, true));
        } catch (Exception e) {
            ivPro = new ImageView(new Image("imageHolder.jpg", 60, 60, true, true));
        }
        ivPro.setStyle(String.format(
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2); " +
                        "-fx-background-radius: %fpx;",
                BORDERS.MD
        ));

        // Product info
        VBox infoBox = new VBox(SPACE.XS);
        Label idLabel = new Label("ID: " + product.getProductId());
        idLabel.setStyle(String.format(
                "-fx-font-size: %fpx; " +
                        "-fx-font-weight: %s; " +
                        "-fx-text-fill: %s;",
                TYPO.BODY, TYPO.BOLD, COLORS.TEXT_PRIMARY
        ));

        Label nameLabel = new Label(product.getProductDescription());
        nameLabel.setStyle(COMPS.getBodyText());
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(300);

        Label priceLabel = new Label(String.format("Price: £%.2f", product.getUnitPrice()));
        priceLabel.setStyle(String.format(
                "-fx-font-size: %fpx; " +
                        "-fx-text-fill: %s;",
                TYPO.BODY_SMALL, COLORS.TEXT_SECONDARY
        ));

        infoBox.getChildren().addAll(idLabel, nameLabel, priceLabel);

        // Stock badge
        Label stockLabel = new Label(String.format("Stock: %d", product.getStockQuantity()));
        stockLabel.setStyle(String.format(
                "-fx-font-size: %fpx; " +
                        "-fx-font-weight: %s; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-color: %s; " +
                        "-fx-padding: 4px 12px; " +
                        "-fx-background-radius: 12px;",
                TYPO.BODY_SMALL, TYPO.BOLD,
                product.getStockQuantity() > 20 ? COLORS.STOCK_HIGH :
                        product.getStockQuantity() > 10 ? COLORS.STOCK_MEDIUM :
                                product.getStockQuantity() > 0 ? COLORS.STOCK_LOW : COLORS.STOCK_CRITICAL
        ));

        itemCard.getChildren().addAll(ivPro, infoBox, stockLabel);
        return itemCard;
    }

    // ==================== PRODUCT FORM PAGE ====================
    private VBox createModernProductFormPage() {
        VBox container = new VBox(SPACE.LG);
        container.setPrefWidth(COLUMN_WIDTH + 20);
        container.setStyle(String.format("-fx-padding: %fpx;", SPACE.MD));

        // Mode selector
        cbProductFormMode = new ComboBox<>();
        cbProductFormMode.setStyle(COMPS.getComboBox());
        cbProductFormMode.getItems().addAll(
                "✏️ Edit Existing Product",
                "➕ Add New Product"
        );
        cbProductFormMode.setValue("✏️ Edit Existing Product");

        vbEditProduct = createModernEditForm();
        disableEditProductChild(true);
        vbNewProduct = createModernNewProductForm();

        VBox formContainer = new VBox(SPACE.MD, cbProductFormMode, vbEditProduct);

        cbProductFormMode.setOnAction(e -> {
            if (cbProductFormMode.getValue().contains("Edit")) {
                formContainer.getChildren().set(1, vbEditProduct);
                theProFormMode = "EDIT";
            } else {
                formContainer.getChildren().set(1, vbNewProduct);
                theProFormMode = "NEW";
            }
        });

        container.getChildren().add(formContainer);
        return container;
    }

    private VBox createModernEditForm() {
        VBox form = new VBox(SPACE.LG);
        form.setStyle(COMPS.getEditFormStyle());

        // Form title
        Label title = new Label("✏️ Edit Product Details");
        title.setStyle(COMPS.getHeading3());

        // ID and Price row
        HBox idPriceRow = new HBox(SPACE.MD);
        VBox idBox = createFormField("Product ID", tfIdEdit = new TextField(), true);
        VBox priceBox = createFormField("Price (£)", tfPriceEdit = new TextField(), false);
        idPriceRow.getChildren().addAll(idBox, priceBox);

        // Stock management
        VBox stockBox = createStockManagementSection();

        // Image section
        VBox imageSection = createImageSection(true);

        // Description
        Label desLabel = new Label("Description");
        desLabel.setStyle(COMPS.getLabelStyle());
        taDescriptionEdit = new TextArea();
        taDescriptionEdit.setPrefHeight(80);
        taDescriptionEdit.setWrapText(true);
        taDescriptionEdit.setStyle(COMPS.getTextArea());
        VBox descBox = new VBox(SPACE.XS, desLabel, taDescriptionEdit);

        // Action buttons
        HBox actionButtons = createFormActionButtons(true);

        form.getChildren().addAll(title, idPriceRow, stockBox, imageSection, descBox, actionButtons);
        return form;
    }

    private VBox createStockManagementSection() {
        VBox stockSection = new VBox(SPACE.SM);

        Label stockLabel = new Label("Stock Management");
        stockLabel.setStyle(COMPS.getLabelStyle());

        HBox stockControls = new HBox(SPACE.SM);
        stockControls.setAlignment(Pos.CENTER_LEFT);

        Label currentLabel = new Label("Current:");
        currentLabel.setStyle(COMPS.getBodyText());

        tfStockEdit = new TextField();
        tfStockEdit.setEditable(false);
        tfStockEdit.setPrefWidth(80);
        tfStockEdit.setStyle(COMPS.getTextFieldReadOnly());

        Label changeLabel = new Label("Change by:");
        changeLabel.setStyle(COMPS.getBodyText());

        tfChangeByEdit = new TextField();
        tfChangeByEdit.setPromptText("0");
        tfChangeByEdit.setPrefWidth(70);
        tfChangeByEdit.setStyle(COMPS.getTextField());

        btnAdd = new Button("➕");
        btnAdd.setStyle(COMPS.getIconButton(COLORS.SUCCESS));
        btnAdd.setOnAction(this::buttonClick);
        setupButtonHover(btnAdd, COLORS.SUCCESS, COLORS.SUCCESS_DARK);

        btnSub = new Button("➖");
        btnSub.setStyle(COMPS.getIconButton(COLORS.ERROR));
        btnSub.setOnAction(this::buttonClick);
        setupButtonHover(btnSub, COLORS.ERROR, COLORS.ERROR_DARK);

        stockControls.getChildren().addAll(
                currentLabel, tfStockEdit, changeLabel, tfChangeByEdit, btnAdd, btnSub
        );

        stockSection.getChildren().addAll(stockLabel, stockControls);
        return stockSection;
    }

    private VBox createImageSection(boolean isEdit) {
        VBox imageSection = new VBox(SPACE.SM);

        Label imageLabel = new Label("Product Image");
        imageLabel.setStyle(COMPS.getLabelStyle());

        ImageView imageView;
        if (isEdit) {
            ivProEdit = new ImageView("WarehouseImageHolder.jpg");
            ivProEdit.setFitWidth(120);
            ivProEdit.setFitHeight(120);
            ivProEdit.setPreserveRatio(true);
            ivProEdit.setOnMouseClicked(this::imageChooser);
            imageView = ivProEdit;
        } else {
            ivProNewPro = new ImageView("WarehouseImageHolder.jpg");
            ivProNewPro.setFitWidth(120);
            ivProNewPro.setFitHeight(120);
            ivProNewPro.setPreserveRatio(true);
            ivProNewPro.setOnMouseClicked(this::imageChooser);
            imageView = ivProNewPro;
        }

        // Image container
        HBox imageContainer = new HBox(imageView);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setStyle(COMPS.getImageViewContainer());
        imageContainer.setCursor(javafx.scene.Cursor.HAND);

        Label hint = new Label("Click to change image");
        hint.setStyle(String.format(
                "-fx-font-size: %fpx; " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-style: italic;",
                TYPO.BODY_SMALL, COLORS.TEXT_TERTIARY
        ));

        imageSection.getChildren().addAll(imageLabel, imageContainer, hint);
        return imageSection;
    }

    private HBox createFormActionButtons(boolean isEdit) {
        HBox buttonRow = new HBox(SPACE.MD);
        buttonRow.setAlignment(Pos.CENTER);

        Button btnCancel = new Button("Cancel");
        btnCancel.setPrefWidth(120);
        btnCancel.setStyle(COMPS.getWarningButton());
        btnCancel.setOnAction(this::buttonClick);
        setupButtonHover(btnCancel, COLORS.WARNING, WarehouseUIStyles.darkenColor(COLORS.WARNING, 0.15));

        Button btnSubmit = new Button("💾 Submit");
        btnSubmit.setPrefWidth(120);
        btnSubmit.setStyle(COMPS.getInfoButton());
        btnSubmit.setOnAction(this::buttonClick);
        setupButtonHover(btnSubmit, COLORS.INFO, WarehouseUIStyles.darkenColor(COLORS.INFO, 0.15));

        if (isEdit) {
            btnCancelEdit = btnCancel;
            btnSubmitEdit = btnSubmit;
        }

        buttonRow.getChildren().addAll(btnCancel, btnSubmit);
        return buttonRow;
    }

    private VBox createModernNewProductForm() {
        VBox form = new VBox(SPACE.LG);
        form.setStyle(COMPS.getNewFormStyle());

        // Form title
        Label title = new Label("➕ Add New Product");
        title.setStyle(COMPS.getHeading3());

        // ID, Price, Stock
        VBox idBox = createFormField("Product ID*", tfIdNewPro = new TextField(), false);
        tfIdNewPro.setPromptText("4 digits (e.g., 1234)");

        VBox priceBox = createFormField("Price (£)*", tfPriceNewPro = new TextField(), false);
        tfPriceNewPro.setPromptText("0.00");

        VBox stockBox = createFormField("Initial Stock*", tfStockNewPro = new TextField(), false);
        tfStockNewPro.setPromptText("0");

        // Image section
        VBox imageSection = createImageSection(false);

        // Description
        Label desLabel = new Label("Description*");
        desLabel.setStyle(COMPS.getLabelStyle());
        taDescriptionNewPro = new TextArea();
        taDescriptionNewPro.setPrefHeight(80);
        taDescriptionNewPro.setWrapText(true);
        taDescriptionNewPro.setStyle(COMPS.getTextArea());
        VBox descBox = new VBox(SPACE.XS, desLabel, taDescriptionNewPro);

        // Action buttons
        HBox actionButtons = createFormActionButtons(false);

        form.getChildren().addAll(
                title, idBox, priceBox, stockBox, imageSection, descBox, actionButtons
        );
        return form;
    }

    private VBox createFormField(String label, TextField textField, boolean readOnly) {
        VBox fieldBox = new VBox(SPACE.XS);

        Label fieldLabel = new Label(label);
        fieldLabel.setStyle(COMPS.getLabelStyle());

        textField.setStyle(readOnly ? COMPS.getTextFieldReadOnly() : COMPS.getTextField());
        textField.setEditable(!readOnly);
        if (!readOnly) {
            setupTextFieldHover(textField);
        }

        fieldBox.getChildren().addAll(fieldLabel, textField);
        return fieldBox;
    }

    // ==================== UI HELPER METHODS ====================
    private void setupTextFieldHover(TextField textField) {
        String originalStyle = textField.getStyle();
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textField.setStyle(originalStyle + String.format(
                        " -fx-border-color: %s; " +
                                "-fx-effect: dropshadow(gaussian, rgba(124, 58, 237, 0.3), 6, 0, 0, 0);",
                        COLORS.PRIMARY
                ));
            } else {
                textField.setStyle(originalStyle);
            }
        });
    }

    private void setupButtonHover(Button button, String baseColor, String hoverColor) {
        String originalStyle = button.getStyle();
        button.setOnMouseEntered(e -> {
            button.setStyle(originalStyle.replace(
                    "-fx-background-color: " + baseColor,
                    "-fx-background-color: " + hoverColor
            ) + " -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
        });
        button.setOnMouseExited(e -> {
            button.setStyle(originalStyle);
        });
    }

    // ==================== EVENT HANDLERS ====================
    private void performSearch() {
        try {
            controller.process("🔍");
        } catch (SQLException | IOException e) {
            showError("Search Error", e.getMessage());
        }
    }

    private void buttonClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String action = btn.getText();

        if (action.contains("Edit") && obrLvProducts.getSelectionModel().getSelectedItem() != null) {
            disableEditProductChild(false);
            cbProductFormMode.setValue("✏️ Edit Existing Product");
        }

        try {
            controller.process(action);
        } catch (Exception e) {
            showError("Operation Failed", e.getMessage());
        }
    }

    private void imageChooser(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(viewWindow);

        if (file != null) {
            if (theProFormMode.equals("EDIT")) {
                isUserSelectedImageEdit = true;
                ivProEdit.setImage(new Image(file.toURI().toString()));
                userSelectedImageUriEdit = file.getAbsolutePath();
            } else if (theProFormMode.equals("NEW")) {
                ivProNewPro.setImage(new Image(file.toURI().toString()));
                imageUriNewPro = file.getAbsolutePath();
            }
        }
    }

    // ==================== UPDATE METHODS ====================
    void updateObservableProductList(ArrayList<Product> productList) {
        int proCounter = productList.size();
        laSearchSummary.setText(String.format("📦 %d product(s) found", proCounter));
        obeProductList.clear();
        obeProductList.addAll(productList);
        obrLvProducts.refresh();
    }

    void updateBtnAddSub(String stock) {
        tfStockEdit.setText(stock);
        tfChangeByEdit.clear();
    }

    void updateEditProductChild(String id, String price, String stock, String des, String imageUrl) {
        tfIdEdit.setText(id);
        tfPriceEdit.setText(price);
        tfStockEdit.setText(stock);
        taDescriptionEdit.setText(des);

        try {
            ivProEdit.setImage(new Image(imageUrl));
        } catch (Exception e) {
            ivProEdit.setImage(new Image("WarehouseImageHolder.jpg"));
        }
    }

    void resetEditChild() {
        tfIdEdit.setText("");
        tfPriceEdit.setText("");
        tfStockEdit.setText("");
        tfChangeByEdit.setText("");
        taDescriptionEdit.setText("");
        ivProEdit.setImage(new Image("WarehouseImageHolder.jpg"));
        isUserSelectedImageEdit = false;
        userSelectedImageUriEdit = null;
        disableEditProductChild(true);
    }

    void resetNewProChild() {
        tfIdNewPro.setText("");
        tfPriceNewPro.setText("");
        tfStockNewPro.setText("");
        taDescriptionNewPro.setText("");
        ivProNewPro.setImage(new Image("WarehouseImageHolder.jpg"));
        imageUriNewPro = null;
    }

    private void disableEditProductChild(boolean disable) {
        tfPriceEdit.setDisable(disable);
        tfChangeByEdit.setDisable(disable);
        btnAdd.setDisable(disable);
        btnSub.setDisable(disable);
        ivProEdit.setDisable(disable);
        taDescriptionEdit.setDisable(disable);
        btnCancelEdit.setDisable(disable);
        btnSubmitEdit.setDisable(disable);
    }

    // ==================== UTILITY METHODS ====================
    private void showStockDashboard() {
        if (stockDashboard != null) {
            stockDashboard.show();
        }
    }

    public void refreshDashboard() {
        if (stockDashboard != null) {
            stockDashboard.refresh();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-font-family: %s;",
                COLORS.SURFACE, TYPO.FONT_PRIMARY
        ));

        alert.showAndWait();
    }
}