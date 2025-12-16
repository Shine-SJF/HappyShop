package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.utility.ProductListFormatter;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WindowBounds;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

public class WishListWindow {

    private static int WIDTH = UIStyle.HistoryWinWidth;
    private static int HEIGHT = UIStyle.HistoryWinHeight;

    public CustomerView cusview;
    public CustomerController cusController;
    private  Stage window = new Stage();
    private  Scene scene;

    private  TextArea taWishlist;

    Label laSummary;
    private ObservableList<Product> obeProductList; //observable product list
    ListView<Product> obrLvProducts; //A ListView observes the product list

    // Create the scene only once (to avoid recreating it multiple times)
//    private  void createScene() {
//        // a TextArea to show stock management history
//        taWishlist = new TextArea();
//        taWishlist.setPrefSize(150,150);
//        taWishlist.setEditable(false);
//        taWishlist.setStyle(UIStyle.textFiledStyle);
//        VBox vbHistory = new VBox(taWishlist);
//        scene = new Scene(vbHistory,WIDTH,HEIGHT);
//    }


    // Create the scene only once (to avoid recreating it multiple times)
    private  void createScene() {
        laSummary = new Label("Wish List Overview");
        // data, an observable ArrayList, observed by obrLvProducts
        obeProductList = FXCollections.observableArrayList();
        obrLvProducts = new ListView<>(obeProductList);//ListView proListView observes proList

        obrLvProducts.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);

                if (empty || product == null) {
                    setGraphic(null);
                    System.out.println("");
                } else {
                    String imageName = product.getProductImageName(); // Get image name (e.g. "0001.jpg")
                    String relativeImageUrl = StorageLocation.imageFolder + imageName;
                    // Get the full absolute path to the image
                    Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
                    String imageFullUri = imageFullPath.toUri().toString();// Build the full image Uri

                    ImageView ivPro;
                    try {
                        ivPro = new ImageView(new Image(imageFullUri, 50,45, true,true)); // Attempt to load the product image
                    } catch (Exception e) {
                        // If loading fails, use a default image directly from the resources folder
                        ivPro = new ImageView(new Image("imageHolder.jpg",50,45,true,true)); // Directly load from resources
                    }

                    Label laProToString = new Label(product.toString()); // Create a label for product details
                    HBox hbox = new HBox(20, ivPro, laProToString); // Put ImageView and label in a horizontal layout
                    setGraphic(hbox);  // Set the whole row content
                }
            }
        });


        Button btnMoveToTrolley = new Button("Move To Trolley");
        btnMoveToTrolley.setOnAction(this::buttonClicked);
        Button btnDelete = new Button("Delete");
        btnDelete.setOnAction(this::buttonClicked);
        HBox hbox =new HBox(60, btnMoveToTrolley,btnDelete);

        hbox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(15, laSummary, obrLvProducts, hbox);

        vbox.setAlignment(Pos.TOP_CENTER);
        scene = new Scene(vbox,WIDTH,HEIGHT);
    }

    // Create the window only when needed (i.e., when the window is not created or closed by user but we need it again)
    private  void createWindow(){
        if (scene == null) {
            createScene(); // create the scene only once
        }

        window = new Stage();
        window.setScene(scene);
        window.setTitle("Wish List");
        window.show();
        //get the bounds of customerView window
        //so that we can put the wish list window next to the customerview window
        WindowBounds bounds = cusview.getWindowBounds();
        window.setX(bounds.x + bounds.width - 20);
        window.setY(bounds.y); // align vertically
    }


    private void buttonClicked(ActionEvent event) {

            Button btn = (Button)event.getSource();
            String action = btn.getText();
        try {
            cusController.doAction(action);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public  void showWishlist(ArrayList<Product> wishlist){
        if(window ==null ||!window.isShowing() ) {
            createWindow();  // Only create window if it's not created or unvisible
        }

        int proCounter = wishlist.size();
        System.out.println(proCounter);
        laSummary.setText(proCounter + " products in Your Wish List");
        laSummary.setVisible(true);
        obeProductList.clear();
        obeProductList.addAll(wishlist);
    }

}
