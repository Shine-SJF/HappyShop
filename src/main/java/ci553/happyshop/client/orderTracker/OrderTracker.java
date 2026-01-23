package ci553.happyshop.client.orderTracker;

import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.orderManagement.OrderState;
import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WinPosManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeMap;

/**
 * OrderTracker displays the current order map (orderId -> state).
 * It receives updates from OrderHub whenever an order is created or changes state.
 *
 * This version supports:
 *  - the original HappyShop observer method (registerOrderTracker)
 *  - Java's PropertyChangeSupport mechanism (PropertyChangeListener)
 *
 * Keeping both makes it easy to extend without breaking the existing system.
 */
public class OrderTracker implements PropertyChangeListener {

    private final int WIDTH = UIStyle.trackerWinWidth;
    private final int HEIGHT = UIStyle.trackerWinHeight;

    // Keeps a local copy of the order map for display
    private static final TreeMap<Integer, OrderState> ordersMap = new TreeMap<>();

    private final TextArea taDisplay;

    public OrderTracker() {
        Label laTitle = new Label("Order_ID,  State");
        laTitle.setStyle(UIStyle.labelTitleStyle);

        taDisplay = new TextArea();
        taDisplay.setEditable(false);
        taDisplay.setStyle(UIStyle.textFiledStyle);

        VBox vbox = new VBox(10, laTitle, taDisplay);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setStyle(UIStyle.rootStyleGray);

        Scene scene = new Scene(vbox, WIDTH, HEIGHT);
        Stage window = new Stage();
        window.setScene(scene);
        window.setTitle("🛒Order Tracker");

        WinPosManager.registerWindow(window, WIDTH, HEIGHT);
        window.show();
    }

    /**
     * Registers this OrderTracker with OrderHub.
     * We register using both approaches:
     * - the original observer list (existing system behaviour)
     * - PropertyChangeSupport (extension from lectures)
     */
    public void registerWithOrderHub() {
        OrderHub orderHub = OrderHub.getOrderHub();

        // Existing observer mechanism
        orderHub.registerOrderTracker(this);

        // Extension: Java built-in observer mechanism
        orderHub.addPropertyChangeListener(this);
    }

    /**
     * Existing update method used by the original observer approach.
     */
    public void setOrderMap(TreeMap<Integer, OrderState> om) {
        ordersMap.clear();
        ordersMap.putAll(om);
        displayOrderMap();
    }

    /**
     * PropertyChangeListener update method (called automatically when OrderHub fires changes).
     * We simply reuse the existing setOrderMap(...) method to refresh the UI.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"orderMap".equals(evt.getPropertyName())) {
            return;
        }

        @SuppressWarnings("unchecked")
        TreeMap<Integer, OrderState> updated =
                (TreeMap<Integer, OrderState>) evt.getNewValue();

        if (updated != null) {
            setOrderMap(updated);
        }
    }

    private void displayOrderMap() {
        StringBuilder sb = new StringBuilder();
        boolean hasCollected = false;

        for (Map.Entry<Integer, OrderState> entry : ordersMap.entrySet()) {
            int orderId = entry.getKey();
            OrderState orderState = entry.getValue();

            if (orderState == OrderState.Collected) {
                hasCollected = true;
            }

            sb.append(orderId)
                    .append(" ".repeat(5))
                    .append(orderState)
                    .append("\n");
        }

        taDisplay.setText(sb.toString());

        // UX effect: turn text green when an order is collected
        if (hasCollected) {
            taDisplay.setStyle(UIStyle.textFiledStyle + "; -fx-text-fill: green;");
        } else {
            taDisplay.setStyle(UIStyle.textFiledStyle + "; -fx-text-fill: black;");
        }
    }

}
