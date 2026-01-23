package ci553.happyshop.payment;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * "Dummy" ATM-style payment UI (for the OCP lab):
 * - Shows ATM login prompt (account + PIN)
 * - Only checks inputs are not empty
 * - Accepts any account/PIN
 * - No balance or real account validation
 */
public class SimpleAtmPaymentUiAdapter implements PaymentService {

    @Override
    public void pay(int amount) throws PaymentException {
        if (amount <= 0) {
            throw new PaymentException("Amount must be > 0");
        }

        // Show ATM-like dialog (but simplified)
        Credentials creds = promptForCredentials(amount);

        // Minimal "validation": only non-empty fields
        if (creds.accountNumber.isBlank() || creds.pin.isBlank()) {
            throw new PaymentException("Account number and PIN cannot be empty.");
        }

        // Always accept (no bank/balance checks)
        Alert ok = new Alert(Alert.AlertType.INFORMATION);
        ok.setTitle("Payment Successful");
        ok.setHeaderText(null);
        ok.setContentText("Payment of £" + amount + " accepted.\n(Account: " + creds.accountNumber + ")");
        ok.showAndWait();
    }

    private Credentials promptForCredentials(int amount) throws PaymentException {
        Dialog<Credentials> dialog = new Dialog<>();
        dialog.setTitle("ATM Payment");
        dialog.setHeaderText("Pay £" + amount + " using ATM");

        ButtonType payButtonType = new ButtonType("Pay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(payButtonType, ButtonType.CANCEL);

        TextField accField = new TextField();
        accField.setPromptText("Account number");

        PasswordField pinField = new PasswordField();
        pinField.setPromptText("PIN");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Account number:"), 0, 0);
        grid.add(accField, 1, 0);
        grid.add(new Label("PIN:"), 0, 1);
        grid.add(pinField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Disable Pay until both fields have something typed
        Button payButton = (Button) dialog.getDialogPane().lookupButton(payButtonType);
        payButton.setDisable(true);

        accField.textProperty().addListener((obs, oldV, newV) ->
                payButton.setDisable(newV.trim().isEmpty() || pinField.getText().trim().isEmpty())
        );
        pinField.textProperty().addListener((obs, oldV, newV) ->
                payButton.setDisable(newV.trim().isEmpty() || accField.getText().trim().isEmpty())
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == payButtonType) {
                return new Credentials(accField.getText().trim(), pinField.getText().trim());
            }
            return null;
        });

        Optional<Credentials> result = dialog.showAndWait();
        if (result.isEmpty()) {
            throw new PaymentException("Payment cancelled.");
        }
        return result.get();
    }

    private static class Credentials {
        final String accountNumber;
        final String pin;

        Credentials(String accountNumber, String pin) {
            this.accountNumber = accountNumber;
            this.pin = pin;
        }
    }
}
