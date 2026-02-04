package ci553.happyshop.client.auth;

import ci553.happyshop.auth.User;
import ci553.happyshop.auth.UserRole;
import ci553.happyshop.client.customer.CustomerClient;
import ci553.happyshop.client.emergency.EmergencyExit;
import ci553.happyshop.client.orderTracker.OrderTracker;
import ci553.happyshop.client.picker.PickerClient;
import ci553.happyshop.client.warehouse.WarehouseClient;
import ci553.happyshop.orderManagement.OrderHub;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

    // the loginview is the user interface layer for authentication in this javafx applciation/ it haandles everthing the user sees and interact during login

public class LoginView {
    public LoginController loginController;

    private Stage stage;
    private TextField tfUsername;
    private PasswordField pfPassword;
    private Label lblMessage;
    private String currentMode = "customer";

    private final int WIDTH = 450;
    private final int HEIGHT = 600;

    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        showCustomerLoginScreen();
        stage.show();
    }

    private void showCustomerLoginScreen() {
        currentMode = "customer";
        VBox root = createLoginScreen("🛍️ Customer Login", "Continue as Customer", true);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle("HappyShop - Customer Login");
    }

    public void showWarehouseLoginScreen() {
        currentMode = "warehouse";
        VBox root = createLoginScreen("🏭 Warehouse Login", "Staff/Admin Login", false);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle("HappyShop - Warehouse Login");
    }

    private VBox createLoginScreen(String title, String buttonText, boolean showRegister) {
        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        container.setStyle("-fx-background-color: #F8F9FA;");

        VBox loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(40));
        loginCard.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        loginCard.setMaxWidth(380);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #4A90E2;");

        VBox usernameBox = new VBox(5);
        Label lblUsername = new Label("Username");
        lblUsername.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        tfUsername = new TextField();
        tfUsername.setPromptText("Enter username");
        tfUsername.setStyle(
                "-fx-font-size: 14px; -fx-padding: 12px; " +
                        "-fx-background-color: #F8F9FA; -fx-border-color: #DDD; " +
                        "-fx-border-radius: 8px; -fx-background-radius: 8px;"
        );
        tfUsername.setPrefWidth(320);
        usernameBox.getChildren().addAll(lblUsername, tfUsername);

        VBox passwordBox = new VBox(5);
        Label lblPassword = new Label("Password");
        lblPassword.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        pfPassword = new PasswordField();
        pfPassword.setPromptText("Enter password");
        pfPassword.setStyle(tfUsername.getStyle());
        pfPassword.setPrefWidth(320);
        passwordBox.getChildren().addAll(lblPassword, pfPassword);

        lblMessage = new Label("");
        lblMessage.setStyle("-fx-text-fill: #FF3B30; -fx-font-size: 13px;");
        lblMessage.setVisible(false);
        lblMessage.setWrapText(true);
        lblMessage.setMaxWidth(320);

        Button btnLogin = new Button(buttonText);
        btnLogin.setStyle(
                "-fx-background-color: #4A90E2; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-padding: 15px 30px; -fx-background-radius: 10px; " +
                        "-fx-cursor: hand;"
        );
        btnLogin.setPrefWidth(320);
        btnLogin.setOnAction(e -> handleLogin());

        pfPassword.setOnAction(e -> handleLogin());
        tfUsername.setOnAction(e -> pfPassword.requestFocus());

        loginCard.getChildren().addAll(lblTitle, usernameBox, passwordBox, lblMessage, btnLogin);

        if (showRegister) {
            HBox registerBox = new HBox(5);
            registerBox.setAlignment(Pos.CENTER);
            Label registerLabel = new Label("Don't have an account?");
            registerLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
            Hyperlink registerLink = new Hyperlink("Register here");
            registerLink.setStyle("-fx-text-fill: #4A90E2; -fx-font-size: 13px;");
            registerLink.setOnAction(e -> showRegistrationScreen());
            registerBox.getChildren().addAll(registerLabel, registerLink);
            loginCard.getChildren().add(registerBox);
        }

        HBox switchBox = new HBox(5);
        switchBox.setAlignment(Pos.CENTER);
        switchBox.setPadding(new Insets(10, 0, 0, 0));
        Hyperlink switchLink = new Hyperlink(showRegister ? "Staff/Admin Login →" : "← Customer Login");
        switchLink.setStyle("-fx-text-fill: #4A90E2; -fx-font-size: 13px;");
        switchLink.setOnAction(e -> {
            if (showRegister) {
                showWarehouseLoginScreen();
            } else {
                showCustomerLoginScreen();
            }
        });
        switchBox.getChildren().add(switchLink);
        loginCard.getChildren().add(switchBox);

        container.getChildren().add(loginCard);
        return container;
    }

    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter username and password", true);
            return;
        }

        lblMessage.setText("Authenticating...");
        lblMessage.setStyle("-fx-text-fill: #4A90E2; -fx-font-size: 13px;");
        lblMessage.setVisible(true);

        new Thread(() -> {
            User user = loginController.login(username, password);

            Platform.runLater(() -> {
                if (user != null) {
                    if (currentMode.equals("customer") && user.getRole() == UserRole.CUSTOMER) {
                        launchClientForUser(user);
                    } else if (currentMode.equals("warehouse") && user.getRole() != UserRole.CUSTOMER) {
                        launchClientForUser(user);
                    } else {
                        showMessage("Access denied for this login screen", true);
                    }
                } else {
                    showMessage("Invalid username or password", true);
                }
            });
        }).start();
    }

    private void showRegistrationScreen() {
        RegisterView registerView = new RegisterView();
        registerView.loginView = this;
        registerView.start(stage);
    }

    private void launchClientForUser(User user) {
        stage.close();

        try {
            OrderHub orderHub = OrderHub.getOrderHub();
            orderHub.initializeOrderMap();

            if (user.getRole() == UserRole.CUSTOMER) {
                Platform.runLater(() -> {
                    try {
                        CustomerClient customerClient = new CustomerClient();
                        Stage customerStage = new Stage();
                        customerClient.start(customerStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else if (user.getRole() == UserRole.STAFF) {
                launchWarehouseSystem(false);
            } else if (user.getRole() == UserRole.ADMIN) {
                launchWarehouseSystem(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchWarehouseSystem(boolean includeEmergencyExit) {
        Platform.runLater(() -> {
            try {
                WarehouseClient warehouseClient = new WarehouseClient();
                Stage warehouseStage = new Stage();
                warehouseClient.start(warehouseStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Platform.runLater(() -> {
            try {
                OrderTracker orderTracker = new OrderTracker();
                orderTracker.registerWithOrderHub();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Platform.runLater(() -> {
            try {
                PickerClient pickerClient = new PickerClient();
                Stage pickerStage = new Stage();
                pickerClient.start(pickerStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (includeEmergencyExit) {
            Platform.runLater(() -> {
                try {
                    EmergencyExit.getEmergencyExit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void showMessage(String message, boolean isError) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: " + (isError ? "#FF3B30" : "#34C759") + "; -fx-font-size: 13px;");
        lblMessage.setVisible(true);
    }
}