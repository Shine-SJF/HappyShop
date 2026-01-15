package ci553.happyshop.client.login;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

// Create Login Page GUI
public class LoginView {
    public LoginController loginController;
    private String userType;

    public void startLogin(Stage loginWindow) {
        loginWindow.setTitle("Login Page");
        HBox root = new HBox();

        VBox leftLayout = new VBox();
        leftLayout.setId("leftLayout");

        Text title = new Text("Happy Shop");
        title.setId("title");

        // create circular white border around the logo
        double circleRadius = 70;
        Circle logoCircle = new Circle(circleRadius, circleRadius, circleRadius);

        Image logo = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("shop_logo.png")));
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(circleRadius * 2 * 0.9);
        logoView.setFitHeight(circleRadius * 2 * 0.9);
        logoView.setPreserveRatio(true);
        logoView.setClip(logoCircle);
        logoCircle.setFill(new ImagePattern(logo));

        Circle border = new Circle(50, 50, 50);
        border.setStroke(javafx.scene.paint.Color.BLACK);
        border.setStrokeWidth(2);
        border.setFill(Color.WHITE);

        StackPane circularLogo = new StackPane();
        circularLogo.getChildren().addAll(border, logoView);

        leftLayout.getChildren().addAll(title, circularLogo);
        leftLayout.setAlignment(Pos.CENTER);

        // create hboxes for horizontal lining of button + image
        HBox customerRow = new HBox(20);

        Button customerButton = new Button("Customer Login");
        customerButton.setOnAction((event) -> {
            userLoginPage();
        });

        // user icon
        Image userIcon = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("user_icon.png")));
        ImageView userIconView = new ImageView(userIcon);
        userIconView.setFitWidth(50);
        userIconView.setFitHeight(50);
        userIconView.setPreserveRatio(true);

        customerRow.getChildren().addAll(userIconView, customerButton);
        customerRow.setAlignment(Pos.TOP_CENTER);

        HBox warehouseRow = new HBox(20);

        Button warehouseButton = new Button("Warehouse Login");
        warehouseButton.setOnAction((event) -> {
            loginController.openWarehouseClient();
        });
        // warehouse Icon
        Image warehouseIcon = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("warehouse_icon.png")));
        ImageView warehouseIconView = new ImageView(warehouseIcon);
        warehouseIconView.setFitWidth(50);
        warehouseIconView.setFitHeight(50);
        warehouseIconView.setPreserveRatio(true);

        warehouseRow.getChildren().addAll(warehouseIconView, warehouseButton);
        warehouseRow.setAlignment(Pos.CENTER);

        HBox managerRow = new HBox(20);
        Button managerButton = new Button("Manager Login");
        managerRow.getChildren().add(managerButton);
        managerRow.setAlignment(Pos.BOTTOM_CENTER);

        VBox rightLayout = new VBox(10);
        rightLayout.getChildren().addAll(customerRow, warehouseRow, managerRow);
        rightLayout.setAlignment(Pos.CENTER);

        HBox.setHgrow(leftLayout, Priority.ALWAYS);
        HBox.setHgrow(rightLayout, Priority.ALWAYS);
        root.getChildren().addAll(leftLayout, rightLayout);

        Scene scene = new Scene(root, 500, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("style/login.css")).toExternalForm());
        loginWindow.setScene(scene);
        loginWindow.show();
    }

    public void userLoginPage() {
        Stage userloginWindow = new Stage();
        userloginWindow.setTitle("Customer Login");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username: ");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password: ");

        userType = "customer";

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction((event) -> {
            try {
                loginController.createUser(usernameField,passwordField, userType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button loginButton = new Button("Login");
        loginButton.setOnAction((event) -> {
            try {
                loginController.userLogin(usernameField,passwordField);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction((event) -> {
            try{
                startLogin(userloginWindow);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });

        VBox loginBox = new VBox();
        loginBox.setAlignment(Pos.CENTER);
        loginBox.getChildren().addAll(usernameField, passwordField, loginButton, signUpButton, cancelButton);

        Scene scene = new Scene(loginBox, 500, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("style/login.css")).toExternalForm());
        userloginWindow.setScene(scene);
        userloginWindow.show();
    }
}
