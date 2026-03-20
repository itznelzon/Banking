import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    private static final String APP_NAME = "James Kyle Bank Management System";
    private static final String DB_ERROR_TITLE = "Database Connection Error";
    private static final String DB_ERROR_MSG = "Cannot connect to XAMPP MySQL.";
    
    public static void main(String[] args) {
        initializeApplication();
    }
    
    /**
     * Main initialization function that handles the entire application startup.
     * This function orchestrates database connection, system initialization, and UI launch.
     */
    public static void initializeApplication() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Step 1: Load MySQL Driver
                loadDatabaseDriver();
                
                // Step 2: Initialize Database Connection
                boolean dbConnected = initializeDatabaseConnection();
                if (!dbConnected) {
                    showConnectionError("Failed to verify database connection.");
                    return;
                }
                
                // Step 3: Initialize Banking System
                BankSystem bankSystem = initializeBankingSystem();
                if (bankSystem == null) {
                    showConnectionError("Failed to initialize banking system.");
                    return;
                }
                
                // Step 4: Launch User Interface
                launchUserInterface(bankSystem);
                
            } catch (IllegalStateException ex) {
                showConnectionError(ex.getMessage());
            } catch (Exception ex) {
                showConnectionError("Unexpected error: " + ex.getMessage());
            }
        });
    }
    
    /**
     * Loads the MySQL JDBC driver.
     * @throws IllegalStateException if driver cannot be loaded
     */
    private static void loadDatabaseDriver() throws IllegalStateException {
        DatabaseConnection.ensureMySqlDriverLoaded();
    }
    
    /**
     * Initializes and verifies database connection.
     * @return true if connection is successful, false otherwise
     */
    private static boolean initializeDatabaseConnection() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                connection.getMetaData();
                return true;
            }
            return false;
        } catch (SQLException ex) {
            System.err.println("Database connection failed: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * Initializes the banking system with reloaded accounts from the database.
     * @return BankSystem instance if initialization successful, null otherwise
     */
    private static BankSystem initializeBankingSystem() {
        try {
            BankSystem bankSystem = new BankSystem();
            bankSystem.reloadAccountsFromDatabase();
            return bankSystem;
        } catch (Exception ex) {
            System.err.println("Banking system initialization failed: " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Launches the Swing-based user interface with the initialized banking system.
     * @param bankSystem the initialized BankSystem instance
     */
    private static void launchUserInterface(BankSystem bankSystem) {
        try {
            BankSwingUI ui = new BankSwingUI(bankSystem);
            ui.setTitle(APP_NAME);
            ui.setVisible(true);
        } catch (Exception ex) {
            showConnectionError("Failed to launch user interface: " + ex.getMessage());
        }
    }
    
    /**
     * Displays connection error message to the user.
     * @param message the error message to display
     */
    private static void showConnectionError(String message) {
        JOptionPane.showMessageDialog(
            null,
            DB_ERROR_MSG + "\n" + message,
            DB_ERROR_TITLE,
            JOptionPane.ERROR_MESSAGE
        );
    }
}