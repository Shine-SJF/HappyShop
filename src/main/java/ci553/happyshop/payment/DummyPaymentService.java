package ci553.happyshop.payment;

import javafx.scene.control.Alert;

/**
 * Simplified payment service (for the OCP lab).
 * Always succeeds (basic validation only).
 */
public class DummyPaymentService implements PaymentService {

    @Override
    public void pay(int amount) throws PaymentException {
        if (amount <= 0) {
            throw new PaymentException("Amount must be > 0");
        }

        // Minimal UI feedback so it's obvious something happened
        Alert ok = new Alert(Alert.AlertType.INFORMATION);
        ok.setTitle("Payment (Dummy)");
        ok.setHeaderText(null);
        ok.setContentText("Dummy payment accepted for £" + amount + ".");
        ok.showAndWait();
    }
}
