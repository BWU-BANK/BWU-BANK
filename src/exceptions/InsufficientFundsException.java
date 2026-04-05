package exceptions;

/**
 * Exception thrown when a withdrawal amount exceeds available funds.
 */
public class InsufficientFundsException extends BankingException {
    private final double shortfall;

    public InsufficientFundsException(double shortfall) {
        super(String.format("Insufficient funds. Shortfall: ₹%.2f", shortfall));
        this.shortfall = shortfall;
    }

    public double getShortfall() {
        return shortfall;
    }
}
