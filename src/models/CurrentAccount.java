package models;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;
import exceptions.BankingException;
import interfaces.Transferable;

/**
 * Represents a Current Account which allows overdraft.
 */
public class CurrentAccount extends Account implements Transferable {
    private double overdraftLimit;

    public CurrentAccount(String accountNumber, String holderName, double initialBalance, double overdraftLimit) {
        super(accountNumber, holderName, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive.");
        }
        this.balance += amount;
        this.transactionHistory.add("Deposit: ₹" + amount);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive.");
        }
        if (this.balance - amount < -overdraftLimit) {
            throw new InsufficientFundsException(amount - (this.balance + overdraftLimit));
        }
        this.balance -= amount;
        this.transactionHistory.add("Withdrawal: ₹" + amount);
    }

    @Override
    public String getAccountType() {
        return "Current Account";
    }

    @Override
    public void transfer(Account target, double amount) throws BankingException {
        this.withdraw(amount);
        target.deposit(amount, "Transfer from " + this.accountNumber);
        this.transactionHistory.add("Transfer to " + target.getAccountNumber() + ": ₹" + amount);
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
}
