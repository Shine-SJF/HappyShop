package ci553.happyshop.login;

/**
 *provides outcome of last login attempt
 *stores the created UserAccount, if password check succeeded and if role requires 2FA o rnot
 */

public class AuthResult {
    public final boolean passwordOk;
    public final boolean requires2FA;
    public final UserAccount account; //null if fail

    public AuthResult(boolean passwordOk, boolean requires2FA, UserAccount account) {
        this.passwordOk = passwordOk;
        this.requires2FA = requires2FA;
        this.account = account;
    }
}