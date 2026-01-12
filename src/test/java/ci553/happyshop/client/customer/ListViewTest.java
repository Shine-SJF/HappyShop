/* package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ListViewTest {
    private CustomerModel cm;
    @BeforeEach
    void setUp() {
        cm = new CustomerModel();
        cm.cusView = new MockCustomerView();
    }

    @Test
    void testSelectProductUpdatesPreview(){
        CustomerModel cm = new CustomerModel();
        cm.cusView = new CustomerView();
        Product p1 = new Product("0001", "TV", "0001.jpg", 100, 10);
        Product p2 = new Product("0002", "Radio", "0002.jpg", 50, 12);

        cm.selectProduct(p2);

        assertEquals("0002", cm.getTheProduct().getProductId(), "The selected product id should match clicked item");

        assertTrue(cm.getDisplayLaSearchResult().contains("Radio"), "Should now show radio");
        assertTrue(cm.getDisplayLaSearchResult().contains("£50.00)"), "Price should show £50");
    }

    private static class MockCustomerView extends CustomerView{
        @Override
        public void update(String img, String res, String trol, String rec, ArrayList<Product> list) {}
    }

} */