package ci553.happyshop.client.customer;

import java.io.IOException;
import java.sql.SQLException;

//import ci553.happyshop.discount.NoDiscount;
//import ci553.happyshop.discount.StudentDiscount;
//import ci553.happyshop.discount.VIPDiscount;

public class CustomerController {
    public CustomerModel cusModel;
    private CustomerView cusView;


    public void setView(CustomerView view) {
        this.cusView = view;
    }

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
            default:
                break;
        }
    }
}

