package services;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Service to log transactions to a file.
 */
public class TransactionLogger {
    private static final String LOG_FILE = "transactions.log";

    /**
     * Logs a message with a timestamp to the log file.
     *
     * @param message The transaction or event message.
     */
    public static void log(String message) {
        // try-with-resources for file I/O
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(LocalDateTime.now() + " - " + message + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to transaction log: " + e.getMessage());
        }
    }
}
