package ci553.happyshop.utility;

public class OtpValidator {

    // OTP is valid if it is exactly 6 digits
    public static boolean isValid(String input) {
        if (input == null) return false;
        if (input.length() != 6) return false;

        for (int i = 0; i < input.length(); i++) {
            if (!Character.isDigit(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
