package ci553.happyshop.client.warehouse;

import ci553.happyshop.utility.WinPosManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;


 //HistoryWindow for displaying warehouse management history

public class HistoryWindow {
    public WarehouseView warehouseView;
    private Stage historyWindow;
    private ListView<String> historyListView;

    /**
     * Shows the management history
     */
    public void showManageHistory(ArrayList<String> history) {
        if (historyWindow == null) {
            createHistoryWindow();
        }

        ObservableList<String> items = FXCollections.observableArrayList(history);
        historyListView.setItems(items);

        if (!historyWindow.isShowing()) {
            historyWindow.show();
        }
    }

    /**
     * Creates the history window
     */
    private void createHistoryWindow() {
        historyWindow = new Stage();

        Label titleLabel = new Label("Warehouse History");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        historyListView = new ListView<>();
        historyListView.setPrefSize(400, 300);

        VBox root = new VBox(10, titleLabel, historyListView);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 450, 400);
        historyWindow.setScene(scene);
        historyWindow.setTitle("Warehouse History");

        // Register with window position manager
        WinPosManager.registerWindow(historyWindow, 450, 400);
    }

}