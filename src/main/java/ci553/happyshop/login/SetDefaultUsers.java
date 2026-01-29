package ci553.happyshop.login;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *writes default user details to users.txt, adding the salt and hashing the password before doing so to ensure security
 used now as there currently is no account creation
 */




public class SetDefaultUsers{
    public static String[] defaultUsers = { //creates default users
        "customer1,customerPassword,Customer",
        "picker1,pickerPassword,Picker",
        "warehouse1,warehousePassword,Warehouse",
        "admin1,adminPassword,Admin"
    };

    public static String initializeDefaultUsers() {
        System.out.println("Initializing default users in the file");

        String filePath = "users.txt"; //file path

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) { //exception handling and file writing
            for (String user : defaultUsers) { //enhanced for loop to write each user to the file
                String[] userDetails = user.split(",");
                String salt = Salt.getSalt(); //generates a salt
                String hashedPassword = Hash.hashPassword(userDetails[1] + salt); //hashes the password with the salt
                writer.write(userDetails[0] + "," + hashedPassword + "," + userDetails[2] + "," + salt); //writes salt and hashed password to file
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

