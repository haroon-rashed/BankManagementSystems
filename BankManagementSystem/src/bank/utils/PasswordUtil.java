package bank.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for password hashing - demonstrates utility abstraction.
 */
public class PasswordUtil {

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing error", e);
        }
    }

    public static boolean verify(String rawPassword, String hashedPassword) {
        return hash(rawPassword).equals(hashedPassword);
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) return false;
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        return hasUpper && hasDigit;
    }
}
