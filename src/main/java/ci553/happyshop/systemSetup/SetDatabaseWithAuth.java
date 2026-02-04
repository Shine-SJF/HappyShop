package ci553.happyshop.systemSetup;

import ci553.happyshop.storageAccess.DatabaseRWFactory;
import ci553.happyshop.storageAccess.UserDatabaseRW;

import java.io.File;
import java.sql.*;

    // the setdatabasewiithauth is a database initialisation utility that sets up the happyshop database from scratch

public class SetDatabaseWithAuth {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Database Setup");
        System.out.println("========================================");
        System.out.println();

        // Check if database folder exists
        File dbFolder = new File("happyShopDB");
        if (dbFolder.exists()) {
            System.out.println("WARNING: Database folder exists!");
            System.out.println("Please delete 'happyShopDB' folder first.");
            System.out.println("========================================");
            return;
        }

        try {
            // Create fresh database
            createDatabase();

            System.out.println();
            System.out.println("========================================");
            System.out.println("SUCCESS! Database created!");
            System.out.println("========================================");
            System.out.println();
            System.out.println("Login Accounts:");
            System.out.println("  customer / customer123");
            System.out.println("  admin / admin123");
            System.out.println("  staff / staff123");
            System.out.println("  warehouse1 / warehouse123");
            System.out.println("  picker / picker123");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            shutdownDerby();
        }
    }

    private static void createDatabase() throws SQLException {
        String dbURL = DatabaseRWFactory.dbURL;

        System.out.println("Creating database...");

        try (Connection conn = DriverManager.getConnection(dbURL)) {
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                // Create ProductTable
                stmt.executeUpdate(
                        "CREATE TABLE ProductTable(" +
                                "productID CHAR(4) PRIMARY KEY," +
                                "description VARCHAR(100)," +
                                "unitPrice DOUBLE," +
                                "image VARCHAR(100)," +
                                "inStock INT," +
                                "maxStock INT)"
                );
                System.out.println("  ProductTable created");

                // Insert products
                String[] products = {
                        "INSERT INTO ProductTable VALUES('0001', '40 inch TV', 269.00, '0001.jpg', 100, 100)",
                        "INSERT INTO ProductTable VALUES('0002', 'DAB Radio', 29.99, '0002.jpg', 50, 100)",
                        "INSERT INTO ProductTable VALUES('0003', 'Toaster', 19.99, '0003.jpg', 25, 100)",
                        "INSERT INTO ProductTable VALUES('0004', 'Watch', 29.99, '0004.jpg', 8, 100)",
                        "INSERT INTO ProductTable VALUES('0005', 'Digital Camera', 89.99, '0005.jpg', 5, 100)",
                        "INSERT INTO ProductTable VALUES('0006', 'MP3 player', 7.99, '0006.jpg', 100, 100)"
                };

                for (String sql : products) {
                    stmt.executeUpdate(sql);
                }
                System.out.println("  Products added");

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }

        // Create UserTable
        UserDatabaseRW.initializeUserTable();
        System.out.println("  UserTable created");
        System.out.println("  User accounts added");
    }

    private static void shutdownDerby() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if (e.getSQLState().equals("XJ015")) {
                System.out.println("Database shutdown complete");
            }
        }
    }
}