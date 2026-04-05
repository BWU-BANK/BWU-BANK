package utils;

import java.util.UUID;

/**
 * Utility class to generate account numbers.
 */
public class AccountNumberGenerator {
    /**
     * Generates a unique 8-character UUID-based account number prefixed with "ACC".
     *
     * @return Generated account number string.
     */
    public static String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
