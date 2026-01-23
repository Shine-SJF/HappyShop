package ci553.happyshop.discount;

/**
 * NoDiscount is a concrete DiscountStrategy representing the default case.
 * - This Applies no discount and return the original total unchanged.
 * Design:
 * - Useful as a safe default strategy to avoid null handling.
 */

//Default Strategy that applies no discount for the customer
public class NoDiscount implements DiscountStrategy {

    public double applyDiscount(double total) {
        return total;
    }
}
