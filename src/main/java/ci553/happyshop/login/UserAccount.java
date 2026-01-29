package ci553.happyshop.login;

/**
 *stores and makes accessible, the users account details, private and final to ensure security
 */

public class UserAccount{
    private final String username;
    private final UserRoles role;
    private final boolean twoFactorEnabled;
    private final String hashedPassword;

    public UserAccount(String username, UserRoles role, boolean twoFactorEnabled, String hashedPassword){
        this.username = username;
        this.role = role;
        this.twoFactorEnabled = twoFactorEnabled;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername(){
        return username;
    }
    public UserRoles getRole(){
        return role;
    }
    public boolean isTwoFactorEnabled(){
        return twoFactorEnabled;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
