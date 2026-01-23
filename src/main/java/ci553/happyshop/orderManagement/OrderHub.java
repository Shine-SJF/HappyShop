package ci553.happyshop.orderManagement;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.client.orderTracker.OrderTracker;
import ci553.happyshop.client.picker.PickerModel;
import ci553.happyshop.storageAccess.OrderFileManager;
import ci553.happyshop.utility.StorageLocation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * OrderHub is the central class that manages all orders in the system.
 *
 * There is only one OrderHub for the whole application (Singleton),
 * and it is responsible for:
 *  - creating new orders
 *  - keeping track of order states
 *  - notifying other parts of the system when orders change
 *
 * It already used a custom observer approach (OrderTracker and PickerModel),
 * and has been extended to also support Java's PropertyChangeSupport.
 */
public class OrderHub {

    // Singleton instance
    private static OrderHub orderHub;

    // File system paths for order storage
    private final Path orderedPath = StorageLocation.orderedPath;
    private final Path progressingPath = StorageLocation.progressingPath;
    private final Path collectedPath = StorageLocation.collectedPath;

    // Java built-in observer support (extension)
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    // Main order state map: orderId -> state
    private final TreeMap<Integer, OrderState> orderMap = new TreeMap<>();

    // Helper maps used for filtering
    private TreeMap<Integer, OrderState> orderedOrderMap = new TreeMap<>();
    private TreeMap<Integer, OrderState> progressingOrderMap = new TreeMap<>();

    // Existing observers used by the system
    private final ArrayList<OrderTracker> orderTrackerList = new ArrayList<>();
    private final ArrayList<PickerModel> pickerModelList = new ArrayList<>();

    // Used to remove collected orders after a short delay
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    // Private constructor for Singleton
    private OrderHub() {}

    // Access point for the Singleton
    public static OrderHub getOrderHub() {
        if (orderHub == null) {
            orderHub = new OrderHub();
        }
        return orderHub;
    }


    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    // Called whenever orderMap changes so listeners get notified
    private void fireOrderMapChanged() {
        // Send a copy so listeners don't accidentally modify internal state
        pcs.firePropertyChange("orderMap", null, new TreeMap<>(orderMap));
    }

    public void registerOrderTracker(OrderTracker orderTracker) {
        orderTrackerList.add(orderTracker);
    }

    public void registerPickerModel(PickerModel pickerModel) {
        pickerModelList.add(pickerModel);
    }

    // Notify all order trackers with the full order map
    public void notifyOrderTrackers() {
        for (OrderTracker tracker : orderTrackerList) {
            tracker.setOrderMap(orderMap);
        }
    }

    // Notify pickers with only ordered + progressing orders
    public void notifyPickerModels() {
        TreeMap<Integer, OrderState> mapForPicker = new TreeMap<>();

        progressingOrderMap = filterOrdersByState(OrderState.Progressing);
        orderedOrderMap = filterOrdersByState(OrderState.Ordered);

        mapForPicker.putAll(progressingOrderMap);
        mapForPicker.putAll(orderedOrderMap);

        for (PickerModel picker : pickerModelList) {
            picker.setOrderMap(mapForPicker);
        }
    }

    // Helper method to filter orders by state
    private TreeMap<Integer, OrderState> filterOrdersByState(OrderState state) {
        TreeMap<Integer, OrderState> filtered = new TreeMap<>();
        for (Map.Entry<Integer, OrderState> entry : orderMap.entrySet()) {
            if (entry.getValue() == state) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }


    // Creates a new order and stores it as "Ordered"
    public Order newOrder(ArrayList<Product> trolley)
            throws IOException, SQLException {

        int orderId = OrderCounter.generateOrderId();
        String orderedTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Order order = new Order(orderId, OrderState.Ordered, orderedTime, trolley);

        // Write order file to disk
        OrderFileManager.createOrderFile(
                orderedPath, orderId, order.orderDetails());

        // Update state and notify observers
        orderMap.put(orderId, order.getState());
        fireOrderMapChanged();
        notifyOrderTrackers();
        notifyPickerModels();

        return order;
    }

    // Changes the state of an order and moves its file
    public void changeOrderStateMoveFile(int orderId, OrderState newState)
            throws IOException {

        if (orderMap.containsKey(orderId)
                && !orderMap.get(orderId).equals(newState)) {

            orderMap.put(orderId, newState);
            fireOrderMapChanged();
            notifyOrderTrackers();
            notifyPickerModels();

            switch (newState) {
                case Progressing ->
                        OrderFileManager.updateAndMoveOrderFile(
                                orderId, newState, orderedPath, progressingPath);

                case Collected -> {
                    OrderFileManager.updateAndMoveOrderFile(
                            orderId, newState, progressingPath, collectedPath);
                    removeCollectedOrder(orderId);
                }
            }
        }
    }

    // Removes collected orders after a short delay
    private void removeCollectedOrder(int orderId) {
        if (orderMap.containsKey(orderId)) {
            scheduler.schedule(() -> {
                orderMap.remove(orderId);
                fireOrderMapChanged();
                notifyOrderTrackers();
            }, 10, TimeUnit.SECONDS);
        }
    }

    // Reads order details for picker view
    public String getOrderDetailForPicker(int orderId) throws IOException {
        if (orderMap.get(orderId) == OrderState.Progressing) {
            return OrderFileManager.readOrderFile(progressingPath, orderId);
        }
        return "This function is only used by pickers.";
    }

    // Loads existing orders from disk on startup
    public void initializeOrderMap() {
        ArrayList<Integer> orderedIds = orderIdsLoader(orderedPath);
        ArrayList<Integer> progressingIds = orderIdsLoader(progressingPath);

        for (Integer id : orderedIds) {
            orderMap.put(id, OrderState.Ordered);
        }
        for (Integer id : progressingIds) {
            orderMap.put(id, OrderState.Progressing);
        }

        fireOrderMapChanged();
        notifyOrderTrackers();
        notifyPickerModels();
    }

    // Reads order IDs from a directory
    private ArrayList<Integer> orderIdsLoader(Path dir) {
        ArrayList<Integer> orderIds = new ArrayList<>();

        if (Files.exists(dir) && Files.isDirectory(dir)) {
            try (Stream<Path> files = Files.list(dir)) {
                for (Path file : files.filter(Files::isRegularFile).toList()) {
                    String name = file.getFileName().toString();
                    if (name.endsWith(".txt")) {
                        try {
                            orderIds.add(
                                    Integer.parseInt(name.replace(".txt", "")));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading orders: " + e.getMessage());
            }
        }
        return orderIds;
    }
}
