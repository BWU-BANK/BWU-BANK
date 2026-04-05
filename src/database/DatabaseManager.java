package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // Supabase PostgreSQL JDBC Connection Strings
    private static final String URL = "jdbc:postgresql://db.fpyjagnxcqqjxacbczpa.supabase.co:5432/postgres";
    private static final String USER = "postgres";
    private static final String PWD = "CHUTIYA@2026";

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PWD);
                initDatabase(); // Ensure Schema Exists
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL JDBC Driver not found. Ensure it is attached to the classpath.", e);
            }
        }
        return connection;
    }

    private static void initDatabase() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Accounts (" +
                "account_number VARCHAR(50) PRIMARY KEY," +
                "holder_name VARCHAR(100) NOT NULL," +
                "balance DOUBLE PRECISION NOT NULL," +
                "account_type VARCHAR(50) NOT NULL," +
                "interest_rate DOUBLE PRECISION," + // For Savings
                "min_balance DOUBLE PRECISION," + // For Savings
                "overdraft_limit DOUBLE PRECISION," + // For Current
                "maturity_date DATE" + // For FixedDeposit
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("[Supabase] Database Synchronized Successfully.");
        }
    }
}
