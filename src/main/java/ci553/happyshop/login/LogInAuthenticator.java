package ci553.happyshop.login;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *validates login details by reading from users.txt
 * it also generated the hash again based off of user input and checks it to the stored hash in users.txt
 *also checks if the current role requires 2FA
 */

public class LogInAuthenticator {

    private static final String USERS_FILE = "users.txt";

    

    public boolean authenticate(String username, String password, String role, String enteredCode) {
        System.out.println("Authenticating user for role: " + role);

        if (username == null || username.isBlank() || password == null || password.isBlank() || role == null || role.isBlank()) {
            return false;
        }
       

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 4) continue; //username,hashedPassword,role,salt

                String fileUsername = parts[0].trim();
                String fileHashedPassword = parts[1].trim();
                String fileRole = parts[2].trim();
                String fileSalt = parts[3].trim();

                if (!fileUsername.equals(username)) continue;
                if (!fileRole.equalsIgnoreCase(role)) continue;

                String computed = Hash.hashPassword(password + fileSalt);

                System.out.println("stored hash:   " + fileHashedPassword);
                System.out.println("computed hash: " + computed);


                if (fileHashedPassword.equals(computed)) {
                    System.out.println("Authentication successful for " + username + " as " + role);
                    return true;
                }
            }

        } catch (IOException e) {
            System.out.println("File error during authentication: " + e.getMessage());
        }

        System.out.println("Authentication failed for " + username + " as " + role);
        return false;
    }


    public boolean requires2FA(String role) {
        if (role == null) return false;

        switch (role.toLowerCase()) {
            case "admin":
            case "picker":
            case "warehouse":
                return true;
            case "customer":
                return false;
            default:
                System.out.println("role not found: " + role);
                return false;
        }
    }
}
