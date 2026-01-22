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
        if(!productId.isEmpty()){
            theProduct = databaseRW.searchByProductId(productId); //search database
            if(theProduct != null && theProduct.getStockQuantity()>0){
                double unitPrice = theProduct.getUnitPrice();
                String description = theProduct.getProductDescription();
                int stock = theProduct.getStockQuantity();

                String baseInfo = String.format("Product_Id: %s\n%s,\nPrice: £%.2f", productId, description, unitPrice);
                String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
                displayLaSearchResult = baseInfo + quantityInfo;
                System.out.println(displayLaSearchResult);
            }
            else{
                theProduct=null;
                displayLaSearchResult = "No Product was found with ID " + productId;
                System.out.println("No Product was found with ID " + productId);
            }
        }else{
            theProduct=null;
            displayLaSearchResult = "Please type ProductID";
            System.out.println("Please type ProductID.");
        }
        updateView();
    }

    void addToTrolley(){
        if(theProduct!= null){

            // trolley.add(theProduct) — Product is appended to the end of the trolley.
            // To keep the trolley organized, add code here or call a method that:
            //TODO
            // 1. Merges items with the same product ID (combining their quantities).
            // 2. Sorts the products in the trolley by product ID.
            trolley.add(theProduct);
            displayTaTrolley = ProductListFormatter.buildString(trolley); //build a String for trolley so that we can show it
        }
        else{
            displayLaSearchResult = "Please search for an available product before adding it to the trolley";
            System.out.println("must search and get an available product before add to trolley");
        }
        displayTaReceipt=""; // Clear receipt to switch back to trolleyPage (receipt shows only when not empty)
        updateView();
    }

    void checkOut() {
        try {
            if(!trolley.isEmpty()){
                ArrayList<Product> groupedTrolley= groupProductsById(trolley);
                ArrayList<Product> insufficientProducts= databaseRW.purchaseStocks(groupedTrolley);

                if(insufficientProducts.isEmpty()){
                    OrderHub orderHub = OrderHub.getOrderHub();
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
                } else {
                    StringBuilder errorMsg = new StringBuilder();
                    for(Product p : insufficientProducts){
                        errorMsg.append("\u2022 ").append(p.getProductId()).append(", ")
                                .append(p.getProductDescription()).append(" (Only ")
                                .append(p.getStockQuantity()).append(" available, ")
                                .append(p.getOrderedQuantity()).append(" requested)\n");
                    }
                    theProduct=null;

                    displayLaSearchResult =
                            "Checkout failed due to insufficient stock for the following products:\n" + errorMsg;
                    System.out.println("stock is not enough");
                }
            } else {
                displayTaTrolley = "Your trolley is empty";
                System.out.println("Your trolley is empty");
            }
        } catch (IOException | SQLException e) {
            // Robustness: never crash the UI, show a friendly message instead
            displayLaSearchResult = "Checkout failed due to a system error. Please try again.";
            System.out.println("Checkout error: " + e.getMessage());
        } finally {
            updateView();
        }
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
                grouped.put(id,new Product(p.getProductId(),p.getProductDescription(),
                        p.getProductImageName(),p.getUnitPrice(),p.getStockQuantity()));
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
        if (theProduct != null) {
            imageName = theProduct.getProductImageName();
            String relativeImageUrl = StorageLocation.imageFolder + imageName;
            Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
            imageName = imageFullPath.toUri().toString();
            System.out.println("Image absolute path: " + imageFullPath);
        } else {
            imageName = "imageHolder.jpg";
        }
        if (cusView != null) {
            cusView.update(
                    imageName,
                    displayLaSearchResult,
                    displayTaTrolley,
                    displayTaReceipt
            );
        }
    }
    // extra notes:
     //Path.toUri(): Converts a Path object (a file or a directory path) to a URI object.
     //File.toURI(): Converts a File object (a file on the filesystem) to a URI object

    public ArrayList<Product> getTrolley() {
        return trolley;
    }
}
