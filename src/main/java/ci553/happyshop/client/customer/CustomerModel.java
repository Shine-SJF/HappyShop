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

//the customermodel is the business logic layer in the MVC pattern for the customer shoppign system

public class CustomerModel {
    public CustomerView cusView;
    public DatabaseRW databaseRW;

    private Product theProduct = null;
    private ArrayList<Product> trolley = new ArrayList<>();

    // For UI display
    private String imageName = "imageHolder.jpg";
    private String displayLaSearchResult = "👋 Welcome to HappyShop!\n\nSearch for products using the form above to see detailed information, pricing, and availability.";
    private String displayTaReceipt = "";

    // Additional state for better UX
    private String lastSearchedId = "";
    private boolean isCheckoutSuccess = false;

    /**
     * Search for products by Product ID or Product Name
     * Priority: If product name is provided, search by name; otherwise search by ID
     */
    void search() throws SQLException {
        String productId = cusView.getSearchProductId();
        String productName = cusView.getSearchProductName();

        // Determine search term - prioritize name if provided, otherwise use ID
        String searchTerm = !productName.isEmpty() ? productName : productId;
        lastSearchedId = searchTerm;

        if(!searchTerm.isEmpty()){
            // Use the generic searchProduct method which handles both ID and name
            ArrayList<Product> results = databaseRW.searchProduct(searchTerm);

            if(!results.isEmpty() && results.get(0).getStockQuantity() > 0){
                theProduct = results.get(0); // Take the first result

                double unitPrice = theProduct.getUnitPrice();
                String description = theProduct.getProductDescription();
                int stock = theProduct.getStockQuantity();
                String imageName = theProduct.getProductImageName();

                // Modern formatted product info
                StringBuilder productInfo = new StringBuilder();
                productInfo.append("✅ Product Found!\n\n");
                productInfo.append("📦 ").append(description).append("\n");
                productInfo.append("🏷️  ID: ").append(theProduct.getProductId()).append("\n");
                productInfo.append("💰 Price: £").append(String.format("%.2f", unitPrice)).append("\n");

                // Stock information with emojis
                if (stock >= 50) {
                    productInfo.append("📈 Stock: ").append(stock).append(" units (In Stock)");
                } else if (stock >= 10) {
                    productInfo.append("⚠️  Stock: ").append(stock).append(" units (Limited Stock)");
                } else if (stock > 0) {
                    productInfo.append("🚨 Stock: ").append(stock).append(" units (Low Stock - Order Soon!)");
                } else {
                    productInfo.append("❌ Out of Stock");
                }

                displayLaSearchResult = productInfo.toString();

                // Clear any previous error messages
                displayTaReceipt = "";

                System.out.println("Product found: " + theProduct.getProductId() + " - " + description);
            }
            else{
                theProduct = null;
                if (results.isEmpty()) {
                    displayLaSearchResult = "❌ Product Not Found\n\nNo product found matching: " + searchTerm +
                            "\n\nPlease check your search term and try again.";
                } else {
                    displayLaSearchResult = "❌ Out of Stock\n\nProduct matching '" + searchTerm +
                            "' is currently out of stock.\n\nPlease check back later or browse other products.";
                }
                System.out.println("No product found or out of stock: " + searchTerm);
            }
        } else {
            theProduct = null;
            displayLaSearchResult = "👋 Welcome to HappyShop!\n\nSearch for products using the form above to see detailed information, pricing, and availability.";
            System.out.println("Empty search - showing welcome message.");
        }
        updateView();
    }

    // ==================== addToTrolley() METHOD ====================
    void addToTrolley() {
        if(theProduct != null) {
            // Check if product already exists in trolley
            boolean found = false;
            for(Product p : trolley) {
                if(p.getProductId().equals(theProduct.getProductId())) {
                    // Check if adding one more exceeds stock
                    if(p.getOrderedQuantity() + 1 > theProduct.getStockQuantity()) {
                        displayLaSearchResult = "🚨 Cannot Add More\n\nProduct: " + theProduct.getProductDescription() +
                                "\nOnly " + theProduct.getStockQuantity() + " units available.\n" +
                                "You already have " + p.getOrderedQuantity() + " in your cart.";
                        updateView();
                        return;
                    }

                    // Product exists - increase quantity
                    p.setOrderedQuantity(p.getOrderedQuantity() + 1);
                    found = true;

                    // Update success message
                    displayLaSearchResult = "✅ Added to Cart!\n\n" +
                            p.getProductDescription() +
                            "\nQuantity: " + p.getOrderedQuantity() +
                            "\nAdded to your shopping cart.";

                    System.out.println("Product " + p.getProductId() + " quantity increased to " + p.getOrderedQuantity());
                    break;
                }
            }

            // If product not found, add new product to trolley
            if(!found) {
                // Check stock availability
                if(theProduct.getStockQuantity() <= 0) {
                    displayLaSearchResult = "❌ Out of Stock\n\n" +
                            theProduct.getProductDescription() +
                            "\nThis product is currently out of stock.";
                    updateView();
                    return;
                }

                // Create a new Product instance to avoid reference issues
                Product newProduct = new Product(
                        theProduct.getProductId(),
                        theProduct.getProductDescription(),
                        theProduct.getProductImageName(),
                        theProduct.getUnitPrice(),
                        theProduct.getStockQuantity()
                );
                newProduct.setOrderedQuantity(1); // Set initial quantity to 1
                trolley.add(newProduct);

                // Update success message
                displayLaSearchResult = "✅ Added to Cart!\n\n" +
                        newProduct.getProductDescription() +
                        "\nSuccessfully added to your shopping cart.";

                System.out.println("Product " + newProduct.getProductId() + " added to trolley");
            }

            // Sort trolley by product ID
            sortTrolley();

            // Clear receipt if any
            displayTaReceipt = "";
        }
        else {
            if (lastSearchedId.isEmpty()) {
                displayLaSearchResult = "🔍 Search First\n\nPlease search for a product before adding to cart.";
            } else {
                displayLaSearchResult = "❌ No Product Selected\n\nPlease search and select a valid product first.";
            }
            System.out.println("Attempted to add to trolley without a product selected.");
        }
        updateView();
    }

    // ==================== SORT TROLLEY ====================
    private void sortTrolley() {
        trolley.sort((p1, p2) -> p1.getProductId().compareTo(p2.getProductId()));
    }

    // ==================== changeProductQuantity() METHOD ====================
    void changeProductQuantity(Product product, int change) throws SQLException {
        for(int i = 0; i < trolley.size(); i++) {
            Product p = trolley.get(i);
            if(p.getProductId().equals(product.getProductId())) {
                int newQuantity = p.getOrderedQuantity() + change;

                // If quantity becomes 0 or negative, remove product from trolley
                if(newQuantity <= 0) {
                    trolley.remove(i);
                    displayLaSearchResult = "🗑️ Removed from Cart\n\n" +
                            p.getProductDescription() +
                            "\nhas been removed from your shopping cart.";
                    System.out.println("Product " + p.getProductId() + " removed from trolley");
                } else {
                    // Check if new quantity exceeds stock
                    if(newQuantity > p.getStockQuantity()) {
                        displayLaSearchResult = "🚨 Stock Limit\n\n" +
                                p.getProductDescription() +
                                "\nOnly " + p.getStockQuantity() + " units available.\n" +
                                "Cannot increase quantity further.";
                        System.out.println("Quantity exceeds available stock for " + p.getProductId());
                    } else {
                        p.setOrderedQuantity(newQuantity);
                        displayLaSearchResult = "🛒 Cart Updated\n\n" +
                                p.getProductDescription() +
                                "\nQuantity updated to: " + newQuantity;
                        System.out.println("Product " + p.getProductId() + " quantity changed to " + newQuantity);
                    }
                }
                break;
            }
        }
        updateView();
    }

    // ==================== checkOut() METHOD ====================
    /**
     * PROCESSES THE CUSTOMER'S CHECKOUT REQUEST
     *
     * This is the core checkout method that:
     * 1. Validates cart is not empty
     * 2. Groups duplicate products by ID for proper stock management
     * 3. Checks stock availability via database
     * 4. Creates order if stock is sufficient
     * 5. Generates receipt and clears cart
     * 6. Handles insufficient stock scenarios
     *
     * Key Business Logic:
     * - Uses purchaseStocks() for atomic stock verification
     * - Creates order through OrderHub singleton
     * - Generates formatted receipt for user confirmation
     * - Clears cart only after successful stock verification
     * - Provides detailed error messages for insufficient stock
     *
     * @throws IOException if file operations fail during order processing
     * @throws SQLException if database operations fail
     */
    void checkOut() throws IOException, SQLException {
        // 1. VALIDATE CART HAS ITEMS
        if(!trolley.isEmpty()){
            // 2. GROUP PRODUCTS - Consolidate duplicate items by product ID
            // This ensures quantities are summed correctly before stock check
            ArrayList<Product> groupedTrolley = groupProductsById(trolley);

            // 3. CRITICAL: VERIFY STOCK AVAILABILITY
            // purchaseStocks() checks AND reserves stock atomically
            // Returns list of products with insufficient stock (empty list if all OK)
            ArrayList<Product> insufficientProducts = databaseRW.purchaseStocks(groupedTrolley);

            // 4. PROCESS ORDER IF STOCK IS AVAILABLE
            if(insufficientProducts.isEmpty()){
                // 4a. CREATE ORDER via singleton OrderHub
                // OrderHub manages order ID generation and persistence
                OrderHub orderHub = OrderHub.getOrderHub();
                Order theOrder = orderHub.newOrder(trolley);

                // 4b. CLEAR CART - Only after successful order creation
                // This prevents losing items if order creation fails
                trolley.clear();

                // 4c. GENERATE FORMATTED RECEIPT
                // Uses ProductListFormatter for consistent product display
                StringBuilder receiptBuilder = new StringBuilder();
                receiptBuilder.append("🎉 ORDER CONFIRMED!\n");
                receiptBuilder.append("═══════════════════════════\n\n");
                receiptBuilder.append("📋 Order ID: ").append(theOrder.getOrderId()).append("\n");
                receiptBuilder.append("📅 Date: ").append(theOrder.getOrderedDateTime()).append("\n\n");
                receiptBuilder.append("📦 ORDER DETAILS:\n");
                receiptBuilder.append(ProductListFormatter.buildString(theOrder.getProductList())).append("\n\n");
                receiptBuilder.append("═══════════════════════════\n");
                receiptBuilder.append("✅ Thank you for your purchase!\n");
                receiptBuilder.append("   Your items will be processed shortly.\n\n");
                receiptBuilder.append("📧 A confirmation email has been sent.\n");
                receiptBuilder.append("📱 Track your order using the Order ID.");

                // 4d. UPDATE VIEW STATE
                displayTaReceipt = receiptBuilder.toString();
                displayLaSearchResult = "✅ Checkout Successful!\n\nYour order has been processed.\nCheck your receipt for details.";

                // 4e. SET CHECKOUT SUCCESS FLAG
                // Used by closeReceipt() to show appropriate follow-up message
                isCheckoutSuccess = true;
                System.out.println("Checkout successful. Order ID: " + theOrder.getOrderId());
            }
            else{
                // 5. HANDLE INSUFFICIENT STOCK SCENARIO
                // Build detailed error message showing exactly which items are problematic
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("🚨 Checkout Failed\n\n");
                errorMsg.append("The following items have insufficient stock:\n\n");

                for(Product p : insufficientProducts){
                    errorMsg.append("• ").append(p.getProductDescription())
                            .append(" (ID: ").append(p.getProductId()).append(")\n")
                            .append("  Available: ").append(p.getStockQuantity())
                            .append(" | Requested: ").append(p.getOrderedQuantity()).append("\n\n");

                    // AUTO-REMOVE: Remove problematic items from cart
                    // Prevents repeated checkout attempts with same insufficient items
                    trolley.removeIf(item -> item.getProductId().equals(p.getProductId()));
                }

                errorMsg.append("These items have been removed from your cart.\n");
                errorMsg.append("Please adjust quantities and try again.");

                // 5a. RESET PRODUCT SELECTION AND DISPLAY ERRORS
                theProduct = null;
                displayLaSearchResult = errorMsg.toString();
                displayTaReceipt = "";

                System.out.println("Checkout failed due to insufficient stock");
            }
        }
        else{
            // 6. HANDLE EMPTY CART SCENARIO
            // Prevents processing empty orders
            displayLaSearchResult = "🛒 Empty Cart\n\nYour shopping cart is empty.\n\nAdd some products before checking out.";
            System.out.println("Checkout attempted with empty trolley");
        }
        // 7. FINAL VIEW UPDATE
        // Ensures UI reflects the new state (success, error, or empty cart)
        updateView();
    }

    // ==================== GROUP PRODUCTS BY ID ====================
    private ArrayList<Product> groupProductsById(ArrayList<Product> proList) {
        Map<String, Product> grouped = new HashMap<>();
        for (Product p : proList) {
            String id = p.getProductId();
            if (grouped.containsKey(id)) {
                Product existing = grouped.get(id);
                existing.setOrderedQuantity(existing.getOrderedQuantity() + p.getOrderedQuantity());
            } else {
                grouped.put(id, new Product(p.getProductId(), p.getProductDescription(),
                        p.getProductImageName(), p.getUnitPrice(), p.getStockQuantity()));
                grouped.get(id).setOrderedQuantity(p.getOrderedQuantity());
            }
        }
        return new ArrayList<>(grouped.values());
    }

    // ==================== CANCEL/CLEAR CART ====================
    void cancel(){
        if (!trolley.isEmpty()) {
            int itemCount = trolley.size();
            trolley.clear();
            displayLaSearchResult = "🗑️ Cart Cleared\n\n" + itemCount + " item(s) removed from your cart.\n\nYour cart is now empty.";
            System.out.println("Cart cleared - " + itemCount + " items removed");
        } else {
            displayLaSearchResult = "🛒 Cart Already Empty\n\nYour shopping cart is already empty.";
        }
        displayTaReceipt = "";
        updateView();
    }

    // ==================== CLOSE RECEIPT ====================
    void closeReceipt(){
        if (isCheckoutSuccess) {
            // Reset to welcome message after successful checkout
            displayLaSearchResult = "👋 Welcome Back!\n\nThank you for your recent purchase.\n\nStart shopping again or check your order status.";
            isCheckoutSuccess = false;
        }
        displayTaReceipt = "";
        updateView();
    }

    // ==================== UPDATE VIEW ====================
    void updateView() {
        try {
            if(theProduct != null){
                imageName = theProduct.getProductImageName();
                String relativeImageUrl = StorageLocation.imageFolder + imageName;
                Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
                if (imageFullPath.toFile().exists()) {
                    imageName = imageFullPath.toUri().toString();
                    System.out.println("Image loaded: " + imageFullPath);
                } else {
                    imageName = "imageHolder.jpg";
                    System.out.println("Product image not found, using placeholder");
                }
            }
            else{
                imageName = "imageHolder.jpg";
            }

            // Update the view with current state
            cusView.update(imageName, displayLaSearchResult, trolley, displayTaReceipt);

        } catch (Exception e) {
            System.err.println("Error updating view: " + e.getMessage());
            e.printStackTrace();
            // Fallback to ensure view is updated
            cusView.update("imageHolder.jpg",
                    "⚠️ System Error\n\nAn error occurred. Please try again.",
                    trolley,
                    "");
        }
    }

    // ==================== GETTERS ====================
    public ArrayList<Product> getTrolley() {
        return new ArrayList<>(trolley); // Return copy to prevent external modification
    }
}