import exceptions.BankingException;
import interfaces.LoanEligible;
import interfaces.Transferable;
import models.Account;
import models.CurrentAccount;
import models.FixedDepositAccount;
import models.SavingsAccount;
import services.Bank;
import services.TransactionLogger;
import utils.AccountNumberGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class running the console-driven UI.
 */
public class BankingApp {
    private static final Bank bank = Bank.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Initializing " + bank.getBankName() + " System...");
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1": openAccount(); break;
                    case "2": depositMoney(); break;
                    case "3": withdrawMoney(); break;
                    case "4": transferFunds(); break;
                    case "5": checkBalance(); break;
                    case "6": viewHistory(); break;
                    case "7": applyForLoan(); break;
                    case "8": viewAllAccounts(); break;
                    case "9": closeAccount(); break;
                    case "10":
                        running = false;
                        System.out.println("Thank you for using " + bank.getBankName() + ". Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please choose between 1 and 10.");
                }
            } catch (BankingException e) {
                System.err.println("Banking Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected Error: " + e.getMessage());
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void printMenu() {
        System.out.println("\n===== JAVA BANKING SYSTEM =====");
        System.out.println("1. Open New Account (Savings / Current / Fixed Deposit)");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Funds");
        System.out.println("5. Check Balance");
        System.out.println("6. View Transaction History");
        System.out.println("7. Apply for Loan");
        System.out.println("8. View All Accounts (Admin)");
        System.out.println("9. Close Account");
        System.out.println("10. Exit");
        System.out.println("===============================");
    }

    private static void openAccount() throws BankingException {
        System.out.println("\n-- Open New Account --");
        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        System.out.println("3. Fixed Deposit Account");
        System.out.print("Select Type: ");
        String type = scanner.nextLine();

        System.out.print("Enter Holder Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Initial Deposit: ₹");
        double initialDeposit = Double.parseDouble(scanner.nextLine());
        
        String accNo = AccountNumberGenerator.generateAccountNumber();
        Account newAccount;

        switch (type) {
            case "1":
                newAccount = new SavingsAccount(accNo, name, initialDeposit, 4.0, 500.0);
                break;
            case "2":
                newAccount = new CurrentAccount(accNo, name, initialDeposit, 1000.0);
                break;
            case "3":
                System.out.print("Enter Duration in Months: ");
                int months = Integer.parseInt(scanner.nextLine());
                newAccount = new FixedDepositAccount(accNo, name, initialDeposit, LocalDate.now().plusMonths(months));
                break;
            default:
                System.out.println("Invalid account type.");
                return;
        }

        bank.openAccount(newAccount);
        System.out.println("Account successfully opened!");
        System.out.println("Your new account number is: " + accNo);
        TransactionLogger.log("Opened new " + newAccount.getAccountType() + " with Account No: " + accNo);
    }

    private static void depositMoney() throws BankingException {
        System.out.print("\nEnter Account Number: ");
        String accNo = scanner.nextLine();
        Account account = bank.findAccount(accNo);

        System.out.print("Enter Deposit Amount: ₹");
        double amount = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Add a note (optional): ");
        String note = scanner.nextLine();

        if (note.isEmpty()) {
            account.deposit(amount);
        } else {
            account.deposit(amount, note);
        }
        bank.updateAccount(account); // Sync to Supabase
        
        System.out.println("Successfully deposited ₹" + amount + " into " + accNo);
        TransactionLogger.log("Deposited ₹" + amount + " to " + accNo);
    }

    private static void withdrawMoney() throws BankingException {
        System.out.print("\nEnter Account Number: ");
        String accNo = scanner.nextLine();
        Account account = bank.findAccount(accNo);

        System.out.print("Enter Withdrawal Amount: ₹");
        double amount = Double.parseDouble(scanner.nextLine());

        account.withdraw(amount);
        bank.updateAccount(account); // Sync to Supabase
        
        System.out.println("Successfully withdrew ₹" + amount + " from " + accNo);
        TransactionLogger.log("Withdrew ₹" + amount + " from " + accNo);
    }

    private static void transferFunds() throws BankingException {
        System.out.print("\nEnter Source Account Number: ");
        String srcAccNo = scanner.nextLine();
        Account srcAccount = bank.findAccount(srcAccNo);

        if (!(srcAccount instanceof Transferable)) {
            System.out.println("Transfers are not supported for this account type (" + srcAccount.getAccountType() + ").");
            return;
        }

        System.out.print("Enter Target Account Number: ");
        String destAccNo = scanner.nextLine();
        Account destAccount = bank.findAccount(destAccNo);

        System.out.print("Enter Transfer Amount: ₹");
        double amount = Double.parseDouble(scanner.nextLine());

        ((Transferable) srcAccount).transfer(destAccount, amount);
        bank.updateAccount(srcAccount); // Sync to Supabase
        bank.updateAccount(destAccount); // Sync to Supabase
        
        System.out.println("Successfully transferred ₹" + amount + " from " + srcAccNo + " to " + destAccNo);
        TransactionLogger.log("Transferred ₹" + amount + " from " + srcAccNo + " to " + destAccNo);
    }

    private static void checkBalance() throws BankingException {
        System.out.print("\nEnter Account Number: ");
        String accNo = scanner.nextLine();
        Account account = bank.findAccount(accNo);

        System.out.println("Account Holder: " + account.getHolderName());
        System.out.println("Current Balance: ₹" + account.getBalance());
        System.out.println("Account Type: " + account.getAccountType());
    }

    private static void viewHistory() throws BankingException {
        System.out.print("\nEnter Account Number: ");
        String accNo = scanner.nextLine();
        Account account = bank.findAccount(accNo);

        account.printStatement();
    }

    private static void applyForLoan() throws BankingException {
        System.out.print("\nEnter Account Number: ");
        String accNo = scanner.nextLine();
        Account account = bank.findAccount(accNo);

        if (!(account instanceof LoanEligible)) {
            System.out.println("Loans are not supported for this account type (" + account.getAccountType() + ").");
            return;
        }

        System.out.print("Enter Output Loan Amount: ₹");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter Loan Tenure in Months: ");
        int tenure = Integer.parseInt(scanner.nextLine());

        LoanEligible loanAccount = (LoanEligible) account;
        loanAccount.applyLoan(amount, tenure);
        bank.updateAccount(account); // Sync to Supabase
        
        TransactionLogger.log("Loan application for ₹" + amount + " submitted by " + accNo);
    }

    private static void viewAllAccounts() {
        System.out.println("\n-- All Accounts (Admin view) --");
        List<Account> accounts = bank.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        for (Account acc : accounts) {
            System.out.printf("[%s] %s | Balance: ₹%.2f | Type: %s\n", 
                acc.getAccountNumber(), acc.getHolderName(), acc.getBalance(), acc.getAccountType());
        }
        System.out.printf("Total Bank Deposits: ₹%.2f\n", bank.getTotalDeposits());
    }

    private static void closeAccount() throws BankingException {
        System.out.print("\nEnter Account Number to Close: ");
        String accNo = scanner.nextLine();
        
        // Ensure it exists first
        Account account = bank.findAccount(accNo);
        double balance = account.getBalance();
        
        System.out.print("Are you sure you want to close account " + accNo + "? (Y/N): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("Y")) {
            bank.closeAccount(accNo);
            System.out.println("Account " + accNo + " closed successfully.");
            if (balance > 0) {
                System.out.println("Disbursing remaining funds: ₹" + balance);
            }
            TransactionLogger.log("Closed account: " + accNo);
        } else {
            System.out.println("Action cancelled.");
        }
    }
}

