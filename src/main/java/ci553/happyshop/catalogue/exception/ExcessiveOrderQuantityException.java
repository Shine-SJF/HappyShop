package ci553.happyshop.catalogue.exception;

public class ExcessiveOrderQuantityException extends RuntimeException {
    public ExcessiveOrderQuantityException(String message) {
        super(message);
    }
}
