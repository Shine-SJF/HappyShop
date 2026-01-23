package ci553.happyshop.client.customer;

import java.io.IOException;
import java.sql.SQLException;

import ci553.happyshop.catalogue.exception.ExcessiveOrderQuantityException;
import ci553.happyshop.catalogue.exception.UnderMinimumPaymentException;

/**
 * CustomerController
 *
 * This class just sits between the buttons in the UI and the CustomerModel.
 * It doesn't do the main logic itself (that stays in the model).
 */
public class CustomerController {

    public CustomerModel cusModel;

    // Item-level trolley controls

    public void removeItemFromTrolley(String productId) {
        cusModel.removeItemFromTrolley(productId);
    }

    public void decreaseItemQuantity(String productId) {
        cusModel.decreaseItemQuantity(productId);
    }

    public void increaseItemQuantity(String productId) {
        cusModel.increaseItemQuantity(productId);
    }

    // Main button actions

    public void doAction(String action) throws SQLException, IOException {
        switch (action) {
            case "Search" -> cusModel.search();

            case "Add to Trolley" -> cusModel.addToTrolley();

            case "Cancel" -> cusModel.cancel();

            case "Check Out" -> {
                try {
                    cusModel.checkOut();
                } catch (UnderMinimumPaymentException e) {
                    // Model already shows a popup, so this is mainly for debugging
                    System.out.println("Checkout failed (min payment): " + e.getMessage());
                } catch (ExcessiveOrderQuantityException e) {
                    // Model already adjusts and notifies the user
                    System.out.println("Checkout failed (quantity limit): " + e.getMessage());
                }
            }

            case "OK & Close" -> cusModel.closeReceipt();
        }
    }
}
