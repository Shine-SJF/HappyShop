package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.payment.PaymentException;
import ci553.happyshop.payment.PaymentService;
import ci553.happyshop.storageAccess.DatabaseRW;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * CustomerModelFeatureTest
 * These tests focus on the business logic I added
 * I intentionally avoid full GUI testing here
 * Instead, I initialise only the small JavaFX controls that CustomerModel reads (TextField + ListView),
 * then verify changes via the model state (trolley contents) and the view messages recorded by a small test stub.
 */
public class CustomerModelFeatureTest {

    private CustomerModel model;
    private TestCustomerView view;
    private FakeDatabaseRW db;
    private FakePaymentService payment;

    // JavaFX toolkit setup
    @BeforeAll
    static void startJavaFxToolkit() throws Exception {
        // JavaFX can only be started once per JVM
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            latch.countDown();
        }
        assertTrue(latch.await(3, TimeUnit.SECONDS), "JavaFX toolkit did not start in time");
    }

    @BeforeEach
    void setUp() throws Exception {
        model = new CustomerModel();
        view = new TestCustomerView();
        db = new FakeDatabaseRW();
        payment = new FakePaymentService();

        // Wire dependencies
        model.cusView = view;
        model.databaseRW = db;
        model.setPaymentService(payment);

        // Creates the minimal JavaFX controls on the FX thread
        runFxAndWait(view::initControls);
    }

   // Feature 1: Search by Product ID OR Name (id:xxxx / name:keyword)

    @Test
    void search_idMode_filtersExactProductId() throws Exception {
        // Arrange: the "database" returns two products
        db.searchResults = new ArrayList<>(List.of(
                product("0001", "Milk", 1.20, 10),
                product("0002", "Bread", 0.99, 10)
        ));

        // Search specifically by id:
        runFxAndWait(() -> view.tfId.setText("id:0002"));
        model.search();

        // Only the matching product is displayed
        assertEquals(1, view.productList.size());
        assertEquals("0002", view.productList.get(0).getProductId());
        assertTrue(view.lastSearchLabel.contains("product(s) found"));
    }

    @Test
    void search_nameMode_filtersByDescriptionContainsIgnoreCase() throws Exception {
        // Arrange
        db.searchResults = new ArrayList<>(List.of(
                product("0001", "Semi Skimmed Milk", 1.20, 10),
                product("0002", "Bread", 0.99, 10)
        ));

        // Search by keyword in description
        runFxAndWait(() -> view.tfId.setText("name:milk"));
        model.search();

        // Assert
        assertEquals(1, view.productList.size());
        assertEquals("0001", view.productList.get(0).getProductId());
    }
    // Feature 2: Sorting

    @Test
    void addToTrolley_sortsTrolleyByProductId() throws Exception {
        Product p2 = product("0002", "Bread", 0.99, 10);
        Product p1 = product("0001", "Milk", 1.20, 10);

        // Add 0002 first
        runFxAndWait(() -> {
            view.productList.setAll(p2);
            view.lvSearchResults.getSelectionModel().select(0);
        });
        model.addToTrolley();

        // Then 0001
        runFxAndWait(() -> {
            view.productList.setAll(p1);
            view.lvSearchResults.getSelectionModel().select(0);
        });
        model.addToTrolley();

        // The trolley should be sorted automatically after each add
        assertEquals(2, model.getTrolley().size());
        assertEquals("0001", model.getTrolley().get(0).getProductId());
        assertEquals("0002", model.getTrolley().get(1).getProductId());
    }

    // Feature 3: Stock realism (cannot add more than available stock)
    @Test
    void addToTrolley_preventsAddingBeyondStock() throws Exception {
        // Arrange: stock is only 1
        Product p = product("0001", "Milk", 1.20, 1);

        runFxAndWait(() -> {
            view.productList.setAll(p);
            view.lvSearchResults.getSelectionModel().select(0);
        });

        // Act: add twice
        model.addToTrolley(); // qty becomes 1
        model.addToTrolley(); // should be rejected

        // Still only 1 in trolley and quantity stayed at 1
        assertEquals(1, model.getTrolley().size());
        assertEquals(1, model.getTrolley().get(0).getOrderedQuantity());
        assertTrue(view.lastSearchLabel.toLowerCase().contains("insufficient stock"));
    }

    @Test
    void addToTrolley_mergesSameProductAndIncrementsQuantity() throws Exception {
        // Arrange: stock is 5 so merging should work
        Product p = product("0001", "Milk", 1.20, 5);

        runFxAndWait(() -> {
            view.productList.setAll(p);
            view.lvSearchResults.getSelectionModel().select(0);
        });

        // Add twice
        model.addToTrolley();
        model.addToTrolley();

        // Only one line item, quantity increased to 2
        assertEquals(1, model.getTrolley().size());
        assertEquals(2, model.getTrolley().get(0).getOrderedQuantity());
    }

    // Feature 4: Minimum spend validation
    // In my CustomerModel implementation, the minimum payment is enforced inside validatePayment(...)
    // and is set to £5.00.
    // validatePayment is private, so for unit testing I call it via reflection.
    @Test
    void validatePayment_throwsWhenBelowMinimum_usingReflection() throws Exception {
        var m = CustomerModel.class.getDeclaredMethod("validatePayment", double.class);
        m.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> m.invoke(model, 4.99));
        assertNotNull(ex.getCause(), "Reflection should wrap the underlying exception as getCause()");
        assertEquals("UnderMinimumPaymentException", ex.getCause().getClass().getSimpleName());
    }

    @Test
    void validatePayment_allowsWhenAtMinimum_usingReflection() throws Exception {
        var m = CustomerModel.class.getDeclaredMethod("validatePayment", double.class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(model, 5.00));
    }

    private static Product product(String id, String desc, double price, int stockQty) {
        return new Product(id, desc, "imageHolder.jpg", price, stockQty);
    }

    /**
     * Runs code on the JavaFX Application Thread and waits.
     * This makes tests stable when interacting with JavaFX controls (TextField/ListView).
     */
    private static void runFxAndWait(Runnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(3, TimeUnit.SECONDS), "FX action timed out");
    }

    // Test doubles
    /**
     * Fake DB: returns a preset list of products so tests don’t need a live Derby DB.
     * To keep tests fast and repeatable
     */
    static class FakeDatabaseRW implements DatabaseRW {
        ArrayList<Product> searchResults = new ArrayList<>();

        @Override
        public ArrayList<Product> searchProduct(String keyword) throws SQLException {
            return searchResults;
        }

        @Override
        public Product searchByProductId(String productId) throws SQLException {
            return searchResults.stream()
                    .filter(p -> p.getProductId().equalsIgnoreCase(productId))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public ArrayList<Product> purchaseStocks(ArrayList<Product> proList) throws SQLException {
            return new ArrayList<>();
        }

        @Override public void updateProduct(String id, String des, double price, String imageName, int stock) { throw new UnsupportedOperationException(); }
        @Override public void deleteProduct(String id) { throw new UnsupportedOperationException(); }
        @Override public void insertNewProduct(String id, String des, double price, String image, int stock) { throw new UnsupportedOperationException(); }
        @Override public boolean isProIdAvailable(String productId) { throw new UnsupportedOperationException(); }
    }

    /**
     * Fake payment service: included for completeness
     */
    static class FakePaymentService implements PaymentService {
        boolean called;
        int lastAmount;
        boolean fail;

        @Override
        public void pay(int amount) throws PaymentException {
            called = true;
            lastAmount = amount;
            if (fail) throw new PaymentException("Fail (test)");
        }
    }

    /**
     * TestCustomerView:
     * CustomerModel reads tfId + lvSearchResults + productList.
     * I override update(...) to capture the message text without requiring the full UI to be initialised.
     */
    static class TestCustomerView extends CustomerView {

        String lastSearchLabel = "";

        void initControls() {
            this.tfId = new TextField();
            this.productList = FXCollections.observableArrayList();
            this.lvSearchResults = new ListView<>(productList);
        }

        @Override
        public void update(String imageName, String searchResult, String trolley, String receipt) {
            this.lastSearchLabel = searchResult;
        }
    }
}
