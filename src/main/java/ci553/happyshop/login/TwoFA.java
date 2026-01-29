package ci553.happyshop.login;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;


/**
 *generates 6-digit 2-Factor Authentication code and writes it to 2FA.txt when user is prompted with the 2FA screen
 *it then verifies that the inputed 2FA code matches the last one in file for that user
 */

public class TwoFA {

    private static final String TFA_FILE = "2FA.txt";

    private final SecureRandom random = new SecureRandom();

    private String lastCode;

    public String generateAndWrite(String username, String role) {
        if (username == null || username.isBlank() || role == null || role.isBlank()) {
            throw new IllegalArgumentException("username/role must not be blank");
        }

        lastCode = generateSixDigitCode();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TFA_FILE, true))) {
            writer.write("username: " + username + "," + "2FA code: " + lastCode + "," + "role: " + role);
            writer.newLine();
            System.out.println("2FA code written to file for " + username + ": " + lastCode);
        } catch (IOException writeFail) {
            System.out.println("File error during 2FA code writing: " + writeFail.getMessage());
            lastCode = null;
            throw new RuntimeException("Failed to write 2FA code", writeFail);
        }

        return lastCode;
    }

    public boolean verify(String enteredCode) {
        return lastCode != null && enteredCode != null && lastCode.equals(enteredCode);
    }

    public String getLastCode() {
        return lastCode;
    }

    private String generateSixDigitCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
