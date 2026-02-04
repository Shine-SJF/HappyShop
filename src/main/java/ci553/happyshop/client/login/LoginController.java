package ci553.happyshop.client.login;

import ci553.happyshop.client.MainController;
import ci553.happyshop.client.customer.CustomerView;
import ci553.happyshop.client.warehouse.WarehouseView;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {
    public LoginModel loginModel;
    public LoginView loginView;
    public UserRegister register = new UserRegister();
    public MainController mainController;

    public void openCustomerClient() {
        mainController.doCustomerClient();
    };

    public void openWarehouseClient(){
        mainController.doWarehouseClient();
    }

    public void createUser(TextField username, TextField password, String userType) throws IOException {
        register.createUser(username, password, userType);
    }

    public void userLogin(TextField username, TextField password) throws IOException {
        if (loginModel.userLogin(username, password)) {
                openCustomerClient();
        }
    }

    public void warehouseLogin(TextField username, TextField password) throws IOException {
        if (loginModel.warehouseLogin(username, password)) {
            openWarehouseClient();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Incorrect Credentials");
            alert.setHeaderText("Incorrect Credentials");
            alert.showAndWait();
        }
    }
}
