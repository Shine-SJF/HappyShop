package ci553.happyshop.client.auth;

import ci553.happyshop.auth.User;

    //the maain responsiblity where it provdies a login method that take usernaame and password, then delegatess the actuall authentication work to a loginModel object
public class LoginController {
    public LoginModel loginModel;

    public User login(String username, String password) {
        return loginModel.authenticateUser(username, password);
    }
}