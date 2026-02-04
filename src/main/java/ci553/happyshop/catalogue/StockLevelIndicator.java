package ci553.happyshop.catalogue;

/**
 * Strategy Pattern implementation for determining stock level status.
 * This class encapsulates the logic for calculating and categorizing stock levels,
 * demonstrating the Single Responsibility Principle (SRP).
 *
 * Features:
 * - Calculates stock percentage based on initial/maximum stock
 * - Categorizes stock into LOW, MEDIUM, HIGH levels
 * - Provides styling information for UI rendering
 *
 * Design Pattern: Strategy Pattern
 * - Encapsulates stock level calculation algorithm
 * - Allows easy extension for different stock level strategies
 */
public class StockLevelIndicator {

    // Stock level thresholds (easily configurable)
    private static final double LOW_STOCK_THRESHOLD = 0.10;  // 10%
    private static final double MEDIUM_STOCK_THRESHOLD = 0.30; // 30%

    /**
     * Enum representing different stock level categories.
     * Using enum provides type safety and clear semantics.
     */
    public enum StockLevel {
        CRITICAL("Critical", "Out of Stock", "#FF3B30", "#FFFFFF"),
        LOW("Low", "Low in Stock", "#FF9500", "#FFFFFF"),
        MEDIUM("Medium", "Limited Stock", "#FFCC00", "#000000"),
        HIGH("High", "In Stock", "#34C759", "#FFFFFF");

        private final String label;
        private final String displayText;
        private final String backgroundColor;
        private final String textColor;

        StockLevel(String label, String displayText, String bgColor, String txtColor) {
            this.label = label;
            this.displayText = displayText;
            this.backgroundColor = bgColor;
            this.textColor = txtColor;
        }

        public String getLabel() { return label; }
        public String getDisplayText() { return displayText; }
        public String getBackgroundColor() { return backgroundColor; }
        public String getTextColor() { return textColor; }
    }

    /**
     * Determines the stock level based on current stock and maximum capacity.
     *
     * Algorithm:
     * - 0% stock = CRITICAL
     * - 0% < stock <= 10% = LOW
     * - 10% < stock <= 30% = MEDIUM
     * - stock > 30% = HIGH
     *
     * currentStock Current quantity in stock
     * maxStock Maximum stock capacity (initial stock when product was added)
     * StockLevel enum representing the current stock status
     */
    public static StockLevel determineStockLevel(int currentStock, int maxStock) {
        if (currentStock <= 0) {
            return StockLevel.CRITICAL;
        }

        if (maxStock <= 0) {
            return StockLevel.HIGH; // Fallback if maxStock is invalid
        }

        double stockPercentage = (double) currentStock / maxStock;

        if (stockPercentage <= LOW_STOCK_THRESHOLD) {
            return StockLevel.LOW;
        } else if (stockPercentage <= MEDIUM_STOCK_THRESHOLD) {
            return StockLevel.MEDIUM;
        } else {
            return StockLevel.HIGH;
        }
    }

    /**
     * Calculates the stock percentage.
     *
     * currentStock Current quantity in stock
     * maxStock Maximum stock capacity
     * Stock percentage as a double (0.0 to 1.0)
     */
    public static double calculateStockPercentage(int currentStock, int maxStock) {
        if (maxStock <= 0) {
            return 0.0;
        }
        return (double) currentStock / maxStock;
    }

    /**
     * Gets JavaFX CSS style string for the stock level indicator.
     *  stockLevel The stock level to get styles for
     *  CSS style string for JavaFX components
     */
    public static String getStyleForLevel(StockLevel stockLevel) {
        return String.format(
                "-fx-background-color: %s; " +
                        "-fx-text-fill: %s; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 12px; " +
                        "-fx-padding: 4px 8px; " +
                        "-fx-background-radius: 4px;",
                stockLevel.getBackgroundColor(),
                stockLevel.getTextColor()
        );
    }

    /**
     * Gets console color code for terminal output (ANSI colors).
     * stockLevel The stock level
     * ANSI color code string
     */
    public static String getConsoleColorCode(StockLevel stockLevel) {
        switch (stockLevel) {
            case CRITICAL:
                return "\u001B[41m"; // Red background
            case LOW:
                return "\u001B[31m"; // Red text
            case MEDIUM:
                return "\u001B[33m"; // Yellow text
            case HIGH:
                return "\u001B[32m"; // Green text
            default:
                return "\u001B[0m";  // Reset
        }
    }

     //Resets console color.
    public static String resetConsoleColor() {
        return "\u001B[0m";
    }
}