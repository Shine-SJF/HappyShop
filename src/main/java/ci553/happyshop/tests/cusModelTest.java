package ci553.happyshop.tests;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.client.customer.CustomerModel;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class cusModelTest {


    @Test
    public void checkOutTest() throws Exception {
        Product p = new Product("0009", "ProductTest", "imageHolder.jpg", 2.00, 200);
        CustomerModel cusModel = new CustomerModel();
        ArrayList<Product> trolley = new ArrayList<>();
        trolley.add(p);
    }


}
