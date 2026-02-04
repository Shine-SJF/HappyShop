package ci553.happyshop.utility;

import ci553.happyshop.catalogue.Product;

import java.util.ArrayList;

import ci553.happyshop.discount.DiscountStrategy;

import ci553.happyshop.discount.NoDiscount;


/**
 * This class builds a formatted, receipt-like summary from a list of products.
 * It is used by:
 * 1. CustomerModel – to display the trolley and receipt
 * 2. The Order class – to generate a summary for writing to an order's file
 */

public class ProductListFormatter {

    /**
     * Backwards-compatible version: no discount applied.
     */
    public static String buildString(ArrayList<Product> proList) {
        return buildString(proList, new NoDiscount());
    }
    /**
     * @param proList
     * @param strategy
     * @return
     */
    public static String buildString(ArrayList<Product> proList, DiscountStrategy strategy) {
        StringBuilder sb = new StringBuilder();
        double totalPrice = 0;
        for (Product pr : proList) {
            int orderedQuantity = pr.getOrderedQuantity();

            String aProduct = String.format(" %-7s %-18.18s (%2d) £%7.2f\n",
                    pr.getProductId(),
                    pr.getProductDescription(),
                    pr.getOrderedQuantity(),
                    pr.getUnitPrice() * orderedQuantity);

            sb.append(aProduct);
            totalPrice = totalPrice + pr.getUnitPrice() * orderedQuantity;
        }

        double discountedTotal = strategy.applyDiscount(totalPrice);
        String lineSeparator = "-".repeat(44) + "\n";
        String baseTotalLine = String.format(" %-35s £%7.2f\n", "Total (before discount)", totalPrice);
        String finalTotalLine = String.format(" %-35s £%7.2f\n", "Total (after discount)", discountedTotal);
        sb.append(lineSeparator);
        sb.append(baseTotalLine);
        sb.append(finalTotalLine);
        return sb.toString();
    }
}

