package ci553.happyshop.catalogue;

import ci553.happyshop.orderManagement.OrderState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    void orderStoresBasicInformationCorrectly() {
        ArrayList<Product> trolley = new ArrayList<>();
        trolley.add(new Product("0001", "Test item", "img.jpg", 2.50, 10));

        Order order = new Order(
                1,
                OrderState.Ordered,
                "2026-01-05 10:00:00",
                trolley
        );

        // Basic checks to make sure the constructor stored everything properly
        assertEquals(1, order.getOrderId());
        assertEquals(OrderState.Ordered, order.getState());
        assertEquals("2026-01-05 10:00:00", order.getOrderedDateTime());
        assertEquals(1, order.getProductList().size());
    }

    @Test
    void orderDetailsContainsImportantInformation() {
        ArrayList<Product> trolley = new ArrayList<>();
        trolley.add(new Product("0002", "Milk", "milk.jpg", 1.20, 10));
        trolley.add(new Product("0003", "Bread", "bread.jpg", 1.00, 10));

        Order order = new Order(
                99,
                OrderState.Ordered,
                "2026-01-05 11:00:00",
                trolley
        );

        String details = order.orderDetails();

        // Only check for key values, not exact formatting
        assertTrue(details.contains("99"));
        assertTrue(details.toLowerCase().contains("ordered"));
        assertTrue(details.contains("0002"));
        assertTrue(details.contains("0003"));
    }
}
