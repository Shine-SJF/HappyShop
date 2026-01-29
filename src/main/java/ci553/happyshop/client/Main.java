package ci553.happyshop.client;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;
import ci553.happyshop.login.DecideClient;
import ci553.happyshop.login.LoadLogin;
import ci553.happyshop.login.SetDefaultUsers;
import ci553.happyshop.login.LoadLogin.LoginCallback;


/**
 *The Main class initialises the orderHub map
 *creates default user accounts in users.txt
 *starts the login UI, after succesful login DecideClient is called to launch the correct clients based on the role
 */

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }    
@Override
public void start(Stage window) throws IOException { //starts login

    DecideClient launcher = new DecideClient();
    launcher.initializeOrderMap();
    System.out.println(SetDefaultUsers.initializeDefaultUsers());

    LoadLogin login = new LoadLogin();
    login.setLoginCallback(new LoginCallback() { //alows for other classes to return to main
        @Override
        public void onLoginSuccess(String username, String role, boolean isAuthenticated, boolean requires2FA, Stage stage, String code) {

            if (!isAuthenticated || requires2FA) {
                System.out.println("not authenticated, role: " + role);
                return;
            }
            System.out.println("login successful, user: " + username + ", role: " + role);
            launcher.launchForRole(role);
        }
        @Override
        public void onLoginFailure() {
            System.out.println("login failed.");
        }
    });

    login.start(new Stage());
    }
}



