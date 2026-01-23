package ci553.happyshop.utility;

public class OtpValidatorDriver {

    public static void main(String[] args) {

        String[][] tests = {
                {"123456", "true",  "valid partition: exactly 6 digits"},
                {"000000", "true",  "edge digits (all zeros)"},
                {"999999", "true",  "edge digits (all nines)"},
                {"12345",  "false", "boundary: length 5"},
                {"1234567","false", "boundary: length 7"},
                {"12a456", "false", "contains a non-digit"},
                {"",       "false", "empty string"},
                {"     ",  "false", "spaces are not digits"},
        };

        System.out.println("OTP Validator manual tests:\n");

        for (String[] t : tests) {
            String input = t[0];
            boolean expected = Boolean.parseBoolean(t[1]);
            String reason = t[2];

            boolean actual = OtpValidator.isValid(input);

            System.out.printf("Input: %-8s Expected: %-5s Actual: %-5s  (%s)%n",
                    "\"" + input + "\"", expected, actual, reason);
        }
    }
}
