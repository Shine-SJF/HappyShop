package ci553.happyshop.auth;

import ci553.happyshop.storageAccess.UserDatabaseRW;
import java.sql.SQLException;

/**
 * AuthenticationManager - Central authentication system
 * Single pattern for managing user authentication
 */
public class AuthenticationManager {

    private static AuthenticationManager instance;
    private UserDatabaseRW userDatabase;
    private User currentUser;

    private AuthenticationManager() {
        this.userDatabase = new UserDatabaseRW();
    }

    public static AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }


      //a user with username and password

    public User login(String username, String password) {
        try {
            User user = userDatabase.authenticateUser(username, password);
            if (user != null) {
                this.currentUser = user;
                System.out.println("Login successful: " + username);
            } else {
                System.out.println("Login failed: Invalid credentials for " + username);
            }
            return user;
        } catch (SQLException e) {
            System.err.println("===========================================");
            System.err.println("DATABASE ERROR DURING LOGIN");
            System.err.println("===========================================");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println();

            // Provide helpful hints based on error
            if (e.getSQLState() != null) {
                if (e.getSQLState().equals("XJ004")) {
                    System.err.println("CAUSE: Database not found!");
                    System.err.println("SOLUTION: Run SetDatabaseWithAuth.java to create the database");
                } else if (e.getSQLState().equals("42X05")) {
                    System.err.println("CAUSE: UserTable does not exist!");
                    System.err.println("SOLUTION: Run SetDatabaseWithAuth.java to initialize tables");
                } else if (e.getSQLState().equals("42Y07")) {
                    System.err.println("CAUSE: UserTable schema mismatch!");
                    System.err.println("SOLUTION: Run SetDatabaseWithAuth.java to recreate tables");
                } else {
                    System.err.println("SOLUTION: Check console output or run DatabaseDiagnostic.java");
                }
            }

            System.err.println("===========================================");
            e.printStackTrace();
            return null;
        }
    }


     //Register a new customer or creating new customer account

    public RegistrationResult registerCustomer(String username, String password, String email, String fullName) {
        try {
            // Validate input
            if (username == null || username.trim().isEmpty()) {
                return new RegistrationResult(false, "Username cannot be empty");
            }
            if (username.length() < 3) {
                return new RegistrationResult(false, "Username must be at least 3 characters");
            }
            if (password == null || password.length() < 6) {
                return new RegistrationResult(false, "Password must be at least 6 characters");
            }
            if (email == null || !email.contains("@")) {
                return new RegistrationResult(false, "Invalid email address");
            }

            // Check if username exists
            if (userDatabase.usernameExists(username)) {
                return new RegistrationResult(false, "Username already exists");
            }

            // Register user
            boolean success = userDatabase.registerUser(username, password, email, fullName, UserRole.CUSTOMER);

            if (success) {
                return new RegistrationResult(true, "Account created successfully! Please login.");
            } else {
                return new RegistrationResult(false, "Registration failed. Please try again.");
            }

        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return new RegistrationResult(false, "Database error: " + e.getMessage());
        }
    }

    //Get currently logged in user

    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Registration result inner class
     */
    public static class RegistrationResult {
        private final boolean success;
        private final String message;

        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}