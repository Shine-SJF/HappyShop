package ci553.happyshop.login;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class SetDefaultUsers{
    public static String[] defaultUsers = { //creates default users
        "customer1,customerPassword,Customer",
        "Picker1,PickerPassword,Picker",
        "warehouse1,warehousePassword,Warehouse",
        "admin1,adminPassword,Admin"
    };

    public static String initializeDefaultUsers() {
        System.out.println("Initializing default users in the file");

        String filePath = "users.txt"; //file path

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) { //exception handling and file writing
            for (String user : defaultUsers) { //enhanced for loop to write each user to the file
                String[] userDetails = user.split(",");
                writer.write(userDetails[0] + "," + userDetails[1] + "," + userDetails[2] + ","); //writes to file
                writer.newLine();
            }
            System.out.println("Default users written to file.");
            return "Default users initialized successfully.";
        } catch (IOException userInitialization) { //exception handling for file writing
            System.out.println("File error during user initialization: " + userInitialization.getMessage());
            return "Error initializing default users.";
        } 
    }

    public String getDefaultUsers() { //getter for default user details
        return String.join("\n", defaultUsers);
    }
    
}

