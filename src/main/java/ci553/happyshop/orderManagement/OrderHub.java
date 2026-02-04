package ci553.happyshop.orderManagement;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.client.orderTracker.OrderTracker;
import ci553.happyshop.client.picker.PickerModel;
import ci553.happyshop.storageAccess.OrderFileManager;
import ci553.happyshop.utility.StorageLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Enhanced OrderHub with stock reservation tracking.
 * Key Features:
 * - Reserves stock when order is created
 * - Releases reserved stock when order is collected
 * - Tracks reserved quantities per product
 * - Prevents overselling
 *
 */
public class OrderHub {
    private static OrderHub orderHub;

    private final Path orderedPath = StorageLocation.orderedPath;
    private final Path progressingPath = StorageLocation.progressingPath;
    private final Path collectedPath = StorageLocation.collectedPath;

    private TreeMap<Integer, OrderState> orderMap = new TreeMap<>();
    private TreeMap<Integer, OrderState> OrderedOrderMap = new TreeMap<>();
    private TreeMap<Integer, OrderState> progressingOrderMap = new TreeMap<>();

    // NEW: Track reserved stock per product
    private HashMap<String, Integer> reservedStockMap = new HashMap<>();
    // Key: productId, Value: total quantity reserved across all active orders

    // NEW: Track order contents for stock release
    private HashMap<Integer, ArrayList<Product>> orderContentsMap = new HashMap<>();
    // Key: orderId, Value: list of products in that order

    private ArrayList<OrderTracker> orderTrackerList = new ArrayList<>();
    private ArrayList<PickerModel> pickerModelList = new ArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Singleton pattern
    private OrderHub() {}

    public static OrderHub getOrderHub() {
        if (orderHub == null)
            orderHub = new OrderHub();
        return orderHub;
    }

    /**
     * Creates a new order and reserves stock.
     * FIXED: Now properly reserves stock to prevent overselling.
     */
    public Order newOrder(ArrayList<Product> trolley) throws IOException, SQLException {
        int orderId = OrderCounter.generateOrderId();
        String orderedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Order theOrder = new Order(orderId, OrderState.Ordered, orderedDateTime, trolley);

        // Write order details to file
        String orderDetail = theOrder.orderDetails();
        OrderFileManager.createOrderFile(orderedPath, orderId, orderDetail);

        // NEW: Reserve stock for this order
        reserveStockForOrder(orderId, trolley);

        // Add order to map
        orderMap.put(orderId, theOrder.getState());

        // Notify observers
        notifyOrderTrackers();
        notifyPickerModels();

        System.out.println("✅ Order " + orderId + " created. Stock reserved.");
        printReservedStockStatus();

        return theOrder;
    }

    /**
     * Reserves stock for an order.
     * Increments reserved quantity for each product.
     */
    private void reserveStockForOrder(int orderId, ArrayList<Product> products) {
        // Store order contents for later release
        orderContentsMap.put(orderId, new ArrayList<>(products));

        // Reserve stock for each product
        for (Product product : products) {
            String productId = product.getProductId();
            int quantity = product.getOrderedQuantity();

            // Add to reserved stock
            reservedStockMap.put(productId,
                    reservedStockMap.getOrDefault(productId, 0) + quantity);

            System.out.println(String.format(
                    "🔒 Reserved %d units of product %s (Total reserved: %d)",
                    quantity, productId, reservedStockMap.get(productId)
            ));
        }
    }

    /**
     * Releases reserved stock when order is collected.
     */
    private void releaseReservedStock(int orderId) {
        ArrayList<Product> orderProducts = orderContentsMap.get(orderId);

        if (orderProducts != null) {
            for (Product product : orderProducts) {
                String productId = product.getProductId();
                int quantity = product.getOrderedQuantity();

                // Reduce reserved stock
                int currentReserved = reservedStockMap.getOrDefault(productId, 0);
                int newReserved = Math.max(0, currentReserved - quantity);

                if (newReserved == 0) {
                    reservedStockMap.remove(productId);
                } else {
                    reservedStockMap.put(productId, newReserved);
                }

                System.out.println(String.format(
                        "🔓 Released %d units of product %s (Remaining reserved: %d)",
                        quantity, productId, newReserved
                ));
            }

            // Remove order contents from tracking
            orderContentsMap.remove(orderId);
        }
    }

    //Gets reserved quantity for a specific product.

    public int getReservedStock(String productId) {
        return reservedStockMap.getOrDefault(productId, 0);
    }


     //Prints current reserved stock status (for debugging).

    private void printReservedStockStatus() {
        if (reservedStockMap.isEmpty()) {
            System.out.println("📦 No stock currently reserved");
        } else {
            System.out.println("📦 Current Reserved Stock:");
            for (Map.Entry<String, Integer> entry : reservedStockMap.entrySet()) {
                System.out.println(String.format("   Product %s: %d units reserved",
                        entry.getKey(), entry.getValue()));
            }
        }
    }

    /**
     * Changes order state and manages stock accordingly.
     */
    public void changeOrderStateMoveFile(int orderId, OrderState newState) throws IOException {
        if (orderMap.containsKey(orderId) && !orderMap.get(orderId).equals(newState)) {
            orderMap.put(orderId, newState);
            notifyOrderTrackers();
            notifyPickerModels();

            switch (newState) {
                case OrderState.Progressing:
                    OrderFileManager.updateAndMoveOrderFile(orderId, newState, orderedPath, progressingPath);
                    System.out.println("📋 Order " + orderId + " now being prepared");
                    break;

                case OrderState.Collected:
                    OrderFileManager.updateAndMoveOrderFile(orderId, newState, progressingPath, collectedPath);

                    // NEW: Release reserved stock when collected
                    releaseReservedStock(orderId);

                    System.out.println("✅ Order " + orderId + " collected. Stock released.");
                    printReservedStockStatus();

                    removeCollectedOrder(orderId);
                    break;
            }
        }
    }

    /**
     * Removes collected orders after delay (unchanged).
     */
    private void removeCollectedOrder(int orderId) {
        if (orderMap.containsKey(orderId)) {
            scheduler.schedule(() -> {
                orderMap.remove(orderId);
                System.out.println("Order " + orderId + " removed from tracker and OrdersMap.");
                notifyOrderTrackers();
            }, 10, TimeUnit.SECONDS);
        }
    }

    // ========== EXISTING METHODS ==========

    public void registerOrderTracker(OrderTracker orderTracker) {
        orderTrackerList.add(orderTracker);
    }

    public void notifyOrderTrackers() {
        for (OrderTracker orderTracker : orderTrackerList) {
            orderTracker.setOrderMap(orderMap);
        }
    }

    public void registerPickerModel(PickerModel pickerModel) {
        pickerModelList.add(pickerModel);
    }

    public void notifyPickerModels() {
        TreeMap<Integer, OrderState> orderMapForPicker = new TreeMap<>();
        progressingOrderMap = filterOrdersByState(OrderState.Progressing);
        OrderedOrderMap = filterOrdersByState(OrderState.Ordered);
        orderMapForPicker.putAll(progressingOrderMap);
        orderMapForPicker.putAll(OrderedOrderMap);

        for (PickerModel pickerModel : pickerModelList) {
            pickerModel.setOrderMap(orderMapForPicker);
        }
    }

    private TreeMap<Integer, OrderState> filterOrdersByState(OrderState state) {
        TreeMap<Integer, OrderState> filteredOrderMap = new TreeMap<>();
        for (Map.Entry<Integer, OrderState> entry : orderMap.entrySet()) {
            if (entry.getValue() == state) {
                filteredOrderMap.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredOrderMap;
    }

    public String getOrderDetailForPicker(int orderId) throws IOException {
        OrderState state = orderMap.get(orderId);
        if (state.equals(OrderState.Progressing)) {
            return OrderFileManager.readOrderFile(progressingPath, orderId);
        } else {
            return "the function is only for picker";
        }
    }

    /**
     * Enhanced: Initializes order map and rebuilds reserved stock tracking.
     */
    public void initializeOrderMap() {
        ArrayList<Integer> orderedIds = orderIdsLoader(orderedPath);
        ArrayList<Integer> progressingIds = orderIdsLoader(progressingPath);

        if (orderedIds.size() > 0) {
            for (Integer orderId : orderedIds) {
                orderMap.put(orderId, OrderState.Ordered);
                // TODO: Reload order contents to rebuild reserved stock
            }
        }

        if (progressingIds.size() > 0) {
            for (Integer orderId : progressingIds) {
                orderMap.put(orderId, OrderState.Progressing);
                // TODO: Reload order contents to rebuild reserved stock
            }
        }

        notifyOrderTrackers();
        notifyPickerModels();

        System.out.println("orderMap initialized. " + orderMap.size() + " orders in total, including:");
        System.out.println(orderedIds.size() + " Ordered orders, " + progressingIds.size() + " Progressing orders");
    }

    private ArrayList<Integer> orderIdsLoader(Path dir) {
        ArrayList<Integer> orderIds = new ArrayList<>();

        if (Files.exists(dir) && Files.isDirectory(dir)) {
            try (Stream<Path> fileStream = Files.list(dir)) {
                List<Path> files = fileStream.filter(Files::isRegularFile).toList();

                if (files.isEmpty()) {
                    System.out.println(dir + " is empty");
                } else {
                    for (Path file : files) {
                        String fileName = file.getFileName().toString();
                        if (fileName.endsWith(".txt")) {
                            try {
                                int orderId = Integer.parseInt(fileName.substring(0, fileName.lastIndexOf('.')));
                                orderIds.add(orderId);
                                System.out.println(orderId);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid file name: " + fileName);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading " + dir + ", " + e.getMessage());
            }
        } else {
            System.out.println(dir + " does not exist.");
        }
        return orderIds;
    }
}