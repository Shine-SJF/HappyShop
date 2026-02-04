package ci553.happyshop.tests;

import ci553.happyshop.catalogue.Product;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductTest {

    // test product is built correctly
    @Test
    public void addProductTest() throws Exception {
        Product p = new Product("0009", "ProductTest", "imageHolder.jpg", 2.00, 200);
        assertEquals("0009", p.getProductId());
        assertEquals("ProductTest", p.getProductDescription());
        assertEquals("imageHolder.jpg", p.getProductImageName());
        assertEquals(200, p.getStockQuantity());
    }

    @Test
    public void setQuantityTest() throws Exception {
        Product p = new Product("0009", "ProductTest", "imageHolder.jpg", 2.00, 200);
        p.setOrderedQuantity(6);
        assertEquals(6, p.getOrderedQuantity());
    }

    @Test
    public void getQuantityTest() throws Exception {
        Product p = new Product("0009", "ProductTest", "imageHolder.jpg", 2.00, 200);
        p.setOrderedQuantity(2);
        assertEquals(2, p.getOrderedQuantity());
    }

}
