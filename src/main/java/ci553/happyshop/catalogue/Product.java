package ci553.happyshop.catalogue;

import ci553.happyshop.catalogue.StockLevelIndicator.StockLevel;

/**
 * Enhanced Product class with stock level tracking and low stock warning support.
 *
 * - Tracks maximum stock capacity for percentage calculations
 * - Integrates with StockLevelIndicator for intelligent stock status
 * - Provides formatted stock status information
 * - Single Responsibility: Product manages product data
 * - Open/Closed: Extended functionality without modifying existing behavior
 * - Dependency Inversion: Depends on StockLevelIndicator abstraction
 *
 */
public class Product implements Comparable<Product> {
    private String proId;
    private String productName;          // NEW: Added product name field
    private String proDescription;
    private String proImageName;
    private double unitPrice;
    private int orderedQuantity = 1;
    private int stockQuantity;
    private int maxStockCapacity; // Track maximum stock for percentage calculation

    /**
     * Primary constructor used by DatabaseRW.
     * Automatically sets maxStockCapacity to initial stock quantity.
     */
    public Product(String id, String name, String des, String image, double aPrice, int stockQuantity) {
        this.proId = id;
        this.productName = name;
        this.proDescription = des;
        this.proImageName = image;
        this.unitPrice = aPrice;
        this.stockQuantity = stockQuantity;
        this.maxStockCapacity = stockQuantity; // Initialize max capacity
    }

    /**
     * Backward compatibility constructor (for existing code)
     * Uses description as product name
     */
    public Product(String id, String des, String image, double aPrice, int stockQuantity) {
        this(id, des, des, image, aPrice, stockQuantity); // Use description as both name and description
    }

    /**
     * Extended constructor with explicit max stock capacity.
     * Useful when restocking products with known maximum capacity.
     */
    public Product(String id, String name, String des, String image, double aPrice,
                   int stockQuantity, int maxStockCapacity) {
        this.proId = id;
        this.productName = name;
        this.proDescription = des;
        this.proImageName = image;
        this.unitPrice = aPrice;
        this.stockQuantity = stockQuantity;
        this.maxStockCapacity = maxStockCapacity;
    }

    // ========== GETTERS ==========
    public String getProductId() { return proId; }
    public String getProductDescription() { return proDescription; }
    public String getProductImageName() { return proImageName; }
    public double getUnitPrice() { return unitPrice; }
    public int getOrderedQuantity() { return orderedQuantity; }
    public int getStockQuantity() { return stockQuantity; }
    public int getMaxStockCapacity() { return maxStockCapacity; }

    // ========== SETTERS ==========
    public void setOrderedQuantity(int orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    // ========== STOCK LEVEL METHODS ==========

    /**
     * Gets the current stock level status.
     * Delegates to StockLevelIndicator for calculation.
     * @return StockLevel enum (CRITICAL, LOW, MEDIUM, HIGH)
     */
    public StockLevel getStockLevel() {
        return StockLevelIndicator.determineStockLevel(stockQuantity, maxStockCapacity);
    }
    /**
     * Gets the stock percentage (0.0 to 1.0).
     * Stock percentage as decimal
     */
    public double getStockPercentage() {
        return StockLevelIndicator.calculateStockPercentage(stockQuantity, maxStockCapacity);
    }
    /**
     * Gets formatted stock status text for display.
     * Human-readable stock status
     */
    public String getStockStatusText() {
        StockLevel level = getStockLevel();
        return level.getDisplayText();
    }

    @Override
    public int compareTo(Product otherProduct) {
        return this.proId.compareTo(otherProduct.proId);
    }

    @Override
    public String toString() {
        // Updated to include product name
        return String.format("ID: %s - %s, £%.2f/unit, stock: %d (%s)\nDescription: %s",
                proId,
                productName,
                unitPrice,
                stockQuantity,
                getStockStatusText(),
                proDescription
        );
    }


    /**
     * Clone method for creating copies of products
     * A new Product instance with the same values
     */
    public Product clone() {
        return new Product(proId, productName, proDescription, proImageName,
                unitPrice, stockQuantity, maxStockCapacity);
    }
}