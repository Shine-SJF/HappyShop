package ci553.happyshop.discount;

//Default Strategy that applies no discount for the customer
public class NoDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double total) {
        return total;
    }
}
