package ci553.happyshop.client.customer;

import ci553.happyshop.atm.Bank;
import ci553.happyshop.payment.AtmPaymentAdapter;
import ci553.happyshop.payment.PaymentService;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.storageAccess.DatabaseRWFactory;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * CustomerClient (standalone)
 *
 * A standalone Customer Client that can be run independently without launching the full system.
 * Designed for early-stage testing (full order flow may require other clients to be active).
 *
 * Adapter Pattern note:
 * - This client configures PaymentService using AtmPaymentAdapter (ATM system reused without modification).
 * - CustomerModel depends only on PaymentService interface.
 */
public class CustomerClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates the Model, View, and Controller objects and links them together for communication.
     * Also creates DatabaseRW via DatabaseRWFactory and injects it into the CustomerModel.
     * Configures PaymentService via AtmPaymentAdapter and injects it into the model.
     */
    @Override
    public void start(Stage window) {

        CustomerView cusView = new CustomerView();
        CustomerController cusController = new CustomerController();

        DatabaseRW databaseRW = DatabaseRWFactory.createDatabaseRW();

        Bank bank = new Bank();
        bank.addBankAccount(1234, 1111, 500, "current");
        PaymentService paymentService = new AtmPaymentAdapter(bank, 1234, 1111);

        CustomerModel cusModel = new CustomerModel();
        cusModel.setPaymentService(paymentService);

        cusView.cusController = cusController;
        cusController.cusModel = cusModel;

        cusModel.cusView = cusView;
        cusModel.databaseRW = databaseRW;

        cusView.start(window);
    }
    }

