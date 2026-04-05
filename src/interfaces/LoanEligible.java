package interfaces;

/**
 * Interface indicating an account is eligible to apply for a loan.
 */
public interface LoanEligible {
    /**
     * Checks if the account is eligible for the specified loan amount.
     * @param loanAmount The requested loan amount.
     * @return true if eligible, false otherwise.
     */
    boolean checkEligibility(double loanAmount);

    /**
     * Applies for a loan.
     * @param amount The loan amount.
     * @param tenureMonths The duration of the loan in months.
     */
    void applyLoan(double amount, int tenureMonths);
}
