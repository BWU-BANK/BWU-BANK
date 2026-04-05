package exceptions;

/**
 * Exception thrown when an account with the same account number already exists.
 */
public class DuplicateAccountException extends BankingException {
    private final String accountNumber;

    public DuplicateAccountException(String accountNumber) {
        super("Account already exists: " + accountNumber);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
