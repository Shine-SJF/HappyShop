package ci553.happyshop.client;

public class MainController {
    public Main main;

    public MainController() {}

    public void doCustomerClient(){
        main.startCustomerClient();
        main.startPickerClient();
        main.startOrderTracker();
        main.initializeOrderMap();
        main.startEmergencyExit();
    }

    public void doWarehouseClient(){
        main.startWarehouseClient();
        main.startEmergencyExit();
    }
}
