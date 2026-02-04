package ci553.happyshop.tests;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.orderManagement.OrderState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static ci553.happyshop.orderManagement.OrderState.Ordered;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderTest {
    private Order order;
    private ArrayList<Product> products;

    @Before
    public void setUp() throws Exception {
        products = new ArrayList<>();
        products.add(new Product("001", "ProductTest", "imageHolder.jpg", 2.00, 200));
        products.add(new Product("002", "ProductTest", "imageHolder.jpg", 2.00, 200));

        order = new Order(1, Ordered, "2025-11-13 12:32:12", products);
    }

    @Test
    public void getOrderTest() throws Exception {
        assertEquals(1, order.getOrderId());
        assertEquals(Ordered, order.getState());
        assertEquals("2025-11-13 12:32:12", order.getOrderedDateTime());
        assertEquals(products, order.getProductList());
    }
}
