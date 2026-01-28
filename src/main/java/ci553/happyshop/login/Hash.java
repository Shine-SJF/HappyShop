package ci553.happyshop.login;

public class Hash { //hashes passwords using a simple hashing algorithm as proof of concept
    public static String hashPassword(String password) {
        StringBuilder hashed = new StringBuilder();

        
        char[] passwordArray = password.toCharArray(); //turn in into array for indexing


        for (int i = 0; i < password.length(); i++) { //simple hashing algorithm that scrambles digits
            int randomDigit = (int) (Math.random() *password.length());
            int a = password.charAt(i);
            int b = password.charAt(randomDigit);
            int c = a;
            a = b;
            b = c;

            passwordArray[i] = (char) a;
            passwordArray[randomDigit] = (char) b;
           

        }
        System.out.println(passwordArray);
        return hashed.toString();
        
    }

}
 