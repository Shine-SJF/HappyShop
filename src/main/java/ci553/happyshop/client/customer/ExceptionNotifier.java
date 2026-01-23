package ci553.happyshop.client.customer;

import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WindowBounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The ExceptionNotifier class provides a dependent window that displays
 * exception messages to the customer during checkout.
 *
 * It is triggered by the CustomerModel when business rules are violated,
 * such as payment under £5 or excessive order quantity.
 *
 * This window appears near the CustomerView window to maintain a cohesive
 * and user-friendly interface.
 */
public class ExceptionNotifier {
    public CustomerView cusView; // track the CustomerView window

    private static int WIDTH = UIStyle.removeProNotifierWinWidth;
    private static int HEIGHT = UIStyle.removeProNotifierWinHeight;

    private Stage window;
    private Scene scene;
    private TextArea taExceptionMsg;

    // Creates the Scene
    private void createScene(String titleText, String actionText) {
        Label laTitle = new Label(titleText);
        laTitle.setStyle(UIStyle.alertTitleLabelStyle);

        taExceptionMsg = new TextArea();
        taExceptionMsg.setEditable(false);
        taExceptionMsg.setWrapText(true);
        taExceptionMsg.setPrefHeight(80);
        taExceptionMsg.setStyle(UIStyle.alertContentTextAreaStyle);

        Label laCustomerAction = new Label(actionText);
        laCustomerAction.setWrapText(true);
        laCustomerAction.setStyle(UIStyle.alertContentUserActionStyle);

        Button btnOk = new Button("Ok");
        btnOk.setStyle(UIStyle.alertBtnStyle);
        btnOk.setOnAction(e -> window.close());

        HBox hbCustomerAction = new HBox(20, laCustomerAction, btnOk);
        hbCustomerAction.setAlignment(Pos.CENTER_LEFT);

        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);

        pane.add(laTitle, 0, 0);
        pane.add(taExceptionMsg, 0, 1);
        pane.add(hbCustomerAction, 0, 2);
        pane.setStyle(UIStyle.rootStyleGray);

        scene = new Scene(pane, WIDTH, HEIGHT);
    }

    // Creates the window
    private void createWindow(String titleText, String actionText) {
        if (scene == null) {
            createScene(titleText, actionText);
        }

        window = new Stage();
        window.initModality(Modality.NONE);
        window.setTitle("Checkout Exception");
        window.setScene(scene);

        WindowBounds bounds = cusView.getWindowBounds();
        window.setX(bounds.x + bounds.width - WIDTH - 10);
        window.setY(bounds.y + bounds.height / 2 + 40);
        window.show();
    }

    // Show exception message
    public void showExceptionMsg(String exceptionMsg, String titleText, String actionText) {
        if (window == null || !window.isShowing()) {
            createWindow(titleText, actionText);
        }

        taExceptionMsg.setText(exceptionMsg);
        window.toFront();
    }

    // Close the notifier window
    public void closeNotifierWindow() {
        if (window != null && window.isShowing()) {
            window.close();
        }
    }
}

