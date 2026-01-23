package ci553.happyshop.client;

import ci553.happyshop.atm.Bank;

import ci553.happyshop.client.customer.CustomerController;
import ci553.happyshop.client.customer.CustomerModel;
import ci553.happyshop.client.customer.CustomerView;

import ci553.happyshop.client.emergency.EmergencyExit;
import ci553.happyshop.client.orderTracker.OrderTracker;

import ci553.happyshop.client.picker.PickerController;
import ci553.happyshop.client.picker.PickerModel;
import ci553.happyshop.client.picker.PickerView;

import ci553.happyshop.client.warehouse.AlertSimulator;
import ci553.happyshop.client.warehouse.HistoryWindow;
import ci553.happyshop.client.warehouse.WarehouseController;
import ci553.happyshop.client.warehouse.WarehouseModel;
import ci553.happyshop.client.warehouse.WarehouseView;

import ci553.happyshop.orderManagement.OrderHub;

import ci553.happyshop.payment.PaymentService;
import ci553.happyshop.payment.PaymentServiceFactory;
import ci553.happyshop.payment.PaymentServiceFactory.PaymentType;

import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.storageAccess.DatabaseRWFactory;

import javafx.application.Application;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    // ADDED: keep a reference to Main so buttons/controllers can call Main.showWarehouse() etc.
    private static Main instance;

    // ADDED: keep the primary stage (the one JavaFX gives us)
    private Stage primaryStage;

    // ADDED: build picker once (hidden) so update() never crashes
    private Stage pickerStage;

    // ADDED: create shared dependencies once and reuse them
    private DatabaseRW databaseRW;
    private PaymentService paymentService;

    // ADDED: keep these so we can show windows later when needed
    private PickerModel pickerModel;
    private PickerView pickerView;
    private OrderTracker orderTracker;

    // ADDED: build warehouse only when needed
    private WarehouseView warehouseView;
    private WarehouseModel warehouseModel;
    private HistoryWindow historyWindow;
    private AlertSimulator alertSimulator;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) throws IOException {

        instance = this;
        primaryStage = window;

        // make database + payment once at startup
        databaseRW = DatabaseRWFactory.createDatabaseRW();
        paymentService = choosePaymentService();

        // register observers first
        registerObserversWithoutShowingWindows();

        // Load existing orders after observers are registered
        initializeOrderMap();

        // only start the customer client at launch
        startCustomerClient(primaryStage);
    }

    // simple navigation buttons
    public static void showPicker() {
        if (instance != null) instance.openPickerWindow();
    }

    public static void showWarehouse() {
        if (instance != null) instance.openWarehouseWindow();
    }

    public static void showEmergencyExit() {
        if (instance != null) instance.openEmergencyExit();
    }

    //moved payment selection into a helper method so start() stays tidy
    private PaymentService choosePaymentService() {

        Bank bank = new Bank();
        bank.addBankAccount(1234, 1111, 500, "current");
        bank.addBankAccount(9999, 9999, 5000, "current");

        ChoiceDialog<PaymentType> dialog =
                new ChoiceDialog<>(PaymentType.ATM, PaymentType.values());
        dialog.setTitle("Payment Method");
        dialog.setHeaderText("Choose payment method for this session");
        dialog.setContentText("Payment type:");

        PaymentType paymentType = dialog.showAndWait().orElse(PaymentType.DUMMY);

        return PaymentServiceFactory.createPaymentService(paymentType, bank);
    }

    private void registerObserversWithoutShowingWindows() {

        pickerModel = new PickerModel();
        pickerView = new PickerView();
        PickerController pickerController = new PickerController();

        pickerView.pickerController = pickerController;
        pickerController.pickerModel = pickerModel;
        pickerModel.pickerView = pickerView;

        pickerModel.registerWithOrderHub();

        pickerStage = new Stage();
        pickerView.start(pickerStage);
        pickerStage.hide();

        orderTracker = new OrderTracker();
        orderTracker.registerWithOrderHub();
    }

    private void startCustomerClient(Stage stageToUse) {

        CustomerView cusView = new CustomerView();
        CustomerController cusController = new CustomerController();
        CustomerModel cusModel = new CustomerModel();

        // MVC wiring
        cusView.cusController = cusController;
        cusController.cusModel = cusModel;

        // use shared database + chosen payment service
        cusModel.cusView = cusView;
        cusModel.databaseRW = databaseRW;
        cusModel.setPaymentService(paymentService);

        // use the primary stage
        cusView.start(stageToUse);
    }

    private void openPickerWindow() {
        pickerStage.show();      //show the already-built window
        pickerStage.toFront();   // bring it to the front
    }

    // open warehouse only when user asks for it
    private void openWarehouseWindow() {

        if (warehouseView == null) {
            warehouseView = new WarehouseView();
            WarehouseController controller = new WarehouseController();
            warehouseModel = new WarehouseModel();

            warehouseView.controller = controller;
            controller.model = warehouseModel;
            warehouseModel.view = warehouseView;

            // reuse shared database
            warehouseModel.databaseRW = databaseRW;

            // dependent windows set up once
            historyWindow = new HistoryWindow();
            alertSimulator = new AlertSimulator();

            warehouseModel.historyWindow = historyWindow;
            warehouseModel.alertSimulator = alertSimulator;

            historyWindow.warehouseView = warehouseView;
            alertSimulator.warehouseView = warehouseView;
        }

        warehouseView.start(new Stage());
    }

    // emergency exit only appears when user clicks for it
    private void openEmergencyExit() {
        EmergencyExit.getEmergencyExit();
    }

    private void initializeOrderMap() {
        OrderHub.getOrderHub().initializeOrderMap();
    }
}
