package ci553.happyshop.client.auth;

import ci553.happyshop.auth.AuthenticationManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

    //the registerview is the user interface for customer registration in this javafx application. it handles the complete registration workflow

public class RegisterView {

    public LoginView loginView;
    private Stage stage;
    private TextField tfUsername;
    private PasswordField pfPassword;
    private TextField tfPasswordVisible;
    private PasswordField pfConfirmPassword;
    private TextField tfConfirmPasswordVisible;
    private TextField tfEmail;
    private TextField tfFullName;
    private CheckBox cbShowPassword;
    private Label lblMessage;
    private Button btnRegister;

    private final int WIDTH = 500;
    private final int HEIGHT = 850;  // Increased from 750 to show all elements

    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        VBox root = createRegistrationView();
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #F8F9FA; -fx-background-color: #F8F9FA;");

        Scene scene = new Scene(scrollPane, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createRegistrationView() {
        VBox container = new VBox(25);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(40));
        container.setStyle("-fx-background-color: #F8F9FA;");

        Button btnBack = new Button("â† Back to Login");
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #4A90E2; -fx-font-size: 14px; -fx-cursor: hand;");
        btnBack.setOnAction(e -> goBackToLogin());
        HBox backContainer = new HBox(btnBack);
        backContainer.setAlignment(Pos.TOP_LEFT);

        VBox regCard = new VBox(20);
        regCard.setAlignment(Pos.CENTER);
        regCard.setPadding(new Insets(40));
        regCard.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        regCard.setMaxWidth(400);

        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #4A90E2;");

        Label subtitle = new Label("Join HappyShop today");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        VBox usernameBox = createInputField("Username");
        tfUsername = (TextField) usernameBox.getChildren().get(1);
        tfUsername.setPromptText("At least 3 characters");

        // Add character counter hint
        Label usernameHint = new Label("Choose a unique username (3+ characters)");
        usernameHint.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");
        usernameBox.getChildren().add(usernameHint);

        VBox fullNameBox = createInputField("Full Name");
        tfFullName = (TextField) fullNameBox.getChildren().get(1);
        tfFullName.setPromptText("Your full name");

        VBox emailBox = createInputField("Email");
        tfEmail = (TextField) emailBox.getChildren().get(1);
        tfEmail.setPromptText("your.email@example.com");

        VBox passwordBox = createPasswordFields();

        lblMessage = new Label("");
        lblMessage.setStyle("-fx-text-fill: #FF3B30; -fx-font-size: 13px; -fx-wrap-text: true;");
        lblMessage.setMaxWidth(350);
        lblMessage.setWrapText(true);
        lblMessage.setVisible(false);

        btnRegister = new Button("Create Account");
        btnRegister.setStyle(
                "-fx-background-color: #34C759; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-padding: 15px 30px; -fx-background-radius: 10px; -fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(52, 199, 89, 0.3), 10, 0, 0, 3);"
        );
        btnRegister.setPrefWidth(350);
        btnRegister.setPrefHeight(50);
        btnRegister.setOnAction(e -> handleRegistration());

        // Add hover effects
        btnRegister.setOnMouseEntered(e -> btnRegister.setStyle(
                "-fx-background-color: #2EA64A; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-padding: 15px 30px; -fx-background-radius: 10px; -fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(52, 199, 89, 0.5), 15, 0, 0, 5);"
        ));
        btnRegister.setOnMouseExited(e -> btnRegister.setStyle(
                "-fx-background-color: #34C759; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-padding: 15px 30px; -fx-background-radius: 10px; -fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(52, 199, 89, 0.3), 10, 0, 0, 3);"
        ));

        HBox loginBox = new HBox(5);
        loginBox.setAlignment(Pos.CENTER);
        Label loginLabel = new Label("Already have an account?");
        loginLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        Hyperlink loginLink = new Hyperlink("Login here");
        loginLink.setStyle("-fx-text-fill: #4A90E2; -fx-font-size: 13px;");
        loginLink.setOnAction(e -> goBackToLogin());
        loginBox.getChildren().addAll(loginLabel, loginLink);

        regCard.getChildren().addAll(
                title, subtitle, usernameBox, fullNameBox, emailBox, passwordBox,
                lblMessage, btnRegister, loginBox
        );

        container.getChildren().addAll(backContainer, regCard);
        return container;
    }

    private void goBackToLogin() {
        if (loginView != null) {
            loginView.start(stage);
        }
    }

    private void handleRegistration() {
        String username = tfUsername.getText().trim();
        String fullName = tfFullName.getText().trim();
        String email = tfEmail.getText().trim();
        String password = cbShowPassword.isSelected() ? tfPasswordVisible.getText() : pfPassword.getText();
        String confirmPassword = cbShowPassword.isSelected() ? tfConfirmPasswordVisible.getText() : pfConfirmPassword.getText();

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in all fields", true);
            return;
        }

        if (username.length() < 3) {
            showMessage("Username must be at least 3 characters", true);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showMessage("Please enter a valid email address", true);
            return;
        }

        if (password.length() < 6) {
            showMessage("Password must be at least 6 characters", true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match", true);
            return;
        }

        btnRegister.setDisable(true);
        btnRegister.setText("Creating Account...");

        new Thread(() -> {
            AuthenticationManager authManager = AuthenticationManager.getInstance();
            AuthenticationManager.RegistrationResult result = authManager.registerCustomer(
                    username, password, email, fullName
            );

            Platform.runLater(() -> {
                btnRegister.setDisable(false);
                btnRegister.setText("Create Account");

                if (result.isSuccess()) {
                    showMessage("âœ… " + result.getMessage(), false);

                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            Platform.runLater(() -> goBackToLogin());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    showMessage("âŒ " + result.getMessage(), true);
                }
            });
        }).start();
    }

    private VBox createInputField(String label) {
        VBox box = new VBox(5);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        TextField field = new TextField();
        field.setStyle(
                "-fx-font-size: 14px; -fx-padding: 12px; " +
                        "-fx-background-color: #F8F9FA; -fx-border-color: #DDD; " +
                        "-fx-border-width: 1px; -fx-border-radius: 8px; -fx-background-radius: 8px;"
        );
        field.setPrefWidth(350);
        box.getChildren().addAll(lbl, field);
        return box;
    }

    private VBox createPasswordFields() {
        VBox box = new VBox(15);

        VBox passwordBox = new VBox(5);
        Label lblPassword = new Label("Password");
        lblPassword.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");

        pfPassword = new PasswordField();
        pfPassword.setPromptText("At least 6 characters");
        pfPassword.setStyle(
                "-fx-font-size: 14px; -fx-padding: 12px; " +
                        "-fx-background-color: #F8F9FA; -fx-border-color: #DDD; " +
                        "-fx-border-width: 1px; -fx-border-radius: 8px; -fx-background-radius: 8px;"
        );
        pfPassword.setPrefWidth(350);

        tfPasswordVisible = new TextField();
        tfPasswordVisible.setStyle(pfPassword.getStyle());
        tfPasswordVisible.setPrefWidth(350);
        tfPasswordVisible.setVisible(false);
        tfPasswordVisible.setManaged(false);
        pfPassword.textProperty().bindBidirectional(tfPasswordVisible.textProperty());

        StackPane passwordStack = new StackPane(pfPassword, tfPasswordVisible);
        passwordStack.setAlignment(Pos.CENTER_LEFT);
        passwordBox.getChildren().addAll(lblPassword, passwordStack);

        VBox confirmBox = new VBox(5);
        Label lblConfirm = new Label("Confirm Password");
        lblConfirm.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");

        pfConfirmPassword = new PasswordField();
        pfConfirmPassword.setPromptText("Re-enter your password");
        pfConfirmPassword.setStyle(pfPassword.getStyle());
        pfConfirmPassword.setPrefWidth(350);

        tfConfirmPasswordVisible = new TextField();
        tfConfirmPasswordVisible.setStyle(pfPassword.getStyle());
        tfConfirmPasswordVisible.setPrefWidth(350);
        tfConfirmPasswordVisible.setVisible(false);
        tfConfirmPasswordVisible.setManaged(false);
        pfConfirmPassword.textProperty().bindBidirectional(tfConfirmPasswordVisible.textProperty());

        StackPane confirmStack = new StackPane(pfConfirmPassword, tfConfirmPasswordVisible);
        confirmStack.setAlignment(Pos.CENTER_LEFT);
        confirmBox.getChildren().addAll(lblConfirm, confirmStack);

        cbShowPassword = new CheckBox("Show passwords");
        cbShowPassword.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        cbShowPassword.setOnAction(e -> togglePasswordVisibility());

        box.getChildren().addAll(passwordBox, confirmBox, cbShowPassword);
        return box;
    }

    private void togglePasswordVisibility() {
        boolean show = cbShowPassword.isSelected();
        pfPassword.setVisible(!show);
        pfPassword.setManaged(!show);
        tfPasswordVisible.setVisible(show);
        tfPasswordVisible.setManaged(show);
        pfConfirmPassword.setVisible(!show);
        pfConfirmPassword.setManaged(!show);
        tfConfirmPasswordVisible.setVisible(show);
        tfConfirmPasswordVisible.setManaged(show);
    }

    private void showMessage(String message, boolean isError) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: " + (isError ? "#FF3B30" : "#34C759") + "; -fx-font-size: 13px; -fx-wrap-text: true;");
        lblMessage.setVisible(true);
    }
}