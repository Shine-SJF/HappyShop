package ci553.happyshop.login;

import ci553.happyshop.utility.UIStyle;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import javafx.scene.layout.BorderPane;


/**
 *JavaFX based UI for role selection, logging in, and 2FA
 *checks for entered details are made by LogInAuthenticator
 *verifies 2FA codes using TwoFA class, and uses CurrentUser.
 *Once full login succeeds, it notifies the main using a callback method
 */



public class LoadLogin extends Application {
    private final LogInAuthenticator authenticator = new LogInAuthenticator();
    private String enteredCode;
    private LoginCallback loginCallback;
    private Scene twoFAScene;
    private final CurrentUser currentUser = CurrentUser.getInstance();
    private final TwoFA twoFAService = new TwoFA();

    @Override
    public void start(Stage primaryStage) { //abstract method
        startLogin(primaryStage);
    }

    public void setLoginCallback(LoginCallback loginCallback) {
    this.loginCallback = loginCallback;
    }

    public interface LoginCallback { //interface meaning method must be implemented
    void onLoginSuccess(String username, String role, boolean isAuthenticated, boolean requires2FA, Stage twoFALayout, String code);; //calls main after success
    void onLoginFailure();}

    public String getEnteredCode() {
        return enteredCode;
    }

    public Scene getTwoFALayout() {
        return twoFAScene;
    }

    public static Stage getPrimaryStage() {
        return new Stage();
    }

    public VBox startLogin(Stage primaryStage) {
        System.out.println("loading role selection screen");
        Label selectRoleLabel = new Label("Select Login Role");
        selectRoleLabel.setStyle(UIStyle.labelTitleStyle);


        Button customerButton = new Button("Customer");
        customerButton.setStyle(UIStyle.buttonStyle);
        customerButton.setOnAction(event -> {
            System.out.println("customer role selected.");
            showLoginScreen(primaryStage, "Customer");
        });

        Button PickerButton = new Button("Picker");
        PickerButton.setStyle(UIStyle.buttonStyle);
        PickerButton.setOnAction(event -> {
            System.out.println("Picker role selected.");
            showLoginScreen(primaryStage, "Picker");
        });

        Button warehouseButton = new Button("Warehouse");
        warehouseButton.setStyle(UIStyle.buttonStyle);
        warehouseButton.setOnAction(event -> {
        System.out.println("warehouse role selected.");
        showLoginScreen(primaryStage, "Warehouse");
        });


        Button adminButton = new Button("Admin");
        adminButton.setStyle(UIStyle.buttonStyle);
        adminButton.setOnAction(event -> {
            System.out.println("admin role selected.");
            showLoginScreen(primaryStage, "Admin");
        });

        VBox roleSelectionLayout = new VBox(15, selectRoleLabel, customerButton, PickerButton, warehouseButton, adminButton);
        roleSelectionLayout.setAlignment(Pos.CENTER);
        roleSelectionLayout.setStyle(UIStyle.rootStyleBlue); 
        selectRoleLabel.setStyle(UIStyle.labelTitleStyle);
        customerButton.setStyle(UIStyle.buttonStyle);
        PickerButton.setStyle(UIStyle.buttonStyle);
        adminButton.setStyle(UIStyle.buttonStyle);
        warehouseButton.setStyle(UIStyle.buttonStyle);

        Scene roleSelectionScene = new Scene(roleSelectionLayout, UIStyle.customerWinWidth, UIStyle.customerWinHeight * 1.5);
        primaryStage.setScene(roleSelectionScene);
        primaryStage.setTitle("Select Role");
        primaryStage.show();
        System.out.println("role selection screen displayed.");

        return roleSelectionLayout;
        }

    private VBox showLoginScreen(Stage primaryStage, String role) {
        System.out.println("displaying login screen for role: " + role);

        // Back arrow button
        Polygon backArrow = new Polygon();
        backArrow.getPoints().addAll(
                0.0, 10.0,
                20.0, 0.0,
                20.0, 20.0
        );
        backArrow.setFill(Color.BLACK);

        Button backButton = new Button("", backArrow);
        backButton.setStyle("-fx-background-color: transparent;");
        backButton.setOnAction(event -> {
            System.out.println("returning to role selection screen.");
            startLogin(primaryStage);
        });

        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setStyle("-fx-padding: 10px;");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle(UIStyle.rootStyleForRole(role));
        mainLayout.setTop(topBar);

        Label titleLabel = new Label("Login as " + role);
        titleLabel.setStyle(UIStyle.labelTitleStyle);
        
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle(UIStyle.labelStyle);
        TextField usernameText = new TextField();
        usernameText.setPromptText("Enter your username");
        usernameText.setStyle(UIStyle.textFiledStyle);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle(UIStyle.labelStyle);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle(UIStyle.textFiledStyle);
        TextField passwordTextField = new TextField();
        passwordTextField.setPromptText("Enter your password");
        passwordTextField.setStyle(UIStyle.textFiledStyle);
        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!passwordTextField.getText().equals(newValue)) {
                passwordTextField.setText(newValue);
            }
        });
        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!passwordField.getText().equals(newValue)) {
                passwordField.setText(newValue);
            }
        });
        CheckBox showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.setStyle(UIStyle.labelStyle);
        showPasswordCheckBox.setOnAction(event -> {
        boolean show = showPasswordCheckBox.isSelected();
        passwordField.setManaged(!show);
        passwordField.setVisible(!show);
        passwordTextField.setManaged(show);
        passwordTextField.setVisible(show);
        });

        Button loginButton = new Button("Login");
        loginButton.setStyle(UIStyle.buttonStyle);
        loginButton.setOnAction(event -> {
        String enteredUsername = usernameText.getText();
        String password = showPasswordCheckBox.isSelected()
                ? passwordTextField.getText()
                : passwordField.getText();

        if (enteredUsername == null || enteredUsername.isBlank()) {
            System.out.println("No username entered.");
            if (loginCallback != null) loginCallback.onLoginFailure();
            return;
        }

        boolean passwordOk = authenticator.authenticate(enteredUsername, password, role, null);
        if (!passwordOk) {
            System.out.println("login failed for role: " + role);
            if (loginCallback != null) loginCallback.onLoginFailure();
            return;
        }

        boolean needs2FA = authenticator.requires2FA(role);

        UserRoles roleEnum;
        switch (role.toLowerCase()) { //takes string rto enum
            case "admin" -> roleEnum = UserRoles.ADMIN;
            case "warehouse" -> roleEnum = UserRoles.WAREHOUSE;
            case "picker" -> roleEnum = UserRoles.PICKER;
            case "customer" -> roleEnum = UserRoles.CUSTOMER;
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        }

        UserAccount account = new UserAccount(enteredUsername, roleEnum, needs2FA, null);
        currentUser.startSession(account);

        if (!needs2FA) {
            System.out.println("login successful (no 2FA required)");
            if (loginCallback != null) {loginCallback.onLoginSuccess(account.getUsername(),account.getRole().toString(),true,false,primaryStage,null);}


            primaryStage.close();
            return;
            }

            System.out.println("login successful (2FA required)");

            String code = twoFAService.generateAndWrite(account.getUsername(), account.getRole().toString());

            Stage twoFAStage = new Stage();
            startTwoFA(twoFAStage, account.getUsername(), account.getRole().toString(), code);
            twoFAStage.show();

            primaryStage.close();

        });

        VBox loginLayout = new VBox(15,titleLabel, usernameLabel, usernameText, passwordLabel, passwordField, passwordTextField, showPasswordCheckBox, loginButton);

        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setStyle("-fx-padding: 20px;");

        mainLayout.setCenter(loginLayout);

        Scene loginScene = new Scene(mainLayout, UIStyle.customerWinWidth, UIStyle.customerWinHeight * 1.5);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("login - " + role);

        return loginLayout;
    }


    public Scene startTwoFA(Stage stage, String username, String role, String code) {
    
        // Back button
        Polygon backArrow = new Polygon();
        backArrow.getPoints().addAll(
                0.0, 10.0,
                20.0, 0.0,
                20.0, 20.0
        );
        backArrow.setFill(Color.BLACK);

        Button backButton = new Button("", backArrow);
        backButton.setStyle("-fx-background-color: transparent;");
        backButton.setOnAction(e -> {
            System.out.println("Returning to role selection screen.");
            startLogin(stage);
        });

        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setStyle("-fx-padding: 10px; -fx-background-color: #34495E;");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle(UIStyle.rootStyleForRole(role));
        mainLayout.setTop(topBar);

        Label titleLabel = new Label("Two-factor authentication required!");
        titleLabel.setStyle(UIStyle.labelTitleStyle);
        Label usernameLabel = new Label("Username: " + username);
        usernameLabel.setStyle(UIStyle.labelStyle);
        Label roleLabel = new Label("Role: " + role);
        roleLabel.setStyle(UIStyle.labelStyle);
        Label twoFALabel = new Label("Enter 2FA Code:");
        twoFALabel.setStyle(UIStyle.labelStyle);

        TextField twoFAPasswordField = new TextField();
        twoFAPasswordField.setPromptText("Enter your 2FA code");
        twoFAPasswordField.setStyle(UIStyle.textFiledStyle);

        Button submitButton = new Button("Submit");
        submitButton.setStyle(UIStyle.buttonStyle);
        submitButton.setOnAction(e -> {
        enteredCode = twoFAPasswordField.getText();
        System.out.println("2FA code entered: " + enteredCode);

        boolean ok = twoFAService.verify(enteredCode);
        if (ok) {
            System.out.println("2FA verification successful!");
            currentUser.verifyTwoFactor();

            if (loginCallback != null) {
                loginCallback.onLoginSuccess(username, role, true, false, stage, enteredCode);
            }
            stage.close();
        } else {
            System.out.println("2FA verification failed!"); //bookmark stop from entering characters
            if (loginCallback != null) {
                loginCallback.onLoginSuccess(username, role, false, true, stage, enteredCode);
            }
        }
        });

        VBox twoFALayout = new VBox(15, titleLabel, usernameLabel, roleLabel, twoFALabel, twoFAPasswordField, submitButton);
        twoFALayout.setAlignment(Pos.CENTER);
        twoFALayout.setStyle("-fx-padding: 20px;");

        mainLayout.setCenter(twoFALayout);

        twoFAScene = new Scene(mainLayout, UIStyle.customerWinWidth, UIStyle.customerWinHeight * 1.5);
        stage.setScene(twoFAScene);
        stage.setTitle("Two-factor authentication required!");

        return twoFAScene;
    }

    public static void main(String[] args) {
        System.out.println("launching application");
        launch(args);
    }
}





