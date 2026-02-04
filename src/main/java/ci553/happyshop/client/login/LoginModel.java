package ci553.happyshop.client.login;

import javafx.scene.control.TextField;

import java.io.*;

public class LoginModel {

    public boolean userLogin(TextField username, TextField password) {
        String userType = "customer";
        return createFileReader(username, password, userType)  || createFileReader(username, password, "employee");
    }

    public boolean warehouseLogin(TextField username, TextField password) {
        String userType = "employee";
        return createFileReader(username, password,  "employee");
    }

    private boolean createFileReader(TextField username, TextField password, String userType) {
        InputStream is = getClass().getClassLoader().getResourceAsStream("user.txt");
        System.out.println(getClass().getClassLoader().getResource("user.txt"));
        try{
            String line;
            // create a reader for lines
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    String fileUsername = values[0];
                    String filePassword = values[1];
                    String fileUserType = values[2];

                    if (fileUsername.equals(username.getText().trim()) &&
                            filePassword.equals(password.getText().trim()) &&
                            fileUserType.equalsIgnoreCase(userType)) {
                        System.out.println("Login successful for " + userType);
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }
}





