package ci553.happyshop.discount;

public interface DiscountStrategy {
    /**
     * @param total the original order total
     * @return the discounted total
      */
    double applyDiscount(double total);
}
