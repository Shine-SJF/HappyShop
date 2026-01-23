package ci553.happyshop.payment;

import ci553.happyshop.atm.Bank;

public class AtmPaymentAdapter implements PaymentService {

    private final Bank bank;
    private final int accNumber;
    private final int accPassword;

    public AtmPaymentAdapter(Bank bank, int accNumber, int accPassword) {
        this.bank = bank;
        this.accNumber = accNumber;
        this.accPassword = accPassword;
    }

    @Override
    public void pay(int amount) throws PaymentException {
        if (amount <= 0) throw new PaymentException("Amount must be > 0");

        if (!bank.login(accNumber, accPassword)) {
            throw new PaymentException("ATM login failed");
        }

        try {
            if (!bank.withdraw(amount)) {
                throw new PaymentException("Payment declined (insufficient funds)");
            }
        } finally {
            bank.logout();
        }
    }
}
