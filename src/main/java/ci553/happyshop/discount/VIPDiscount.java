package ci553.happyshop.discount;

/**
 * VIPDiscount is a concrete DiscountStrategy for VIP customers.
 * - This appleis the VIP discount rule to the given total.
 * Design:
 * - Concrete strategy within the Strategy pattern family.
 */

//Discount strategy for VIP customers
public class VIPDiscount implements DiscountStrategy {
    private static final double DISCOUNT_RATE = 0.20; // 20%

    public double applyDiscount(double total) {
        return total - (total * DISCOUNT_RATE);
    }
}

