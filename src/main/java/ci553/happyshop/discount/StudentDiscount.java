package ci553.happyshop.discount;

//Discount strategy for Student customers
public class StudentDiscount implements DiscountStrategy {
    private static final double DISCOUNT_RATE = 0.10; // 10%
    @Override
    public double applyDiscount(double total) {
        return total - (total * DISCOUNT_RATE);
    }
}
