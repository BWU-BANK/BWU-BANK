package exceptions;

import java.time.LocalDate;

/**
 * Exception thrown when attempting to withdraw from a Fixed Deposit before its maturity date.
 */
public class PrematureWithdrawalException extends BankingException {
    private final LocalDate maturityDate;

    public PrematureWithdrawalException(LocalDate maturityDate) {
        super("Cannot withdraw funds before maturity date: " + maturityDate);
        this.maturityDate = maturityDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }
}
