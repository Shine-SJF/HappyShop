package ci553.happyshop.client.auth;

import ci553.happyshop.auth.AuthenticationManager;
import ci553.happyshop.auth.User;

    //the loginModel serves as a wrapper aroound the AuthenticaationManager providing two key function

public class LoginModel {
    public LoginView loginView;
    private AuthenticationManager authManager;

    public LoginModel() {
        this.authManager = AuthenticationManager.getInstance();
    }

    public User authenticateUser(String username, String password) {
        return authManager.login(username, password);
    }

    public AuthenticationManager.RegistrationResult registerCustomer(String username, String password, String email, String fullName) {
        return authManager.registerCustomer(username, password, email, fullName);
    }

}