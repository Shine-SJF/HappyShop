package ci553.happyshop.catalogue.exception;

public class UnderMinimumPaymentException extends RuntimeException {
    public UnderMinimumPaymentException(String message) {
        super(message);
    }
}
