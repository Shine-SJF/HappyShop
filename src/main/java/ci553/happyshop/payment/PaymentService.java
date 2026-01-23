package ci553.happyshop.payment;

public interface PaymentService {
    void pay(int amount) throws PaymentException;
}

