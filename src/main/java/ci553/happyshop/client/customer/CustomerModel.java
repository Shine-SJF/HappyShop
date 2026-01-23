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

    void addToTrolley() {
        if (theProduct == null) {
            displayLaSearchResult = "Please search for an available product before adding it to the trolley";
            System.out.println("Must search and get an available product before adding to trolley");
            updateView();
            return;
        }
        // Determine remaining stock
        int stockLeft = theProduct.getStockQuantity() - getOrderedQuantityInTrolley(theProduct.getProductId());
        if (stockLeft <= 0) {
            displayLaSearchResult = "Cannot add more, product is out of stock!";
            System.out.println("Cannot add more, product is out of stock!");
            updateView();
            return;
        }
        boolean merged = false;
        for (Product p : trolley) {
            if (p.getProductId().equals(theProduct.getProductId())) {
                p.setOrderedQuantity(p.getOrderedQuantity() + 1); //Add ONE unit only
                merged = true;
                break;
            }
        }
        if (!merged) {
            // Add a new product to trolley
            Product copy = new Product(
                    theProduct.getProductId(),
                    theProduct.getProductDescription(),
                    theProduct.getProductImageName(),
                    theProduct.getUnitPrice(),
                    theProduct.getStockQuantity()
            );
            copy.setOrderedQuantity(1);
            trolley.add(copy);
        }
        // Sort trolley by product ID
        trolley.sort(Comparator.comparing(Product::getProductId));
        // Update display
        displayTaTrolley = ProductListFormatter.buildString(trolley);
        displayTaReceipt = "";
        updateView();
    }

    /**
     * Helper method to calculate how many units of a product are already in the trolley
     */
    private int getOrderedQuantityInTrolley(String productId) {
        for (Product p : trolley) {
            if (p.getProductId().equals(productId)) {
                return p.getOrderedQuantity();
            }
        }
        return 0;
    }

    void checkOut() throws IOException, SQLException {

        if (trolley.isEmpty()) {
            displayTaTrolley = "Your trolley is empty";
            updateView();
            return;
        }

        // Group products to optimise stock checking
        ArrayList<Product> groupedTrolley = groupProductsById(trolley);

        // Attempt to purchase stock
        ArrayList<Product> insufficientProducts =
                databaseRW.purchaseStocks(groupedTrolley);

        //1: All stock sufficient
        if (insufficientProducts.isEmpty()) {

            OrderHub orderHub = OrderHub.getOrderHub();
            Order order = orderHub.newOrder(trolley);

            trolley.clear();
            displayTaTrolley = "";

            displayTaReceipt = String.format(
                    "Order_ID: %s\nOrdered_Date_Time: %s\n%s",
                    order.getOrderId(),
                    order.getOrderedDateTime(),
                    ProductListFormatter.buildString(order.getProductList())
            );

            updateView();
            return;
        }
        //2: Insufficient stock
        StringBuilder msg = new StringBuilder(
                "The following products were removed due to insufficient stock:\n\n"
        );
        for (Product p : insufficientProducts) {
            msg.append("• ")
                    .append(p.getProductId()).append(", ")
                    .append(p.getProductDescription())
                    .append(" (Only ")
                    .append(p.getStockQuantity()).append(" available, ")
                    .append(p.getOrderedQuantity()).append(" requested)\n");
        }
        // Remove insufficient products from trolley
        trolley.removeIf(tp ->
                insufficientProducts.stream().anyMatch(ip ->
                        ip.getProductId().equals(tp.getProductId()))
        );
        // Show popup notification
        RemoveProductNotifier notifier = new RemoveProductNotifier();
        notifier.showRemovalMsg(msg.toString());
        notifier.closeNotifierWindow();

        // Refresh trolley display
        displayTaTrolley = ProductListFormatter.buildString(trolley);
        displayTaReceipt = "";
        theProduct = null;

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
        // Four UI elements to be passed to CustomerView for display updates.
        // Image to show in product preview (Search Page)
        String imageName;
        if(theProduct != null){
            imageName = theProduct.getProductImageName();
            String relativeImageUrl = StorageLocation.imageFolder + imageName; //relative file path, eg images/0001.jpg
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
