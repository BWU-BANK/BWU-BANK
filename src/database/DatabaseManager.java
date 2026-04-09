package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseManager {
    // Supabase PostgreSQL JDBC Connection Strings
    // Load from .env file or fallback to OS environment variables
    private static String getEnvConfig(String key) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String val = dotenv.get(key);
        if (val == null || val.isEmpty()) {
            val = System.getenv(key);
        }
        return val;
    }

    private static final String URL = getEnvConfig("SUPABASE_DB_URL");
    private static final String USER = getEnvConfig("SUPABASE_DB_USER");
    private static final String PWD = getEnvConfig("SUPABASE_DB_PASSWORD");

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
