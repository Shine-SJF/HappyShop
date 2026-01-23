package ci553.happyshop.client.customer;

import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WinPosManager;
import ci553.happyshop.utility.WindowBounds;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.client.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ci553.happyshop.utility.StorageLocation;
import javafx.util.Duration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.sql.SQLException;

/**
 * The CustomerView is split into two sections by a line:
 * 1) Search Page
 * 2) Trolley / Receipt page
 */
public class CustomerView {
    public CustomerController cusController;

    private final int WIDTH = UIStyle.customerWinWidth;
    private final int HEIGHT = UIStyle.customerWinHeight;

    private HBox hbRoot;
    private VBox vbTrolleyPage;
    private VBox vbReceiptPage;

    public ObservableList<Product> productList;
    public ListView<Product> lvSearchResults;

    TextField tfId;
    TextField tfName;

    private ImageView ivProduct;
    private Label lbProductInfo;
    private TextArea taTrolley;
    private TextArea taReceipt;

    private TextField tfTrolleyProductId;

    private Stage viewWindow;

    public void start(Stage window) {
        VBox vbSearchPage = createSearchPage();
        vbTrolleyPage = CreateTrolleyPage();
        vbReceiptPage = createReceiptPage();

        Line line = new Line(0, 0, 0, HEIGHT);
        line.setStrokeWidth(4);
        line.setStroke(Color.PINK);
        VBox lineContainer = new VBox(line);
        lineContainer.setPrefWidth(4);
        lineContainer.setAlignment(Pos.CENTER);

        hbRoot = new HBox(10, vbSearchPage, lineContainer, vbTrolleyPage);

        // lets both sides grow when the window is bigger
        HBox.setHgrow(vbSearchPage, Priority.ALWAYS);
        HBox.setHgrow(vbTrolleyPage, Priority.ALWAYS);

        hbRoot.setAlignment(Pos.CENTER);
        hbRoot.setStyle(UIStyle.rootStyle);

        Scene scene = new Scene(hbRoot, WIDTH, HEIGHT);
        window.setScene(scene);
        window.setTitle("🛒 HappyShop Customer Client");

        // stops the window being too small for the layout
        window.setMinWidth(900);
        window.setMinHeight(500);

        window.centerOnScreen();
        window.setResizable(true);
        window.show();

        // only for position not size
        WinPosManager.registerWindow(window, 0, 0);

        viewWindow = window;

        // small fade-in when the page opens
        fadeIn(hbRoot);
    }

    private VBox createSearchPage() {
        Label laPageTitle = new Label("Search by Product ID/Name");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        tfId = new TextField();
        tfId.setPromptText("eg. 0001");
        tfId.setStyle(UIStyle.textFiledStyle);
        HBox hbId = new HBox(10, tfId);

        Label laPlaceHolder = new Label(" ".repeat(15));

        Button btnSearch = new Button("Search");
        btnSearch.setStyle(UIStyle.buttonStyle);
        addButtonEffects(btnSearch);
        btnSearch.setOnAction(this::buttonClicked);

        Button btnAddToTrolley = new Button("Add to Trolley");
        btnAddToTrolley.setStyle(UIStyle.buttonStyle);
        addButtonEffects(btnAddToTrolley);
        btnAddToTrolley.setOnAction(this::buttonClicked);

        Button btnWarehouse = new Button("Warehouse");
        btnWarehouse.setStyle(UIStyle.buttonStyle);
        addButtonEffects(btnWarehouse);
        btnWarehouse.setOnAction(e -> Main.showWarehouse());

        Button btnPicker = new Button("Picker");
        btnPicker.setStyle(UIStyle.buttonStyle);
        addButtonEffects(btnPicker);
        btnPicker.setOnAction(e -> Main.showPicker());

        Button btnEmergency = new Button("Emergency");
        btnEmergency.setStyle(UIStyle.buttonStyle);
        addButtonEffects(btnEmergency);
        btnEmergency.setOnAction(e -> Main.showEmergencyExit());

        // keeps the main actions on one row
        HBox hbMainBtns = new HBox(10, btnSearch, btnAddToTrolley);
        hbMainBtns.setAlignment(Pos.CENTER);

        // navigation buttons on their own row so they don’t get squashed
        HBox hbNavBtns = new HBox(10, btnWarehouse, btnPicker, btnEmergency);
        hbNavBtns.setAlignment(Pos.CENTER);

        // makes sure button text doesn’t turn into "..."
        btnSearch.setMinWidth(Region.USE_PREF_SIZE);
        btnAddToTrolley.setMinWidth(Region.USE_PREF_SIZE);
        btnWarehouse.setMinWidth(Region.USE_PREF_SIZE);
        btnPicker.setMinWidth(Region.USE_PREF_SIZE);
        btnEmergency.setMinWidth(Region.USE_PREF_SIZE);

        productList = FXCollections.observableArrayList();
        lvSearchResults = new ListView<>(productList);
        lvSearchResults.setPrefHeight(HEIGHT - 260);
        VBox.setVgrow(lvSearchResults, Priority.ALWAYS);
        lvSearchResults.setStyle(UIStyle.listViewStyle);

        lvSearchResults.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);

                if (empty || product == null) {
                    setGraphic(null);
                } else {
                    String imageName = product.getProductImageName();
                    String relativeImageUrl = StorageLocation.imageFolder + imageName;
                    Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
                    String imageFullUri = imageFullPath.toUri().toString();

                    ImageView ivPro;
                    try {
                        ivPro = new ImageView(new Image(imageFullUri, 50, 45, true, true));
                    } catch (Exception e) {
                        ivPro = new ImageView(new Image("imageHolder.jpg", 50, 45, true, true));
                    }

                    Label lbTopLine = new Label(
                            "Id: " + product.getProductId() +
                                    "   £" + product.getUnitPrice() + " / unit"
                    );

                    Label lbSecondLine = new Label(
                            "Name: " + product.getProductDescription()
                    );

                    Label lbThirdLine = new Label(
                            "Stock left: " + product.getStockQuantity()
                    );

                    // makes the text easier to read
                    lbTopLine.setStyle("-fx-font-weight: bold;");
                    lbSecondLine.setStyle("-fx-font-size: 12px;");
                    lbThirdLine.setStyle("-fx-font-size: 11px; -fx-text-fill: #444444;");

                    // stacks the text vertically
                    VBox textBox = new VBox(2, lbTopLine, lbSecondLine, lbThirdLine);
                    textBox.setAlignment(Pos.CENTER_LEFT);

                    // image + text together
                    HBox row = new HBox(12, ivPro, textBox);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setMinHeight(70);
                    row.setStyle("-fx-padding: 8px;");

                    setGraphic(row);

                }
            }
        });

        ivProduct = new ImageView("imageHolder.jpg");
        ivProduct.setFitHeight(100);
        ivProduct.setPreserveRatio(true);

        lbProductInfo = new Label("Product info will appear here");
        lbProductInfo.setStyle(UIStyle.labelTitleStyle);

        VBox vbSearchPage = new VBox(15, laPageTitle, hbId, hbMainBtns, hbNavBtns, lvSearchResults);
        vbSearchPage.setAlignment(Pos.TOP_CENTER);
        vbSearchPage.setStyle("-fx-padding: 15px;");

        return vbSearchPage;
    }

    private VBox CreateTrolleyPage() {
        Label laPageTitle = new Label("🛒🛒  Trolley 🛒🛒");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        taTrolley = new TextArea();
        taTrolley.setEditable(false);

        taTrolley.setPrefHeight(HEIGHT - 180);
        taTrolley.setWrapText(true);
        VBox.setVgrow(taTrolley, Priority.ALWAYS);

        taTrolley.setStyle("-fx-control-inner-background: white; -fx-border-color: #dddddd; -fx-border-radius: 6;");

        Label laEditTrolley = new Label("Edit item (Product ID):");
        laEditTrolley.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        tfTrolleyProductId = new TextField();
        tfTrolleyProductId.setPromptText("e.g. 0001");
        tfTrolleyProductId.setStyle(UIStyle.textFiledStyle);
        tfTrolleyProductId.setMaxWidth(120);

        Button btnRemoveItem = new Button("Remove");
        Button btnMinusOne = new Button("−");
        Button btnPlusOne = new Button("+");

        // a bit bigger so they’re easier to click
        btnRemoveItem.setPrefSize(120, 40);
        btnMinusOne.setPrefSize(60, 40);
        btnPlusOne.setPrefSize(60, 40);

        btnRemoveItem.setStyle(UIStyle.buttonStyle);
        btnMinusOne.setStyle(UIStyle.buttonStyle);
        btnPlusOne.setStyle(UIStyle.buttonStyle);

        addButtonEffects(btnRemoveItem);
        addButtonEffects(btnMinusOne);
        addButtonEffects(btnPlusOne);

        btnRemoveItem.setOnAction(e -> {
            cusController.removeItemFromTrolley(tfTrolleyProductId.getText());
            pulseTrolleyBox();
        });

        btnMinusOne.setOnAction(e -> {
            cusController.decreaseItemQuantity(tfTrolleyProductId.getText());
            pulseTrolleyBox();
        });

        btnPlusOne.setOnAction(e -> {
            cusController.increaseItemQuantity(tfTrolleyProductId.getText());
            pulseTrolleyBox();
        });

        HBox hbItemBtns = new HBox(8, btnRemoveItem, btnMinusOne, btnPlusOne);
        hbItemBtns.setAlignment(Pos.CENTER);

        VBox vbItemControls = new VBox(6, laEditTrolley, tfTrolleyProductId, hbItemBtns);
        vbItemControls.setStyle("-fx-padding: 5px;");
        vbItemControls.setMaxHeight(110);
        vbItemControls.setAlignment(Pos.CENTER);

        Button btnCancel = new Button("Cancel");
        btnCancel.setPrefSize(140, 45);
        btnCancel.setStyle(UIStyle.buttonStyle);
        addButtonEffects(btnCancel);
        btnCancel.setOnAction(e -> {
            buttonClicked(new ActionEvent(btnCancel, btnCancel));
            pulseTrolleyBox();
        });

        Button btnCheckout = new Button("Check Out");
        btnCheckout.setPrefSize(160, 45);
        btnCheckout.setStyle(UIStyle.buttonStyle);
        addButtonEffects(btnCheckout);
        btnCheckout.setOnAction(e -> {
            buttonClicked(new ActionEvent(btnCheckout, btnCheckout));
            pulseTrolleyBox();
        });

        HBox hbBtns = new HBox(12, btnCancel, btnCheckout);
        hbBtns.setStyle("-fx-padding: 10px 15px 15px 15px;");
        hbBtns.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.SOMETIMES);

        vbTrolleyPage = new VBox(10, laPageTitle, taTrolley, spacer, vbItemControls, hbBtns);
        vbTrolleyPage.setAlignment(Pos.TOP_CENTER);
        vbTrolleyPage.setStyle("-fx-padding: 15px;");

        return vbTrolleyPage;
    }

    private VBox createReceiptPage() {
        Label laPageTitle = new Label("Receipt");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        taReceipt = new TextArea();
        taReceipt.setEditable(false);
        taReceipt.setPrefSize(WIDTH / 2, HEIGHT - 50);
        taReceipt.setWrapText(true);

        Button btnCloseReceipt = new Button("OK & Close");
        btnCloseReceipt.setStyle(UIStyle.buttonStyle);
        addButtonEffects(btnCloseReceipt);
        btnCloseReceipt.setOnAction(this::buttonClicked);

        vbReceiptPage = new VBox(15, laPageTitle, taReceipt, btnCloseReceipt);
        vbReceiptPage.setAlignment(Pos.TOP_CENTER);
        vbReceiptPage.setStyle(UIStyle.rootStyleYellow);
        return vbReceiptPage;
    }

    private void buttonClicked(ActionEvent event) {
        try {
            Button btn = (Button) event.getSource();
            String action = btn.getText();

            if (action.equals("Add to Trolley") || action.equals("OK & Close")) {
                showTrolleyOrReceiptPage(vbTrolleyPage);
            }

            cusController.doAction(action);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String imageName, String searchResult, String trolley, String receipt) {
        ivProduct.setImage(new Image(imageName));
        lbProductInfo.setText(searchResult);
        taTrolley.setText(trolley);

        pulseTrolleyBox();

        if (!receipt.equals("")) {
            showTrolleyOrReceiptPage(vbReceiptPage);
            taReceipt.setText(receipt);
        }
    }

    private void showTrolleyOrReceiptPage(Node pageToShow) {
        int lastIndex = hbRoot.getChildren().size() - 1;
        if (lastIndex >= 0) {
            hbRoot.getChildren().set(lastIndex, pageToShow);
            fadeIn(pageToShow);
        }
    }

    WindowBounds getWindowBounds() {
        return new WindowBounds(viewWindow.getX(), viewWindow.getY(),
                viewWindow.getWidth(), viewWindow.getHeight());
    }

    // simple hover + press feedback for buttons
    private void addButtonEffects(Button btn) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(8);
        shadow.setColor(Color.color(0, 0, 0, 0.18));

        btn.setOnMouseEntered(e -> btn.setEffect(shadow));
        btn.setOnMouseExited(e -> btn.setEffect(null));

        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.97);
            btn.setScaleY(0.97);
        });

        btn.setOnMouseReleased(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
    }

    // MenuButton is a different type, so it needs its own small hover effect
    private void addButtonEffects(MenuButton btn) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(8);
        shadow.setColor(Color.color(0, 0, 0, 0.18));

        btn.setOnMouseEntered(e -> btn.setEffect(shadow));
        btn.setOnMouseExited(e -> btn.setEffect(null));
    }

    // quick pulse on the trolley area so the user sees something changed
    private void pulseTrolleyBox() {
        if (taTrolley == null) return;

        ScaleTransition st = new ScaleTransition(Duration.millis(120), taTrolley);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.01);
        st.setToY(1.01);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    // soft fade-in for UI elements
    private void fadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(180), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
}
