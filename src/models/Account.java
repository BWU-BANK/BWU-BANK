package models;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all bank accounts.
 */
public abstract class Account {
    protected String accountNumber;
    protected String holderName;
    protected double balance;
    protected List<String> transactionHistory;

    public Account(String accountNumber, String holderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        if (initialBalance > 0) {
            transactionHistory.add("Initial deposit: ₹" + initialBalance);
        }
    }

    public abstract void deposit(double amount) throws InvalidAmountException;

    /**
     * Overloaded deposit method with note.
     * @param amount the amount to deposit
     * @param note custom note for the transaction
     */
    public void deposit(double amount, String note) throws InvalidAmountException {
        deposit(amount);
        transactionHistory.add("Deposit Note: " + note);
    }

    public abstract void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException;

    public abstract String getAccountType();

    public void printStatement() {
        System.out.println("----- Statement for " + accountNumber + " -----");
        for (String record : transactionHistory) {
            System.out.println(record);
        }
        System.out.println("Current Balance: ₹" + balance);
        System.out.println("----------------------------------------");
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public double getBalance() {
        return balance;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }
}
