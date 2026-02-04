package ci553.happyshop.systemSetup;

import ci553.happyshop.auth.PasswordHasher;
import ci553.happyshop.storageAccess.DatabaseRWFactory;
import ci553.happyshop.utility.StorageLocation;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SetDatabase {

    private static final String dbURL = DatabaseRWFactory.dbURL + ";create=true";
    private static Path imageWorkingFolderPath = StorageLocation.imageFolderPath;
    private static Path imageBackupFolderPath = StorageLocation.imageResetFolderPath;

    private String[] tables = {"ProductTable", "UserTable"};
    private static final Lock lock = new ReentrantLock();

    public static void main(String[] args) throws SQLException, IOException {
        SetDatabase setDB = new SetDatabase();
        setDB.clearTables();
        setDB.initializeTable();
        setDB.queryTableAfterInitilization();
        deleteFilesInFolder(imageWorkingFolderPath);
        copyFolderContents(imageBackupFolderPath, imageWorkingFolderPath);
    }

    private void clearTables() throws SQLException {
        lock.lock();
        try (Connection con = DriverManager.getConnection(dbURL);
             Statement statement = con.createStatement()) {
            System.out.println("Database happyShopDB is connected successfully!");
            for (String table : tables) {
                try {
                    statement.executeUpdate("DROP TABLE " + table.toUpperCase());
                    System.out.println("Dropped table: " + table);
                } catch (SQLException e) {
                    if ("42Y55".equals(e.getSQLState())) {
                        System.out.println("Table " + table + " does not exist. Skipping...");
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void initializeTable() throws SQLException {
        lock.lock();

        String[] iniTableSQL = {
                // Create ProductTable
                "CREATE TABLE ProductTable(" +
                        "productID CHAR(4) PRIMARY KEY," +
                        "description VARCHAR(100)," +
                        "unitPrice DOUBLE," +
                        "image VARCHAR(100)," +
                        "inStock INT," +
                        "maxStock INT," +
                        "CHECK (inStock >= 0)," +
                        "CHECK (maxStock >= 0)" +
                        ")",

                // Create UserTable
                "CREATE TABLE UserTable(" +
                        "username VARCHAR(50) PRIMARY KEY," +
                        "passwordHash VARCHAR(255) NOT NULL," +
                        "email VARCHAR(100)," +
                        "fullName VARCHAR(100)," +
                        "role VARCHAR(20) NOT NULL," +
                        "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "lastLogin TIMESTAMP," +
                        "isActive BOOLEAN DEFAULT TRUE" +
                        ")",

                // Insert products
                "INSERT INTO ProductTable VALUES('0001', '40 inch TV', 269.00, '0001.jpg', 100, 100)",
                "INSERT INTO ProductTable VALUES('0002', 'DAB Radio', 29.99, '0002.jpg', 50, 100)",
                "INSERT INTO ProductTable VALUES('0003', 'Toaster', 19.99, '0003.jpg', 25, 100)",
                "INSERT INTO ProductTable VALUES('0004', 'Watch', 29.99, '0004.jpg', 8, 100)",
                "INSERT INTO ProductTable VALUES('0005', 'Digital Camera', 89.99, '0005.jpg', 5, 100)",
                "INSERT INTO ProductTable VALUES('0006', 'MP3 player', 7.99, '0006.jpg', 100, 100)",
                "INSERT INTO ProductTable VALUES('0007', 'USB drive', 6.99, '0007.jpg', 75, 100)",
                "INSERT INTO ProductTable VALUES('0008', 'USB2 drive', 7.99, '0008.jpg', 30, 100)",
                "INSERT INTO ProductTable VALUES('0009', 'USB3 drive', 8.99, '0009.jpg', 10, 100)",
                "INSERT INTO ProductTable VALUES('0010', 'USB4 drive', 9.99, '0010.jpg', 3, 100)",
                "INSERT INTO ProductTable VALUES('0011', 'USB5 drive', 10.99, '0011.jpg', 0, 100)",
                "INSERT INTO ProductTable VALUES('0012', 'USB6 drive', 10.99, '0011.jpg', 90, 100)"
        };

        try (Connection connection = DriverManager.getConnection(dbURL)) {
            System.out.println("Database happyShopDB is created successfully!");
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement()) {
                // Create tables
                statement.executeUpdate(iniTableSQL[0]);
                statement.executeUpdate(iniTableSQL[1]);

                // Insert products
                for (int i = 2; i < iniTableSQL.length; i++) {
                    statement.addBatch(iniTableSQL[i]);
                }
                statement.executeBatch();

                // Insert users with hashed passwords
                insertUser(connection, "admin1234", "admin1234", "admin@happyshop.com", "Administrator", "ADMIN");
                insertUser(connection, "staff1234", "staff1234", "staff@happyshop.com", "Warehouse Staff", "STAFF");

                connection.commit();

                System.out.println("\n✅ Tables created successfully");
                System.out.println("✅ 12 products inserted");
                System.out.println("✅ 2 default accounts created\n");
                System.out.println("═══════════════════════════════════════");
                System.out.println("  Default Warehouse Accounts");
                System.out.println("═══════════════════════════════════════");
                System.out.println("👑 Admin:  admin1234 / admin1234");
                System.out.println("🧑‍💼 Staff:  staff1234 / staff1234");
                System.out.println("═══════════════════════════════════════\n");

            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Transaction rolled back due to an error!");
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
    }

    private void insertUser(Connection conn, String username, String password, String email, String fullName, String role) throws SQLException {
        String insertUserSQL = "INSERT INTO UserTable (username, passwordHash, email, fullName, role, isActive) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertUserSQL)) {
            String passwordHash = PasswordHasher.hashPassword(password);

            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.setString(3, email);
            stmt.setString(4, fullName);
            stmt.setString(5, role);
            stmt.setBoolean(6, true);

            stmt.executeUpdate();
        }
    }

    private void queryTableAfterInitilization() throws SQLException {
        lock.lock();

        System.out.println("═══════════════ Product Table ═══════════════");
        String sqlQuery = "SELECT * FROM ProductTable";
        String title = String.format("%-12s %-20s %-10s %-10s %-10s %s",
                "productID", "description", "unitPrice", "inStock", "maxStock", "image");
        System.out.println(title);

        try (Connection connection = DriverManager.getConnection(dbURL);
             Statement stat = connection.createStatement()) {
            ResultSet resultSet = stat.executeQuery(sqlQuery);
            while (resultSet.next()) {
                String productID = resultSet.getString("productID");
                String description = resultSet.getString("description");
                double unitPrice = resultSet.getDouble("unitPrice");
                String image = resultSet.getString("image");
                int inStock = resultSet.getInt("inStock");
                int maxStock = resultSet.getInt("maxStock");
                String record = String.format("%-12s %-20s %-10.2f %-10d %-10d %s",
                        productID, description, unitPrice, inStock, maxStock, image);
                System.out.println(record);
            }
        } finally {
            lock.unlock();
        }

        System.out.println("\n═══════════════ User Table ═══════════════");
        String userQuery = "SELECT username, email, fullName, role FROM UserTable";
        String userTitle = String.format("%-15s %-25s %-20s %s",
                "Username", "Email", "Full Name", "Role");
        System.out.println(userTitle);

        lock.lock();
        try (Connection connection = DriverManager.getConnection(dbURL);
             Statement stat = connection.createStatement()) {
            ResultSet resultSet = stat.executeQuery(userQuery);
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String fullName = resultSet.getString("fullName");
                String role = resultSet.getString("role");
                String record = String.format("%-15s %-25s %-20s %s",
                        username, email, fullName, role);
                System.out.println(record);
            }
        } finally {
            lock.unlock();
        }
    }

    public static void deleteFilesInFolder(Path folder) throws IOException {
        if (Files.exists(folder)) {
            lock.lock();
            try {
                Files.walkFileTree(folder, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Deleted files in folder: " + folder);
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("Folder " + folder + " does not exist");
        }
    }

    public static void copyFolderContents(Path source, Path destination) throws IOException {
        lock.lock();
        if (!Files.exists(source)) {
            throw new IOException("Source folder does not exist: " + source);
        }

        if (!Files.exists(destination)) {
            Files.createDirectories(destination);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(source)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Path targetFile = destination.resolve(file.getFileName());
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } finally {
            lock.unlock();
        }
        System.out.println("Copied files from: " + source + " → " + destination);
    }
}