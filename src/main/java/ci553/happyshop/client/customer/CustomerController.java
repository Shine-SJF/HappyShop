package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import java.io.IOException;
import java.sql.SQLException;

public class CustomerController {
    public CustomerModel cusModel;

    public void doAction(String action) throws SQLException, IOException {
        switch (action) {
            case "Search":
                cusModel.search();
                break;
            case "Add to Trolley":
                cusModel.addToTrolley();
                break;
            case "Cancel":
                cusModel.cancel();
                break;
            case "Check Out":
                cusModel.checkOut();
                break;
            case "OK & Close":
                cusModel.closeReceipt();
                break;
        }
    }

    // This method name must match what CustomerView is calling
    public void changeQuantity(Product product, int delta) throws SQLException, IOException {
        if (cusModel != null) {
            // Call the model's method (note: it's changeProductQuantity, not changeQuantity)
            cusModel.changeProductQuantity(product, delta);
        }
    }
}