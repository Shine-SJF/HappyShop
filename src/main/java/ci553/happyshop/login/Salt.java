package ci553.happyshop.login;
import java.util.Random;

/**
 *generates random simple salt to be appendeed to stored passwords
 *done before hashing
 */




public class Salt{

    public static String saltPassword() { //a static class to salt passwords
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; //list of characters to choose from
    Random random = new Random();
    StringBuilder salt = new StringBuilder(); 

    for (int i = 0; i < 16; i++) {
        int index = random.nextInt(characters.length());
        salt.append(characters.charAt(index)); //appends a random character to the password a random number of times
    }

    return salt.toString();

    } 
    public static String getSalt() { //getter for salt
        String salt = saltPassword();
        return salt;
    }
}
