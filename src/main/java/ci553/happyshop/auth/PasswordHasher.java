package ci553.happyshop.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PasswordHasher - Secure password hashing using SHA-256 with salt
 * (BCrypt alternative for environments without external libraries)
 */
public class PasswordHasher {

    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Hash a password with a random salt
     * @param password Plain text password
     * @return Hashed password in format: salt:hash
     */
    public static String hashPassword(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hash password with salt
            String hash = hashWithSalt(password, salt);

            // Return salt:hash format
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            return saltBase64 + ":" + hash;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verify a password against a stored hash
     * @param password Plain text password to verify
     * @param storedHash Stored hash in format: salt:hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split stored hash into salt and hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            String saltBase64 = parts[0];
            String expectedHash = parts[1];

            // Decode salt
            byte[] salt = Base64.getDecoder().decode(saltBase64);

            // Hash the input password with the same salt
            String actualHash = hashWithSalt(password, salt);

            // Compare hashes
            return actualHash.equals(expectedHash);

        } catch (Exception e) {
            return false;
        }
    }


     //Hash a password with a given salt

    private static String hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);

        // Add salt to digest
        digest.update(salt);

        // Hash password
        byte[] hashedBytes = digest.digest(password.getBytes());

        // Convert to Base64
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

}