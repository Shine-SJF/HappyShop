package ci553.happyshop.payment;

import ci553.happyshop.atm.Bank;

public class PaymentServiceFactory {

    public enum PaymentType {
        ATM,
        DUMMY
    }

    public static PaymentService createPaymentService(PaymentType type, Bank bank) {
        if (type == null) type = PaymentType.DUMMY;

        return switch (type) {
            case ATM -> new AtmPaymentUiAdapter(bank);          // real ATM (checks login + balance)
            case DUMMY -> new SimpleAtmPaymentUiAdapter();      // fake ATM UI (no real checks)
        };
    }
}
