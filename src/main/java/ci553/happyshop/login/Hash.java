package ci553.happyshop.login;

/**
 *scrambles saved passwords
 *string into a randomised version by swapping random characters.
 *proof of centext for hashing passwords for  storage 
 */



public class Hash { //hashes passwords using a simple hashing algorithm as proof of concept
    public static String hashPassword(String input) {
        if (input == null) return null;

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < input.length(); i++) { //simple deterministic scramble
            char c = input.charAt(i);
             
            c = (char) (c + 3 + (i % 5)); //shift character and mix with position

            out.append(c);
        }

        return out.reverse().toString(); //reversed for added security
    }
}



