package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.catalogue.exception.ExcessiveOrderQuantityException;
import ci553.happyshop.catalogue.exception.UnderMinimumPaymentException;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.payment.PaymentException;
import ci553.happyshop.payment.PaymentService;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.utility.ProductListFormatter;
import ci553.happyshop.utility.StorageLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CustomerModel
 *
 * What this class does (in plain terms):
 * - Keeps track of what the customer has searched for and what's in their trolley
 * - Validates rules like minimum payment and max quantity
 * - Talks to the database to check/purchase stock
 * - Uses PaymentService for payment (so I can swap in ATM or a dummy ATM without changing checkout code)
 */
public class CustomerModel {

    // Injected references (this is how the project was set up already)
    public CustomerView cusView;
    public DatabaseRW databaseRW; // interface type

    // Product / trolley state
    private Product theProduct = null;
    private final ArrayList<Product> trolley = new ArrayList<>();

    // Payment dependency (injected)
    private PaymentService paymentService;

    // --- Constructors / injection ---

    // Kept so existing code that does new CustomerModel() still works
    public CustomerModel() {
    }

    // Constructor injection option (nice for testing/clean design)
    public CustomerModel(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Setter injection option (works well with the existing Main wiring)
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // --- UI display fields (passed to CustomerView) ---
    private String imageName = "imageHolder.jpg";
    private String displayLaSearchResult = "No Product was searched yet";
    private String displayTaTrolley = "";
    private String displayTaReceipt = "";

    private double calculateTotalPayment() {
        return trolley.stream()
                .mapToDouble(p -> p.getUnitPrice() * p.getOrderedQuantity())
                .sum();
    }

    // -------------------- Actions called by Controller / View --------------------

    void search() throws SQLException {

        String raw = cusView.tfId.getText();
        String keyword = (raw == null) ? "" : raw.trim();

        if (keyword.isEmpty()) {
            theProduct = null;
            displayLaSearchResult = "Please type a Product ID or a keyword.";
            updateView();
            return;
        }

        // Support simple filters like: id:0001, name:milk
        String mode = "any";
        String query = keyword;

        if (keyword.contains(":")) {
            String[] parts = keyword.split(":", 2);
            mode = parts[0].trim().toLowerCase();
            query = parts[1].trim();
        }

        // Base search from database (we reuse what HappyShop already provides)
        ArrayList<Product> results = databaseRW.searchProduct(query);

        // Extra filtering on top (simple but useful)
        ArrayList<Product> filtered = new ArrayList<>();

        for (Product p : results) {
            switch (mode) {
                case "id" -> {
                    if (p.getProductId().equalsIgnoreCase(query)) filtered.add(p);
                }
                case "name" -> {
                    if (p.getProductDescription().toLowerCase().contains(query.toLowerCase())) filtered.add(p);
                }
                case "stock" -> {
                    // stock:in  -> only show items with stock > 0
                    if (query.equalsIgnoreCase("in") && p.getStockQuantity() > 0) filtered.add(p);
                    // stock:out -> only show items with stock == 0
                    if (query.equalsIgnoreCase("out") && p.getStockQuantity() == 0) filtered.add(p);
                }
                default -> filtered.add(p); // normal behaviour
            }
        }

        cusView.productList.clear();
        cusView.productList.addAll(filtered);

        if (!filtered.isEmpty()) {
            theProduct = filtered.get(0);
            displayLaSearchResult = filtered.size() + " product(s) found for: " + keyword;
        } else {
            theProduct = null;
            displayLaSearchResult = "No product found for: " + keyword;
        }

        updateView();
    }


    void addToTrolley() {
        Product selectedProduct = cusView.lvSearchResults.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            boolean merged = false;

            for (Product p : trolley) {
                if (p.getProductId().equals(selectedProduct.getProductId())) {

                    if (p.getOrderedQuantity() + 1 > selectedProduct.getStockQuantity()) {
                        displayLaSearchResult = "Insufficient stock available for product: " + selectedProduct.getProductId();
                        System.out.println("Insufficient stock available");
                        updateView();
                        return;
                    }

                    p.setOrderedQuantity(p.getOrderedQuantity() + 1);
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                Product newProduct = new Product(
                        selectedProduct.getProductId(),
                        selectedProduct.getProductDescription(),
                        selectedProduct.getProductImageName(),
                        selectedProduct.getUnitPrice(),
                        selectedProduct.getStockQuantity()
                );
                newProduct.setOrderedQuantity(1);
                trolley.add(newProduct);
            }

            trolley.sort(Comparator.comparing(Product::getProductId));
            displayTaTrolley = ProductListFormatter.buildString(trolley);
            displayTaReceipt = "";
        } else {
            displayLaSearchResult = "Please select a product before adding it to the trolley";
            System.out.println(displayLaSearchResult);
        }

        updateView();
    }

    // Kept for compatibility (some earlier tasks might still use it)
    void makeOrganizedTrolley() {
        for (Product p : trolley) {
            if (theProduct != null && p.getProductId().equals(theProduct.getProductId())) {
                p.setOrderedQuantity(p.getOrderedQuantity() + 1);
                Collections.sort(trolley, Comparator.comparing(Product::getProductId));
                return;
            }
        }
        if (theProduct != null) {
            theProduct.setOrderedQuantity(1);
            trolley.add(theProduct);
            Collections.sort(trolley, Comparator.comparing(Product::getProductId));
        }
    }

    // -------------------- Item-level trolley controls (extension) --------------------

    // Remove an item completely by product ID
    public void removeItemFromTrolley(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            displayLaSearchResult = "Enter a product ID to remove.";
            updateView();
            return;
        }

        String id = productId.trim();
        boolean removed = trolley.removeIf(p -> p.getProductId().equalsIgnoreCase(id));

        if (removed) {
            displayLaSearchResult = "Removed " + id + " from trolley.";
            trolley.sort(Comparator.comparing(Product::getProductId));
            displayTaTrolley = ProductListFormatter.buildString(trolley);
        } else {
            displayLaSearchResult = "That product isn't in your trolley.";
        }

        updateView();
    }

    // Decrease quantity by 1 (removes the product if it reaches 0)
    public void decreaseItemQuantity(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            displayLaSearchResult = "Enter a product ID to decrease.";
            updateView();
            return;
        }

        String id = productId.trim();

        for (int i = 0; i < trolley.size(); i++) {
            Product p = trolley.get(i);

            if (p.getProductId().equalsIgnoreCase(id)) {
                int newQty = p.getOrderedQuantity() - 1;

                if (newQty <= 0) {
                    trolley.remove(i);
                    displayLaSearchResult = "Removed " + id + " from trolley.";
                } else {
                    p.setOrderedQuantity(newQty);
                    displayLaSearchResult = "Decreased quantity of " + id + " to " + newQty + ".";
                }

                trolley.sort(Comparator.comparing(Product::getProductId));
                displayTaTrolley = ProductListFormatter.buildString(trolley);
                updateView();
                return;
            }
        }

        displayLaSearchResult = "That product isn't in your trolley.";
        updateView();
    }

    // Increase quantity by 1 (simple version)
    public void increaseItemQuantity(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            displayLaSearchResult = "Enter a product ID to increase.";
            updateView();
            return;
        }

        String id = productId.trim();

        for (Product p : trolley) {
            if (p.getProductId().equalsIgnoreCase(id)) {
                p.setOrderedQuantity(p.getOrderedQuantity() + 1);
                displayLaSearchResult = "Increased quantity of " + id + " to " + p.getOrderedQuantity() + ".";
                trolley.sort(Comparator.comparing(Product::getProductId));
                displayTaTrolley = ProductListFormatter.buildString(trolley);
                updateView();
                return;
            }
        }

        displayLaSearchResult = "That product isn't in your trolley.";
        updateView();
    }

    /**
     * Checkout flow:
     * - validate trolley rules
     * - check stock & purchase
     * - take payment via PaymentService
     * - create the order in OrderHub
     */
    public void checkOut() throws IOException, SQLException {

        if (trolley.isEmpty()) {
            displayTaTrolley = "Your trolley is empty";
            System.out.println("Your trolley is empty");
            updateView();
            return;
        }

        try {
            double totalPayment = calculateTotalPayment();

            validateTrolley(trolley, totalPayment);

            ArrayList<Product> groupedTrolley = groupProductsById(trolley);

            ArrayList<Product> insufficientProducts = databaseRW.purchaseStocks(groupedTrolley);

            if (!insufficientProducts.isEmpty()) {

                StringBuilder errorMsg = new StringBuilder();
                for (Product p : insufficientProducts) {
                    errorMsg.append("\u2022 ").append(p.getProductId()).append(", ")
                            .append(p.getProductDescription()).append(" (Only ")
                            .append(p.getStockQuantity()).append(" available, ")
                            .append(p.getOrderedQuantity()).append(" requested)\n");
                }

                theProduct = null;

                RemoveProductNotifier notifier = new RemoveProductNotifier();
                notifier.cusView = cusView;
                notifier.showRemovalMsg(errorMsg.toString());

                for (Product p : insufficientProducts) {
                    trolley.remove(p);
                }

                notifier.closeNotifierWindow();
                displayLaSearchResult = "Checkout failed due to insufficient stock:\n" + errorMsg;
                System.out.println("stock is not enough");

                trolley.sort(Comparator.comparing(Product::getProductId));
                displayTaTrolley = ProductListFormatter.buildString(trolley);
                updateView();
                return;
            }

            if (paymentService == null) {
                displayLaSearchResult = "Payment service is not configured.";
                updateView();
                return;
            }

            int amountToPay = (int) Math.round(totalPayment);

            try {
                paymentService.pay(amountToPay);
            } catch (PaymentException e) {
                displayLaSearchResult = "Payment failed: " + e.getMessage();
                updateView();
                return;
            }

            OrderHub orderHub = OrderHub.getOrderHub();
            Order theOrder = orderHub.newOrder(trolley);

            trolley.clear();
            displayTaTrolley = "";
            displayTaReceipt = String.format(
                    "Order_ID: %s\nOrdered_Date_Time: %s\n%s",
                    theOrder.getOrderId(),
                    theOrder.getOrderedDateTime(),
                    ProductListFormatter.buildString(theOrder.getProductList())
            );
            System.out.println(displayTaReceipt);

        } catch (UnderMinimumPaymentException e) {
            ExceptionNotifier notifier = new ExceptionNotifier();
            notifier.cusView = cusView;
            notifier.showExceptionMsg(
                    e.getMessage(),
                    "Payment Issue",
                    "Please increase your payment to at least £5. Your trolley remains unchanged."
            );
            return;

        } catch (ExcessiveOrderQuantityException e) {

            for (Product p : trolley) {
                if (p.getOrderedQuantity() > 50) {
                    p.setOrderedQuantity(50);
                }
            }

            ExceptionNotifier notifier = new ExceptionNotifier();
            notifier.cusView = cusView;
            notifier.showExceptionMsg(
                    e.getMessage(),
                    "Quantity Issue",
                    "Items exceeding 50 units were reduced. Please review your trolley."
            );
            displayTaTrolley = ProductListFormatter.buildString(trolley);
            updateView();
            return;
        }

        updateView();
    }

    // -------------------- Validation helpers --------------------

    private void validatePayment(double totalPayment) throws UnderMinimumPaymentException {
        if (totalPayment < 5) {
            throw new UnderMinimumPaymentException("Payment must be at least £5.");
        }
    }

    private void validateQuantities(List<Product> trolley) throws ExcessiveOrderQuantityException {
        for (Product p : trolley) {
            if (p.getOrderedQuantity() > 50) {
                throw new ExcessiveOrderQuantityException(
                        "Cannot order more than 50 units of " + p.getProductDescription()
                );
            }
        }
    }

    private void validateTrolley(List<Product> trolley, double totalPayment)
            throws UnderMinimumPaymentException, ExcessiveOrderQuantityException {
        validatePayment(totalPayment);
        validateQuantities(trolley);
    }

    private ArrayList<Product> groupProductsById(ArrayList<Product> proList) {
        Map<String, Product> grouped = new HashMap<>();
        for (Product p : proList) {
            String id = p.getProductId();
            if (grouped.containsKey(id)) {
                Product existing = grouped.get(id);
                existing.setOrderedQuantity(existing.getOrderedQuantity() + p.getOrderedQuantity());
            } else {
                grouped.put(id, new Product(
                        p.getProductId(),
                        p.getProductDescription(),
                        p.getProductImageName(),
                        p.getUnitPrice(),
                        p.getStockQuantity()
                ));
                grouped.get(id).setOrderedQuantity(p.getOrderedQuantity());
            }
        }
        return new ArrayList<>(grouped.values());
    }

    void cancel() {
        trolley.clear();
        displayTaTrolley = "";
        updateView();
    }

    void closeReceipt() {
        displayTaReceipt = "";
        updateView();
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
        cusView.update(imageName, displayLaSearchResult, displayTaTrolley, displayTaReceipt);
    }

    // for test only
    public ArrayList<Product> getTrolley() {
        return trolley;
    }

    public void setTheProduct(Product theProduct) {
        this.theProduct = theProduct;
    }
}
