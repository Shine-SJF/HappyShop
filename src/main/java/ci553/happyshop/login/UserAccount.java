package ci553.happyshop.login;

public class UserAccount{ //will store user details
    private final String username;
    private final UserRoles role;
    

    public UserAccount(String username, UserRoles role){
        this.username = username;
        this.role = role;
    }

    public String getUsername(){
        return username;
    }
    public UserRoles getRole(){
        return role;
    }
   
}
