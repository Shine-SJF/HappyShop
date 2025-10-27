package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
class CustomerModelTest {

    @Test
    void makingOrganisedTrolley() {
        CustomerModel cm = new CustomerModel();
        Product p = new Product("0001", "Tv", "0001.jpg", 12.01, 100);
        cm.setTheProduct(p);
        cm.MakingOrganisedTrolley();
        cm.MakingOrganisedTrolley();
        cm.MakingOrganisedTrolley();
        ArrayList<Product> tro = cm.getTrolley();
        assertEquals(1, tro.size());
        assertEquals(3, tro.get(0).getOrderedQuantity());
    }
}