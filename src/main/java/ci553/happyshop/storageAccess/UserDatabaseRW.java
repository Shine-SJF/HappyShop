package ci553.happyshop.storageAccess;

import ci553.happyshop.auth.User;
import ci553.happyshop.auth.UserRole;
import ci553.happyshop.auth.PasswordHasher;

import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UserDatabaseRW {

    private static final String dbURL = DatabaseRWFactory.dbURL;
    private static final Lock lock = new ReentrantLock();

    public User findUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM UserTable WHERE username = ?";

        lock.lock();
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String passwordHash = rs.getString("passwordHash");
                    String email = rs.getString("email");
                    String fullName = rs.getString("fullName");
                    String roleStr = rs.getString("role");
                    boolean isActive = rs.getBoolean("isActive");

                    if (!isActive) {
                        System.out.println("Account inactive: " + username);
                        return null;
                    }

                    UserRole role = UserRole.valueOf(roleStr.toUpperCase());
                    return new User(username, passwordHash, email, fullName, role);
                }
            }
        } finally {
            lock.unlock();
        }
        return null;
    }

    public User authenticateUser(String username, String password) throws SQLException {
        User user = findUserByUsername(username);

        if (user != null && PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
            updateLastLogin(username);
            System.out.println("Login successful: " + username);
            return user;
        }

        System.out.println("Login failed: " + username);
        return null;
    }

    public boolean registerUser(String username, String password, String email, String fullName, UserRole role) throws SQLException {
        if (usernameExists(username)) {
            System.out.println("Username exists: " + username);
            return false;
        }

        String passwordHash = PasswordHasher.hashPassword(password);
        String insertSQL = "INSERT INTO UserTable (username, passwordHash, email, fullName, role, isActive) VALUES (?, ?, ?, ?, ?, ?)";

        lock.lock();
        try (Connection conn = DriverManager.getConnection(dbURL)) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                stmt.setString(1, username);
                stmt.setString(2, passwordHash);
                stmt.setString(3, email);
                stmt.setString(4, fullName);
                stmt.setString(5, role.name());
                stmt.setBoolean(6, true);

                int rows = stmt.executeUpdate();
                conn.commit(); // COMMIT TRANSACTION

                System.out.println(rows > 0 ? "Registered: " + username : "Registration failed");
                return rows > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM UserTable WHERE username = ?";

        lock.lock();
        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } finally {
            lock.unlock();
        }
    }

    private void updateLastLogin(String username) {
        lock.lock();
        try (Connection conn = DriverManager.getConnection(dbURL)) {
            conn.setAutoCommit(false);

            String sql = "UPDATE UserTable SET lastLogin = CURRENT_TIMESTAMP WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Last login update failed: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Connection error updating last login: " + username);
        } finally {
            lock.unlock();
        }
    }


    public static void initializeUserTable() throws SQLException {
        lock.lock();

        String createTableSQL = "CREATE TABLE UserTable(" +
                "userId INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "passwordHash VARCHAR(255) NOT NULL," +
                "email VARCHAR(100)," +
                "fullName VARCHAR(100)," +
                "role VARCHAR(20) NOT NULL," +
                "isActive BOOLEAN DEFAULT true," +
                "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "lastLogin TIMESTAMP" +
                ")";

        try (Connection conn = DriverManager.getConnection(dbURL)) {
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTableSQL);
                System.out.println("  UserTable created");

                String insertSQL = "INSERT INTO UserTable (username, passwordHash, email, fullName, role, isActive) VALUES (?, ?, ?, ?, ?, true)";

                try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                    // Admin
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, PasswordHasher.hashPassword("admin123"));
                    pstmt.setString(3, "admin@happyshop.com");
                    pstmt.setString(4, "System Administrator");
                    pstmt.setString(5, "ADMIN");
                    pstmt.executeUpdate();

                    // Staff
                    pstmt.setString(1, "staff");
                    pstmt.setString(2, PasswordHasher.hashPassword("staff123"));
                    pstmt.setString(3, "staff@happyshop.com");
                    pstmt.setString(4, "Warehouse Staff");
                    pstmt.setString(5, "STAFF");
                    pstmt.executeUpdate();

                    // Warehouse1
                    pstmt.setString(1, "warehouse1");
                    pstmt.setString(2, PasswordHasher.hashPassword("warehouse123"));
                    pstmt.setString(3, "warehouse1@happyshop.com");
                    pstmt.setString(4, "John Warehouse");
                    pstmt.setString(5, "STAFF");
                    pstmt.executeUpdate();

                    // Picker
                    pstmt.setString(1, "picker");
                    pstmt.setString(2, PasswordHasher.hashPassword("picker123"));
                    pstmt.setString(3, "picker@happyshop.com");
                    pstmt.setString(4, "Order Picker");
                    pstmt.setString(5, "PICKER");
                    pstmt.executeUpdate();

                    // Customer
                    pstmt.setString(1, "customer");
                    pstmt.setString(2, PasswordHasher.hashPassword("customer123"));
                    pstmt.setString(3, "customer@example.com");
                    pstmt.setString(4, "Test Customer");
                    pstmt.setString(5, "CUSTOMER");
                    pstmt.executeUpdate();
                }

                conn.commit(); // COMMIT ALL CHANGES
                System.out.println("  Default accounts created");

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } finally {
            lock.unlock();
        }
    }
}