package ci553.happyshop.discount;

//Discount strategy for VIP customers
public class VIPDiscount implements DiscountStrategy {
    private static final double DISCOUNT_RATE = 0.20; // 20%
    @Override
    public double applyDiscount(double total) {
        return total - (total * DISCOUNT_RATE);
    }
}

