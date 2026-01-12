package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.ProductListFormatter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * You can either directly modify the CustomerModel class to implement the required tasks,
 * or create a subclass of CustomerModel and override specific methods where appropriate.
 */
public class CustomerModel {
    public CustomerView cusView;
    public DatabaseRW databaseRW; //Interface type, not specific implementation
                                  //Benefits: Flexibility: Easily change the database implementation.

    private Product theProduct =null; // product found from search
    private ArrayList<Product> trolley =  new ArrayList<>(); // a list of products in trolley

    // Four UI elements to be passed to CustomerView for display updates.
    private String imageName = "imageHolder.jpg";                // Image to show in product preview (Search Page)
    private String displayLaSearchResult = "No Product was searched yet"; // Label showing search result message (Search Page)
    private String displayTaTrolley = "";                                // Text area content showing current trolley items (Trolley Page)
    private String displayTaReceipt = "";                                // Text area content showing receipt after checkout (Receipt Page)

    //SELECT productID, description, image, unitPrice,inStock quantity
    void search() throws SQLException {
        String productId = cusView.tfId.getText().trim();
        String productName = cusView.tfName.getText().trim();

        theProduct = null;
        //search database
        if (!productId.isEmpty()){ //if statement that checks when product ID has an input or not by the user

            theProduct = databaseRW.searchByProductId(productId); // queries the database and searches for the product ID enetered

            if (theProduct != null){ // To see if the product was found or not

                if (theProduct.getStockQuantity() > 0){ // To see if the product has a quantity over 0
                    double unitPrice = theProduct.getUnitPrice(); // gets the price of the product
                    String description = theProduct.getProductDescription(); // gets the description of the product
                    int stock = theProduct.getStockQuantity(); // gets the stock level of the product selected

                    String baseInfo = String.format(
                            "Product_Id: %s\n%s,\nPrice: £%.2f",
                            theProduct.getProductId(), // displays the product ID
                            description, // displays the description of the product
                            unitPrice // displays the price of the product
                    );

                    String quantityInfo =
                            stock < 100 ? String.format("\n%d units left.", stock) : "";

                    displayLaSearchResult = baseInfo + quantityInfo;

                }else{
                    displayLaSearchResult = "Product is out of stock"; // messgae that users see when product is out of stcok
                    theProduct = null;
                }

            }else{
                displayLaSearchResult = "No product found with ID: " + productId; // message that is displayed when an incorrect product ID was searched
            }
        }


        else if (!productName.isEmpty()){ //checks to see if product Name has an input or not from the user

            ArrayList<Product> resultList = databaseRW.searchProduct(productName); //queries the database with that product name

            if (!resultList.isEmpty()){

                theProduct = resultList.get(0);

                if (theProduct.getStockQuantity() > 0){ // To see if the product has a quantity over 0
                    double unitPrice = theProduct.getUnitPrice(); // gets the price of the product
                    String description = theProduct.getProductDescription(); // gets the description of the product
                    int stock = theProduct.getStockQuantity();// gets the stock level of the product selected


                    String baseInfo = String.format( // forms a string that will be used in the UI
                            "Product_Id: %s\n%s,\nPrice: £%.2f",
                            theProduct.getProductId(), // displays the product ID of the product
                            description, // displays the description of the product
                            unitPrice // displays the price of the product
                    );

                    String quantityInfo =
                            stock < 100 ? String.format("\n%d units left.", stock) : "";

                    displayLaSearchResult = baseInfo + quantityInfo; // displays the strings

                }else{
                    displayLaSearchResult = "Product is out of stock";
                    theProduct = null;
                }

            }else{
                displayLaSearchResult = "No product found with name: " + productName;
            }
        }


        else{
            displayLaSearchResult = "Please enter a Product ID or Product Name";
        }

        updateView();
    }

    void addToTrolley(){
        if(theProduct!= null){
            int qty = cusView.cbQuantity_Levels.getValue(); // retrives the value from the combo box
            Product productVersion = new Product(theProduct.getProductId(), theProduct.getProductDescription(), theProduct.getProductImageName(), theProduct.getUnitPrice(), theProduct.getStockQuantity()); // A new variable that is used avoids any shared references errors

            // trolley.add(theProduct) — Product is appended to the end of the trolley.
            // To keep the trolley organized, add code here or call a method that:
            //TODO
            // 1. Merges items with the same product ID (combining their quantities).
            // 2. Sorts the products in the trolley by product ID.
            productVersion.setOrderedQuantity(qty); // makes sure that the quantity the user selected matches what is added.
            trolley.add(productVersion);


            ArrayList<Product> combined_trolley = groupProductsById(trolley); // Groups duplicated items into one single line instead of two seperate ones.
            combined_trolley.sort(Comparator.comparing(Product::getProductId)); // sorts the trolley by ascedning product ID
            trolley.clear();
            trolley.addAll(combined_trolley);

            displayTaTrolley = ProductListFormatter.buildString(trolley);

        } else {
            displayLaSearchResult = "Please search for an available product before adding it to the trolley";
        }

        displayTaReceipt = "";
        updateView();
    }

    void checkOut() throws IOException, SQLException {
        if(!trolley.isEmpty()){
            // Group the products in the trolley by productId to optimize stock checking
            // Check the database for sufficient stock for all products in the trolley.
            // If any products are insufficient, the update will be rolled back.
            // If all products are sufficient, the database will be updated, and insufficientProducts will be empty.
            // Note: If the trolley is already organized (merged and sorted), grouping is unnecessary.
            ArrayList<Product> groupedTrolley= groupProductsById(trolley);
            ArrayList<Product> insufficientProducts= databaseRW.purchaseStocks(groupedTrolley);

            if(insufficientProducts.isEmpty()){ // If stock is sufficient for all products
                //get OrderHub and tell it to make a new Order
                OrderHub orderHub =OrderHub.getOrderHub();
                Order theOrder = orderHub.newOrder(trolley);
                trolley.clear();
                displayTaTrolley ="";
                displayTaReceipt = String.format(
                        "Order_ID: %s\nOrdered_Date_Time: %s\n%s",
                        theOrder.getOrderId(),
                        theOrder.getOrderedDateTime(),
                        ProductListFormatter.buildString(theOrder.getProductList())
                );
                System.out.println(displayTaReceipt);
            }
            else{ // Some products have insufficient stock — build an error message to inform the customer
                StringBuilder errorMsg = new StringBuilder();
                for(Product p : insufficientProducts){
                    errorMsg.append("\u2022 "+ p.getProductId()).append(", ")
                            .append(p.getProductDescription()).append(" (Only ")
                            .append(p.getStockQuantity()).append(" available, ")
                            .append(p.getOrderedQuantity()).append(" requested)\n");
                }
                theProduct=null;

                //TODO
                // Add the following logic here:
                // 1. Remove products with insufficient stock from the trolley.
                // 2. Trigger a message window to notify the customer about the insufficient stock, rather than directly changing displayLaSearchResult.
                //You can use the provided RemoveProductNotifier class and its showRemovalMsg method for this purpose.
                //remember close the message window where appropriate (using method closeNotifierWindow() of RemoveProductNotifier class)
                displayLaSearchResult = "Checkout failed due to insufficient stock for the following products:\n" + errorMsg.toString();
                System.out.println("stock is not enough");
            }
        }
        else{
            displayTaTrolley = "Your trolley is empty";
            System.out.println("Your trolley is empty");
        }
        updateView();
    }

    /**
     * Groups products by their productId to optimize database queries and updates.
     * By grouping products, we can check the stock for a given `productId` once, rather than repeatedly
     */
    private ArrayList<Product> groupProductsById(ArrayList<Product> proList) {
        Map<String, Product> grouped = new HashMap<>();
        for (Product p : proList) {
            String id = p.getProductId();
            if (grouped.containsKey(id)) {
                Product existing = grouped.get(id);
                existing.setOrderedQuantity(existing.getOrderedQuantity() + p.getOrderedQuantity());
            } else {
                // Make a shallow copy to avoid modifying the original
                Product Duplicate = new Product(p.getProductId(),p.getProductDescription(), p.getProductImageName(),p.getUnitPrice(),p.getStockQuantity());

                Duplicate.setOrderedQuantity(p.getOrderedQuantity());
                grouped.put(id, Duplicate);
            }
        }
        return new ArrayList<>(grouped.values());
    }

    void cancel(){
        trolley.clear();
        displayTaTrolley="";
        updateView();
    }
    void closeReceipt(){
        displayTaReceipt="";
    }

    void updateView() {
        if(theProduct != null){
            imageName = theProduct.getProductImageName();
            String relativeImageUrl = StorageLocation.imageFolder +imageName; //relative file path, eg images/0001.jpg
            // Get the full absolute path to the image
            Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
            imageName = imageFullPath.toUri().toString(); //get the image full Uri then convert to String
            System.out.println("Image absolute path: " + imageFullPath); // Debugging to ensure path is correct
        }
        else{
            imageName = "imageHolder.jpg";
        }
        cusView.update(imageName, displayLaSearchResult, displayTaTrolley,displayTaReceipt);
    }
     // extra notes:
     //Path.toUri(): Converts a Path object (a file or a directory path) to a URI object.
     //File.toURI(): Converts a File object (a file on the filesystem) to a URI object

    //for test only
    public ArrayList<Product> getTrolley() {
        return trolley;
    }
}
