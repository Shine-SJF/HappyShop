package ci553.happyshop.client.login;

import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRegister { ;
    private final String filePath =  "src/main/resources/user.txt";

    private void register() {}

    // add the user to file
    public void createUser(TextField username, TextField password, String userType) throws IOException {
        FileWriter fw = new FileWriter(filePath, true);
        fw.write(username.getText() + "," + password.getText() + "," + userType + System.lineSeparator());
        fw.close();
    }

    public void deleteUser(TextField username) throws IOException {
    }
}
