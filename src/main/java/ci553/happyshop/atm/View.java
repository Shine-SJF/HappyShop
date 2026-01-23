package ci553.happyshop.atm;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.*;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javax.sound.sampled.*;
import java.io.File;

    public class View{
        // Height and width of the window
        int H = 450;
        int W = 500;

        // User interface components
        Label title;
        TextField message;
        TextArea reply;
        ScrollPane replyScrollPane;
        GridPane grid;
        TilePane keypadButtons;
        Image bankLogo;
        ImageView imageView;
        Text bankName;
        Text direction;
        Button buttonBegin;
        HBox logoNameLayout;
        VBox directionButtonPanel;
        VBox layout;
        Text sessionTerminated;
        VBox sessionTerminatedLayout;

        // Model-View-Controller setup
        public Model model;
        public Controller controller;

        public View()
        {
            Debug.trace("View::<constructor>");
        }

        // Start the GUI with the welcome page
        public void start(Stage window)
        {
            Debug.trace("View::start");
            bankLogo = new Image("file:levelbanklogo.png");
            imageView = new ImageView(bankLogo);
            imageView.setId("Logo");

            imageView.setFitWidth(92);  // Set the width of the logo
            imageView.setFitHeight(92); // Set the height of the logo
            imageView.setPreserveRatio(true);

            bankName = new Text("Level Bank");
            bankName.setId("BankName");
            direction = new Text("Please insert card or press begin.");
            direction.setId("Direction");
            buttonBegin = new Button("Begin");
            buttonBegin.setId("Begin");

            buttonBegin.setOnAction(this::beginButtonClicked);

            logoNameLayout = new HBox(8, imageView, bankName);
            logoNameLayout.setId("LogoNameLayout");
            logoNameLayout.setAlignment(Pos.CENTER);

            directionButtonPanel = new VBox(40, direction, buttonBegin);
            directionButtonPanel.setId("DirectionButtonPanel");
            directionButtonPanel.setAlignment(Pos.CENTER);

            layout = new VBox(80, logoNameLayout, directionButtonPanel);
            layout.setId("WelcomeLayout");
            layout.setAlignment(Pos.CENTER);

            model.setState(model.WELCOME_PAGE);
            Scene scene = new Scene(layout, W, H);
            scene.getStylesheets().add("atm.css");
            window.setScene(scene);
            window.show();
        }

        // Once the begin button is pressed, the ATM interface is revealed
        public void startSession(Stage window)
        {
            // Create the user interface components
            grid = new GridPane();
            grid.setId("ATMLayout");
            keypadButtons = new TilePane();
            keypadButtons.setId("KeypadButtons");

            title = new Label();
            title.setId("BankTitle");
            grid.add(title, 0, 0);

            message = new TextField();
            message.setEditable(false);
            grid.add(message, 0, 1);

            reply = new TextArea();
            reply.setEditable(false);
            replyScrollPane = new ScrollPane();
            replyScrollPane.setContent(reply);
            grid.add(replyScrollPane, 0, 2);

            // Button labels
            String labels[][] = {
                    {"7", "8", "9", "", "Dep", ""},
                    {"4", "5", "6", "", "Wtd", ""},
                    {"1", "2", "3", "", "Bal", "Fin"},
                    {"CLR", "0", "", "MM", "Acc", "Ent"} };

            // Create buttons
            for (String[] row : labels) {
                for (String label : row) {
                    if (label.length() >= 1) {
                        Button b = new Button(label);
                        b.setOnAction(this::buttonClicked);
                        keypadButtons.getChildren().add(b);
                    } else {
                        keypadButtons.getChildren().add(new Text());
                    }
                }
            }
            grid.add(keypadButtons, 0, 3);

            // Display the GUI
            Scene scene = new Scene(grid, W, H);
            scene.getStylesheets().add("atm.css");
            window.setScene(scene);
            window.show();

            model.initialise("Welcome to the ATM!");
            update();
        }

        // Ends the session when finish is pressed, displays a goodbye page before
        // transitioning to the welcome page after a delay
        public void endSession(Stage window)
        {
            sessionTerminated = new Text("This session has ended.");
            sessionTerminated.setId("SessionTerminated");

            sessionTerminatedLayout = new VBox(0, sessionTerminated);
            sessionTerminatedLayout.setId("SessionTerminatedLayout");
            sessionTerminatedLayout.setAlignment(Pos.CENTER);

            // Display the GUI
            Scene scene = new Scene(sessionTerminatedLayout, W, H);
            scene.getStylesheets().add("atm.css");
            window.setScene(scene);
            window.show();

            update();

            PauseTransition beginNewSession = new PauseTransition(Duration.seconds(5));
            beginNewSession.setOnFinished(event -> start(window));
            beginNewSession.play();
        }

        // Display the account settings page
        public void showAccountSettings()
        {
            Debug.trace("View::showAccountSettings");
            grid.getChildren().clear();

            title.setText("Account Settings");
            grid.add(title, 0, 0);

            grid.add(message, 0, 1);

            reply.setText("Click 'Pass' to change your password.");
            grid.add(replyScrollPane, 0, 2);

            String labels[][] = {
                    {"7", "8", "9", "", "Dep", ""},
                    {"4", "5", "6", "", "W/D", ""},
                    {"1", "2", "3", "", "Bal", "Fin"},
                    {"CLR", "0", "", "MM", "Pas", "Back"} };

            keypadButtons.getChildren().clear();
            for (String[] row : labels) {
                for (String label : row) {
                    if (label.length() >= 1) {
                        Button b = new Button(label);
                        b.setOnAction(this::buttonClicked);
                        keypadButtons.getChildren().add(b);
                    } else {
                        keypadButtons.getChildren().add(new Text());
                    }
                }
            }
            grid.add(keypadButtons, 0, 3);
        }

        // Display the password change window
        public void showPasswordChangeWindow()
        {
            Debug.trace("View::showPasswordChangeWindow");
            Stage passwordStage = new Stage();
            passwordStage.setTitle("Change Password");

            GridPane passwordGrid = new GridPane();
            passwordGrid.setId("Layout");

            TextField passwordMessage = new TextField();
            passwordMessage.setEditable(false);
            passwordGrid.add(passwordMessage, 0, 0);

            TextArea passwordReply = new TextArea();
            passwordReply.setEditable(false);
            passwordReply.setText("Please enter your current password and press Ent.");
            ScrollPane passwordScrollPane = new ScrollPane();
            passwordScrollPane.setContent(passwordReply);
            passwordGrid.add(passwordScrollPane, 0, 1);

            TilePane passwordButtonPane = new TilePane();
            passwordButtonPane.setId("Buttons");

            String labels[][] = {
                    {"7", "8", "9", "", "", ""},
                    {"4", "5", "6", "", "", ""},
                    {"1", "2", "3", "", "", ""},
                    {"CLR", "0", "", "", "", "Ent"} };

            for (String[] row : labels) {
                for (String label : row) {
                    if (label.length() >= 1) {
                        Button b = new Button(label);
                        b.setOnAction(e -> passwordButtonClicked(e, passwordMessage, passwordReply, passwordStage));
                        passwordButtonPane.getChildren().add(b);
                    } else {
                        passwordButtonPane.getChildren().add(new Text());
                    }
                }
            }
            passwordGrid.add(passwordButtonPane, 0, 2);

            Scene passwordScene = new Scene(passwordGrid, 480, 370);
            passwordScene.getStylesheets().add("atm.css");
            passwordStage.setScene(passwordScene);
            passwordStage.show();

            model.setState(model.VALIDATE_OLD_PASSWORD);
        }

        // Handle button clicks in the password change window
        private void passwordButtonClicked(ActionEvent event, TextField passwordMessage, TextArea passwordReply, Stage passwordStage)
        {
            Button b = ((Button) event.getSource());
            String label = b.getText();
            playButtonSound();
            if (controller != null) {
                controller.processPasswordChange(label, passwordMessage, passwordReply, passwordStage);
            }
        }

        // Display the account creation window
        public void showAccountCreationWindow()
        {
            Debug.trace("View::showAccountCreationWindow");
            Stage accountStage = new Stage();
            accountStage.setTitle("Create Account");

            GridPane accountGrid = new GridPane();
            accountGrid.setId("Layout");

            TextField accountMessage = new TextField();
            accountMessage.setEditable(false);
            accountGrid.add(accountMessage, 0, 0);

            TextArea accountReply = new TextArea();
            accountReply.setEditable(false);
            accountReply.setText("To begin creating your account, choose between either a Basic (B) or Premium (P) account.");
            ScrollPane accountScrollPane = new ScrollPane();
            accountScrollPane.setContent(accountReply);
            accountGrid.add(accountScrollPane, 0, 1);

            TilePane accountButtonPane = new TilePane();
            accountButtonPane.setId("Buttons");

            String labels[][] = {
                    {"7", "8", "9", "", "", ""},
                    {"4", "5", "6", "", "", ""},
                    {"1", "2", "3", "", "", ""},
                    {"CLR", "0", "", "B", "P", "Ent"} };

            for (String[] row : labels) {
                for (String label : row) {
                    if (label.length() >= 1) {
                        Button b = new Button(label);
                        b.setOnAction(e -> accountButtonClicked(e, accountMessage, accountReply, accountStage));
                        accountButtonPane.getChildren().add(b);
                    } else {
                        accountButtonPane.getChildren().add(new Text());
                    }
                }
            }
            accountGrid.add(accountButtonPane, 0, 2);

            Scene accountScene = new Scene(accountGrid, 480, 370);
            accountScene.getStylesheets().add("atm.css");
            accountStage.setScene(accountScene);
            accountStage.show();

            model.setState(model.SELECT_ACCOUNT_TYPE);
        }

        // The ATM interface is revealed when the begin button on the welcome page
        // is clicked
        public void beginButtonClicked(ActionEvent event) {
            Stage session = (Stage) buttonBegin.getScene().getWindow();
            startSession(session);
            playButtonSound();
        }

        // Handle button clicks in the account creation window
        private void accountButtonClicked(ActionEvent event, TextField accountMessage, TextArea accountReply, Stage accountStage)
        {
            Button b = ((Button) event.getSource());
            String label = b.getText();
            playButtonSound();
            if (controller != null) {
                controller.processAccountCreation(label, accountMessage, accountReply, accountStage);
            }
        }

        public void buttonClicked(ActionEvent event) {
            Button b = ((Button) event.getSource());
            if (controller != null) {
                String label = b.getText();
                Debug.trace("View::buttonClicked: label = " + label);
                controller.process(label);
                playButtonSound();
            }
        }

        // Plays a beep sound effect for every button press
        public void playButtonSound() {
            try {
                File beepEffect = new File("Beep.wav");
                AudioInputStream audio = AudioSystem.getAudioInputStream(beepEffect);
                Clip soundEffect = AudioSystem.getClip();
                soundEffect.open(audio);
                soundEffect.start();
            } catch (Exception e) {
                Debug.trace("View::playButtonSound");
            }
        }

        // Update the view based on the model's state
        public void update()
        {
            if (model != null) {
                Debug.trace("View::update");
                String message1 = model.title;
                title.setText(message1);
                String message2 = model.display1;
                message.setText(message2);
                String message3 = model.display2;
                reply.setText(message3);
            }
        }
    }

