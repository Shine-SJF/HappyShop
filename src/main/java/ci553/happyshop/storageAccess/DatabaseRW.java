package ci553.happyshop.storageAccess;

import ci553.happyshop.catalogue.Product;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The DatabaseRW interface defines the contract for interacting with the product database.
 * It is currently implemented by the DerbyRW class, which provides the actual functionality.
 *
 * Responsibilities:
 * - Searching for products by keyword or product ID.
 * - Performing stock updates and validations during purchases.
 * - Updating, deleting, or inserting products.
 * - Checking whether a product ID is available before insertion.
 */

public interface DatabaseRW {

     //Searches for products by a keyword, which may match the product ID or appear in the description.

    ArrayList<Product> searchProduct(String keyword) throws SQLException;

     // Searches for a product by its unique product ID.

    Product searchByProductId(String productId) throws SQLException;

    ArrayList<Product> purchaseStocks(ArrayList<Product> proList) throws SQLException;

    void updateProduct(String id, String des, double price, String imageName, int stock) throws SQLException;

    // Deletes a product identified by its ID.
    void deleteProduct(String id) throws SQLException;

    void insertNewProduct(String id, String des, double price, String image, int stock) throws SQLException;


    //Checks whether the given product ID is available for use (i.e., not already in use).

    boolean isProIdAvailable(String productId) throws SQLException;
}


