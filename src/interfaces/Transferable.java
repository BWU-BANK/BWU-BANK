package interfaces;

import models.Account;
import exceptions.BankingException;

/**
 * Interface indicating an account can transfer funds to another account.
 */
public interface Transferable {
    /**
     * Transfers funds to a target account.
     * @param target The recipient account.
     * @param amount The amount to transfer.
     * @throws BankingException If the transfer fails (e.g. insufficient funds, invalid amount).
     */
    void transfer(Account target, double amount) throws BankingException;
}
