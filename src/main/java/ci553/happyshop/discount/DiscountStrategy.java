package ci553.happyshop.discount;

/**
 * DiscountStrategy defines a family of discount algorithms that can be applied
 * to a base price or total.
 * Design:
 * - Strategy Pattern: allows discount behaviour to be selected at runtime.
 * Advantage:
 * - Supports the Open/Closed Principle by allowing new discount types to be added
 *   without changing checkout logic.
 */

public interface DiscountStrategy {
}


public interface DiscountStrategy {
    /**
     * @param total the original order total
     * @return the discounted total
      */
    double applyDiscount(double total);
}
