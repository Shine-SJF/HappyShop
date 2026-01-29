package ci553.happyshop.login;

/**
 *authenticates user by running every form of authentification in 1 condensed place
 *checks if user requires 2FA and if user is authenticated
 */

public class AuthChecker {

    private final LogInAuthenticator authenticator = new LogInAuthenticator();

    public AuthResult attemptLogin(String username, String password, UserRoles role) { //if success, return AuthResult with account info
        boolean ok = authenticator.authenticate(username, password, role.toString(), null);
        if (!ok) return new AuthResult(false, false, null);

        boolean needs2fa = authenticator.requires2FA(role.toString());

        UserAccount account = new UserAccount(username, role, needs2fa, null); //creates account object


        return new AuthResult(true, needs2fa, account);
    }
}
