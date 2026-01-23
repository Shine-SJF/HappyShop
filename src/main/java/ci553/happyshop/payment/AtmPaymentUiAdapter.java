package ci553.happyshop.payment;

import ci553.happyshop.atm.Bank;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * UI-enabled adapter:
 * - Prompts for ATM credentials
 * - Checks balance before payment
 * - Shows remaining balance after payment
 */
public class AtmPaymentUiAdapter implements PaymentService {

    private final Bank bank;

    public AtmPaymentUiAdapter(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void pay(int amount) throws PaymentException {
        if (amount <= 0) throw new PaymentException("Amount must be > 0");

        Credentials creds = promptForCredentials(amount);

        // Login (ATM system requirement)
        if (!bank.login(creds.accNumber, creds.accPassword)) {
            showError("ATM Login Failed", "Incorrect account number or PIN.");
            throw new PaymentException("ATM login failed");
        }

        try {
            int balanceBefore = bank.getBalance();
            if (balanceBefore < 0) {
                showError("ATM Error", "Could not read balance.");
                throw new PaymentException("ATM error reading balance");
            }

            // ✅ Insufficient funds check (covers >£500 or any balance)
            if (amount > balanceBefore) {
                showError(
                        "Insufficient Funds",
                        "You tried to pay £" + amount + " but your balance is £" + balanceBefore + "."
                );
                throw new PaymentException("Insufficient funds");
            }

            // Withdraw (the actual payment)
            if (!bank.withdraw(amount)) {
                showError("Payment Declined", "Payment could not be processed.");
                throw new PaymentException("Payment declined");
            }

            int balanceAfter = bank.getBalance();

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Payment Successful");
            ok.setHeaderText(null);
            ok.setContentText("Paid £" + amount + ". Remaining balance: £" + balanceAfter + ".");
            ok.showAndWait();

        } finally {
            bank.logout();
        }
    }

    private void showError(String title, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
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
        pinField.setPromptText("PIN / password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Account number:"), 0, 0);
        grid.add(accField, 1, 0);
        grid.add(new Label("PIN:"), 0, 1);
        grid.add(pinField, 1, 1);

        dialog.getDialogPane().setContent(grid);

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
                try {
                    int acc = Integer.parseInt(accField.getText().trim());
                    int pin = Integer.parseInt(pinField.getText().trim());
                    return new Credentials(acc, pin);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Credentials> result = dialog.showAndWait();

        if (result.isEmpty()) {
            throw new PaymentException("Payment cancelled.");
        }
        if (result.get() == null) {
            throw new PaymentException("Invalid account number or PIN (must be numbers).");
        }
        return result.get();
    }

    private static class Credentials {
        final int accNumber;
        final int accPassword;

        Credentials(int accNumber, int accPassword) {
            this.accNumber = accNumber;
            this.accPassword = accPassword;
        }
    }
}
