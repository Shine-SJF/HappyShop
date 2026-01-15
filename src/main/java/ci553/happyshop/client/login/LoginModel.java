package ci553.happyshop.client.login;

import javafx.scene.control.TextField;

import java.io.*;

public class LoginModel {

    public boolean userLogin(TextField username, TextField password) {
        InputStream is = getClass().getClassLoader().getResourceAsStream("user.txt");
        System.out.println(getClass().getClassLoader().getResource("user.txt"));
        try{
            String line;
            // create a reader for the lines
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    // create strings for username & password to check if it exists
                    // this way stops partial strings causing a false positive
                    String fileUsername = values[0];
                    String filePassword = values[1];

                    if (fileUsername.equals(username.getText()) && filePassword.equals(password.getText())) {
                        System.out.println("Login successful");
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            // LoginView - Tell user it was not found
            return false;
        }
        return false;
    }
}





