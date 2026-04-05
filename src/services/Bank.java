package services;

import database.DatabaseManager;
import exceptions.AccountNotFoundException;
import exceptions.DuplicateAccountException;
import exceptions.BankingException;
import models.Account;
import models.CurrentAccount;
import models.FixedDepositAccount;
import models.SavingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the central Bank singleton managing all accounts via Supabase.
 */
public class Bank {
    private static Bank instance;
    private final String bankName;

    // Private constructor for singleton
    private Bank() {
        this.bankName = "BWU-BANK";
    }

    public static synchronized Bank getInstance() {
        if (instance == null) {
            instance = new Bank();
        }
        return instance;
    }

    public void openAccount(Account account) throws DuplicateAccountException, BankingException {
        // First check if it exists
        try (Connection conn = DatabaseManager.getConnection()) {
            String checkSql = "SELECT account_number FROM Accounts WHERE account_number = ?";
            try (PreparedStatement pt = conn.prepareStatement(checkSql)) {
                pt.setString(1, account.getAccountNumber());
                ResultSet rs = pt.executeQuery();
                if (rs.next()) {
                    throw new DuplicateAccountException(account.getAccountNumber());
                }
            }

            String insertSql = "INSERT INTO Accounts (account_number, holder_name, balance, account_type, interest_rate, min_balance, overdraft_limit, maturity_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, account.getAccountNumber());
                stmt.setString(2, account.getHolderName());
                stmt.setDouble(3, account.getBalance());
                stmt.setString(4, account.getAccountType());
                
                // Defaults for subclass specifics
                stmt.setObject(5, null);
                stmt.setObject(6, null);
                stmt.setObject(7, null);
                stmt.setObject(8, null);

                if (account instanceof SavingsAccount) {
                    stmt.setDouble(5, ((SavingsAccount) account).getInterestRate()); // Warning: requires tweaking original model visibility or assume hardcode
                    stmt.setDouble(6, 500.0); // Hardcoded min as per original structure
                } else if (account instanceof CurrentAccount) {
                    stmt.setDouble(7, 1000.0); // Hardcoded overdraft
                } else if (account instanceof FixedDepositAccount) {
                    stmt.setDate(8, Date.valueOf(java.time.LocalDate.now().plusMonths(12)));
                }

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new BankingException("Database Error: " + e.getMessage());
        }
    }

    public Account findAccount(String accNo) throws AccountNotFoundException, BankingException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM Accounts WHERE account_number = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, accNo);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("holder_name");
                    double bal = rs.getDouble("balance");
                    String type = rs.getString("account_type");
                    
                    if(type.equalsIgnoreCase("Savings")) {
                        return new SavingsAccount(accNo, name, bal, rs.getDouble("interest_rate"), rs.getDouble("min_balance"));
                    } else if (type.equalsIgnoreCase("Current")) {
                        return new CurrentAccount(accNo, name, bal, rs.getDouble("overdraft_limit"));
                    } else if (type.equalsIgnoreCase("Fixed Deposit")) {
                        java.sql.Date sqlDate = rs.getDate("maturity_date");
                        java.time.LocalDate matDate = sqlDate != null ? sqlDate.toLocalDate() : java.time.LocalDate.now();
                        return new FixedDepositAccount(accNo, name, bal, matDate);
                    }
                    // Fallback
                    return new SavingsAccount(accNo, name, bal, 4.0, 500.0);
                } else {
                    throw new AccountNotFoundException(accNo);
                }
            }
        } catch (SQLException e) {
            throw new BankingException("Database Error: " + e.getMessage());
        }
    }

    public void updateAccount(Account account) throws BankingException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE Accounts SET balance = ? WHERE account_number = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, account.getBalance());
                stmt.setString(2, account.getAccountNumber());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new BankingException("Database Update Error: " + e.getMessage());
        }
    }

    public void closeAccount(String accNo) throws AccountNotFoundException, BankingException {
        // verify exist
        findAccount(accNo);
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM Accounts WHERE account_number = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, accNo);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new BankingException("Database Error: " + e.getMessage());
        }
    }

    public List<Account> getAllAccounts() {
        List<Account> accountsList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM Accounts";
            try (java.sql.Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    String accNo = rs.getString("account_number");
                    String name = rs.getString("holder_name");
                    double bal = rs.getDouble("balance");
                    accountsList.add(new SavingsAccount(accNo, name, bal, 4.0, 500.0)); // Simplified for summary view
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error retrieving accounts: " + e.getMessage());
        }
        return accountsList;
    }

    public double getTotalDeposits() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT SUM(balance) as total FROM Accounts";
            try (java.sql.Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if(rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error retrieving total: " + e.getMessage());
        }
        return 0;
    }
    
    public String getBankName() {
        return bankName;
    }
}
