package ci553.happyshop.atm;

import javafx.application.Application;
import javafx.stage.Stage;

// Main class for the ATM project
// This class creates the ATM GUI and model functionality.
// The main class serves as the entry point for the ATM application.
// It sets up the JavaFX application, initializes the Model-View-Controller

    public class Main extends Application
    {
        public void start(Stage window)
        {
            // Set up debugging and print initial message
            Debug.set(true);
            Debug.trace("ATM starting");
            Debug.trace("Main::start");

            // Create a Bank object for this ATM
            Bank b = new Bank();

            // Add some test bank accounts
            b.addBankAccount(10001, 11111, 100, "Basic");
            b.addBankAccount(10002, 22222, 50, "Premium");

            // Create the Model, View, and Controller objects
            Model model = new Model(b);
            View view = new View();
            Controller controller = new Controller();

            // Link them together
            model.view = view;
            model.controller = controller;

            controller.model = model;
            controller.view = view;

            view.model = model;
            view.controller = controller;

            b.view = view;

            // Start the GUI
            view.start(window);

            // Application is now running
            Debug.trace("ATM running");
        }

        public static void main(String args[])
        {
            // The main method is used when launching from the command line
            // Launch initialises the system and then calls start
            // In BlueJ, BlueJ calls start itself
            launch(args);
        }
    }

