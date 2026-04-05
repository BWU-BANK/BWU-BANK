package models;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;
import exceptions.BankingException;
import interfaces.LoanEligible;
import interfaces.Transferable;

/**
 * Represents a Savings Account.
 */
public class SavingsAccount extends Account implements Transferable, LoanEligible {
    private double interestRate;
    private double minBalance;

    public SavingsAccount(String accountNumber, String holderName, double initialBalance, double interestRate, double minBalance) {
        super(accountNumber, holderName, initialBalance);
        this.interestRate = interestRate;
        this.minBalance = minBalance;
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
        if (this.balance - amount < minBalance) {
            throw new InsufficientFundsException(amount - (this.balance - minBalance));
        }
        this.balance -= amount;
        this.transactionHistory.add("Withdrawal: ₹" + amount);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public void transfer(Account target, double amount) throws BankingException {
        this.withdraw(amount);
        target.deposit(amount, "Transfer from " + this.accountNumber);
        this.transactionHistory.add("Transfer to " + target.getAccountNumber() + ": ₹" + amount);
    }

    @Override
    public boolean checkEligibility(double loanAmount) {
        // Simple mock eligibility logic: eligible if current balance is at least 10% of requested loan
        return this.balance >= loanAmount * 0.10;
    }

    @Override
    public void applyLoan(double amount, int tenureMonths) {
        if (checkEligibility(amount)) {
            System.out.println("Loan of ₹" + amount + " for " + tenureMonths + " months approved.");
            this.transactionHistory.add("Loan Approved: ₹" + amount);
        } else {
            System.out.println("Loan application rejected due to insufficient account history/balance.");
        }
    }

    public void applyInterest() {
        double interest = this.balance * (interestRate / 100);
        this.balance += interest;
        this.transactionHistory.add("Interest Applied: ₹" + interest);
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(double minBalance) {
        this.minBalance = minBalance;
    }
}
