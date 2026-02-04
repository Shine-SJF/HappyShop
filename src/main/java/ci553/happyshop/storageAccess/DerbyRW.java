package ci553.happyshop.storageAccess;

import ci553.happyshop.catalogue.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

    //the DerbyRW class is the database aacceesss layer that implements the DatabaseRW interface
    //it handle all databsse operations for teh HHappyshop system using AApache Derby databasse

public class DerbyRW implements DatabaseRW {
    private static String dbURL = DatabaseRWFactory.dbURL;
    private Lock lock = new ReentrantLock();

    // Search product by product Id or name
    public ArrayList<Product> searchProduct(String keyword) throws SQLException {
        ArrayList<Product> productList = new ArrayList<>();

        // Search by product ID first
        Product product = searchByProductId(keyword);
        if (product != null) {
            productList.add(product);
        } else {
            // If no products found by ID, search by product name
            productList = searchByProName(keyword);
        }

        if (productList.isEmpty()) {
            System.out.println("Product " + keyword + " not found.");
        }
        return productList;
    }

    // Search by product Id
    public Product searchByProductId(String proId) throws SQLException {
        Product product = null;
        String query = "SELECT * FROM ProductTable WHERE productID = ?";

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, proId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = makeProObjFromDbRecord(rs);
                    System.out.println("Product " + proId + " found.");
                } else {
                    System.out.println("Product " + proId + " not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    // Search by product name
    private ArrayList<Product> searchByProName(String name) {
        ArrayList<Product> productList = new ArrayList<>();
        String query = "SELECT * FROM ProductTable WHERE LOWER(description) LIKE LOWER(?)";

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + name.toLowerCase() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productList.add(makeProObjFromDbRecord(rs));
                }

                if (productList.isEmpty()) {
                    System.out.println("Product " + name + " not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database query error, search by name: " + name + " " + e.getMessage());
        }

        return productList;
    }

    // Make a Product object from database record
    private Product makeProObjFromDbRecord(ResultSet rs) throws SQLException {
        Product product = null;
        String productId = rs.getString("productID");
        String description = rs.getString("description");
        String imagePath = rs.getString("image");
        double unitPrice = rs.getDouble("unitPrice");
        int inStock = rs.getInt("inStock");
        product = new Product(productId, description, imagePath, unitPrice, inStock);

        System.out.println("Product ID: " + productId);
        System.out.println("Description: " + description);
        System.out.println("Image: " + imagePath);
        System.out.println("unitPrice: " + unitPrice);

        if (inStock <= 0) {
            System.out.println("Product " + productId + " is NOT in stock");
        } else if (inStock < 10) {
            System.out.println("Product " + productId + " low stock warning! " + inStock + " units left.");
        } else {
            System.out.println("Product " + productId + " is available");
        }

        System.out.println("-----");
        return product;
    }

    public ArrayList<Product> purchaseStocks(ArrayList<Product> proList) throws SQLException {
        lock.lock();
        ArrayList<Product> insufficientProducts = new ArrayList<>();

        String checkSql = "SELECT inStock FROM ProductTable WHERE productId = ?";
        String updateSql = "UPDATE ProductTable SET inStock = inStock - ? WHERE productId = ?";

        try (Connection conn = DriverManager.getConnection(dbURL)) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                boolean allSufficient = true;

                for (Product product : proList) {
                    checkStmt.setString(1, product.getProductId());
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        int currentStock = rs.getInt("inStock");
                        int newStock = currentStock - product.getOrderedQuantity();

                        System.out.println("Product ID: " + product.getProductId());
                        System.out.println("Before change: " + currentStock);
                        System.out.println("Quantity Ordered: " + product.getOrderedQuantity());

                        if (newStock >= 0) {
                            updateStmt.setInt(1, product.getOrderedQuantity());
                            updateStmt.setString(2, product.getProductId());
                            updateStmt.addBatch();

                            System.out.println("After change: " + newStock);
                            System.out.println("Update successful for Product ID: " + product.getProductId());
                        } else {
                            insufficientProducts.add(product);
                            allSufficient = false;
                            System.out.println("Not enough stock for Product ID: " + product.getProductId());
                        }
                        System.out.println("--------------------------------");
                    }
                }

                if (allSufficient) {
                    updateStmt.executeBatch();
                    conn.commit();
                    System.out.println("Database update successful.");
                } else {
                    conn.rollback();
                    System.out.println("Insufficient stock for some products, all updates rolled back.");
                }

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Database update error, update failed");
            }
        } finally {
            lock.unlock();
        }

        return insufficientProducts;
    }

    // Warehouse edits an existing product
    public void updateProduct(String id, String des, double price, String iName, int stock) throws SQLException {
        lock.lock();
        String selectSql = "SELECT * FROM ProductTable WHERE productID = ?";
        String updateSql = "UPDATE ProductTable SET description = ?, unitPrice = ?, image = ?, inStock = ? WHERE productID = ?";

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            // Print Before Update
            selectStmt.setString(1, id);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Before Update:");
                    System.out.println("ID: " + rs.getString("productID"));
                    System.out.println("Description: " + rs.getString("description"));
                    System.out.println("Unit Price: " + rs.getDouble("unitPrice"));
                    System.out.println("Stock: " + rs.getInt("inStock"));
                    System.out.println("Image: " + rs.getString("image"));
                } else {
                    System.out.println("Product not found: " + id);
                    return;
                }
            }

            // Perform Update
            updateStmt.setString(1, des);
            updateStmt.setDouble(2, price);
            updateStmt.setString(3, iName);
            updateStmt.setInt(4, stock);
            updateStmt.setString(5, id);
            updateStmt.executeUpdate();

            // Print After Update
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("After Update:");
                    System.out.println("ID: " + rs.getString("productID"));
                    System.out.println("Description: " + rs.getString("description"));
                    System.out.println("Unit Price: " + rs.getDouble("unitPrice"));
                    System.out.println("Stock: " + rs.getInt("inStock"));
                    System.out.println("image: " + rs.getString("image"));
                }
            }
        } finally {
            lock.unlock();
        }
    }

    // Warehouse delete an existing product
    public void deleteProduct(String proId) throws SQLException {
        lock.lock();
        String selectSql = "SELECT * FROM ProductTable WHERE productID = ?";
        String deleteSql = "DELETE FROM ProductTable WHERE productID = ?";

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            conn.setAutoCommit(true);

            selectStmt.setString(1, proId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Before delete:");
                    System.out.println("ID: " + rs.getString("productID"));
                    System.out.println("Description: " + rs.getString("description"));
                    System.out.println("Unit Price: " + rs.getDouble("unitPrice"));
                    System.out.println("Stock: " + rs.getInt("inStock"));
                } else {
                    System.out.println("Product not found: " + proId);
                    return;
                }
            }

            deleteStmt.setString(1, proId);
            deleteStmt.executeUpdate();
            System.out.println("Product " + proId + " deleted from database.");
        } finally {
            lock.unlock();
        }
    }

    // Check if product ID is unique
    public boolean isProIdAvailable(String proId) throws SQLException {
        String query = "SELECT COUNT(*) FROM ProductTable WHERE productID = ?";

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, proId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0; // Available if count is 0
            }
            return false;
        }
    }

    public void insertNewProduct(String id, String des, double price, String image, int stock) throws SQLException {
        lock.lock();

        // FIXED: Explicitly specify column names to match values order
        String insertSql = "INSERT INTO ProductTable (productID, description, unitPrice, image, inStock) VALUES (?, ?, ?, ?, ?)";
        String selectSql = "SELECT * FROM ProductTable WHERE productID = ?";

        try (Connection conn = DriverManager.getConnection(dbURL);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            conn.setAutoCommit(true);

            // Set parameters in correct order matching column definition
            insertStmt.setString(1, id);           // productID CHAR(4)
            insertStmt.setString(2, des);          // description VARCHAR(100)
            insertStmt.setDouble(3, price);        // unitPrice DOUBLE
            insertStmt.setString(4, image);        // image VARCHAR(100)
            insertStmt.setInt(5, stock);           // inStock INT

            System.out.println("Executing INSERT with values:");
            System.out.println("  productID: " + id);
            System.out.println("  description: " + des);
            System.out.println("  unitPrice: " + price);
            System.out.println("  image: " + image);
            System.out.println("  inStock: " + stock);

            insertStmt.executeUpdate();

            // Verify insertion
            selectStmt.setString(1, id);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Insert successful for Product ID: " + id);
                System.out.println("ID: " + rs.getString("productID"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Unit Price: " + rs.getDouble("unitPrice"));
                System.out.println("Image: " + rs.getString("image"));
                System.out.println("Stock: " + rs.getInt("inStock"));
            } else {
                System.out.println("WARNING: Insert may have failed - cannot find product " + id);
            }
        } catch (SQLException e) {
            System.err.println("ERROR inserting product: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            throw e; // Re-throw so model can handle
        } finally {
            lock.unlock();
        }
    }
}