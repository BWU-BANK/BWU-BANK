package exceptions;

/**
 * Exception thrown for invalid amounts (e.g., zero or negative deposit/withdrawal).
 */
public class InvalidAmountException extends BankingException {
    public InvalidAmountException(String reason) {
        super("Invalid amount: " + reason);
    }
}
