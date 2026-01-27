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
 * The LoadLogin class displays the role selection window and the login window.
 *
 * It provides a UI for selecting a role (Customer, Picker, Warehouse, Admin)
 */

public class LoadLogin extends Application {

    @Override
    public void start(Stage primaryStage) { //abstract method
        startLogin(primaryStage);
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
        roleSelectionLayout.setStyle(UIStyle.rootStyleBlue); //uses styles from UIStyle
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

        return roleSelectionLayout;}
    
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
        mainLayout.setTop(topBar);

        Label titleLabel = new Label("Login");
        titleLabel.setStyle(UIStyle.labelTitleStyle);
        
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle(UIStyle.labelStyle);
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle(UIStyle.labelStyle);

        TextField usernameText = new TextField();
        usernameText.setPromptText("Enter your username");
        usernameText.setStyle(UIStyle.textFiledStyle);
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
            System.out.println("login button clicked");
            primaryStage.close();
        });

        VBox loginLayout = new VBox(15, titleLabel, usernameLabel, usernameText, passwordLabel, passwordField, passwordTextField, showPasswordCheckBox, loginButton);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setStyle("-fx-padding: 20px;");

        mainLayout.setCenter(loginLayout);

        Scene loginScene = new Scene(mainLayout, UIStyle.customerWinWidth, UIStyle.customerWinHeight * 1.5);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login");

        return loginLayout;
    }

    public static void main(String[] args) {
        System.out.println("launching application");
        launch(args);
    }

}





