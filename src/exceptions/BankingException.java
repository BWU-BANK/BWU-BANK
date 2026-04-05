package exceptions;

/**
 * Base exception class for all custom banking-related exceptions.
 */
public class BankingException extends Exception {
    public BankingException(String message) {
        super(message);
    }
}
