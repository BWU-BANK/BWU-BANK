package models;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;
import exceptions.PrematureWithdrawalException;

import java.time.LocalDate;

/**
 * Represents a Fixed Deposit account which locks funds for a specific duration.
 */
public class FixedDepositAccount extends Account {
    private LocalDate maturityDate;
    private double lockedAmount;

    public FixedDepositAccount(String accountNumber, String holderName, double initialBalance, LocalDate maturityDate) {
        super(accountNumber, holderName, initialBalance);
        this.maturityDate = maturityDate;
        this.lockedAmount = initialBalance;
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        throw new InvalidAmountException("Cannot deposit into an existing Fixed Deposit once created.");
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        try {
            withdrawFull();
        } catch (PrematureWithdrawalException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Special withdrawal for FD since partial withdrawals are usually not permitted in standard logic.
     */
    public void withdrawFull() throws PrematureWithdrawalException, InsufficientFundsException {
        if (LocalDate.now().isBefore(maturityDate)) {
            throw new PrematureWithdrawalException(maturityDate);
        }
        if (this.balance <= 0) {
            throw new InsufficientFundsException(0);
        }
        this.transactionHistory.add("Fixed Deposit Withdrawal: ₹" + this.balance);
        this.balance = 0;
    }

    @Override
    public String getAccountType() {
        return "Fixed Deposit Account";
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public double getLockedAmount() {
        return lockedAmount;
    }
}
