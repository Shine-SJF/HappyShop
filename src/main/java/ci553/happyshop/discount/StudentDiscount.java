package ci553.happyshop.discount;

/**
 * StudentDiscount is a concrete DiscountStrategy for student customers.
 * - This applies the student discount rule to the given total.
 * Design notes:
 * - Concrete strategy within the Strategy pattern family.
 */

//Discount strategy for Student customers
public class StudentDiscount implements DiscountStrategy {
    private static final double DISCOUNT_RATE = 0.10; // 10%

    public double applyDiscount(double total) {
        return total - (total * DISCOUNT_RATE);
    }
}
