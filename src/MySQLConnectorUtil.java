import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL Connector Utility - Simplifies database operations for jameskylebank.
 * Provides easy-to-use methods for connecting and querying the MySQL database.
 */
public class MySQLConnectorUtil {

    // Database name constant - lowercase as required
    private static final String DATABASE_NAME = "jameskylebank";

    /**
     * Get the database name (lowercase).
     *
     * @return database name: "jameskylebank"
     */
    public static String getDatabaseName() {
        return DATABASE_NAME;
    }

    /**
     * Check if MySQL connector is available and working.
     *
     * @return true if connector is loaded and accessible, false otherwise
     */
    public static boolean isConnectorReady() {
        return DatabaseConnection.isMySqlConnectorAvailable();
    }

    /**
     * Get connector version information.
     *
     * @return connector version string (e.g., "9.6.0")
     */
    public static String getConnectorVersion() {
        return DatabaseConnection.getMySqlConnectorVersion();
    }

    /**
     * Get full database connection status report.
     *
     * @return formatted string with database, host, user, and table info
     */
    public static String getConnectionStatus() {
        return DatabaseConnection.getTableInfo();
    }

    /**
     * Get number of accounts in the database.
     *
     * @return count of rows in accounts table
     */
    public static int getAccountsCount() {
        return DatabaseConnection.getTableAccountCount();
    }

    /**
     * Get number of login log entries.
     *
     * @return count of rows in login_logs table
     */
    public static int getLoginLogsCount() {
        return DatabaseConnection.getTableLoginLogCount();
    }

    /**
     * Get a connection to the jameskylebank database.
     *
     * @return active MySQL connection
     * @throws SQLException if connection fails
     */
    public static Connection connect() throws SQLException {
        return DatabaseConnection.getJamesKylebankConnection();
    }

    /**
     * Connect to jameskylebank database explicitly by name.
     * This function makes it clear you are connecting to "jameskylebank" (lowercase).
     *
     * @return active MySQL connection to jameskylebank
     * @throws SQLException if connection fails
     */
    public static Connection connectToJameskylebank() throws SQLException {
        return DatabaseConnection.getConnectionForDatabase("jameskylebank");
    }

    /**
     * Get a connection to any database by name.
     *
     * @param databaseName name of database to connect to
     * @return active MySQL connection
     * @throws SQLException if connection fails
     */
    public static Connection connectToDatabase(String databaseName) throws SQLException {
        return DatabaseConnection.getConnectionForDatabase(databaseName);
    }

    /**
     * Execute a simple SELECT query and return all results.
     *
     * @param sql SQL query string (e.g., "SELECT * FROM accounts")
     * @return list of row data, each row is an Object array
     * @throws SQLException if query execution fails
     */
    public static List<Object[]> executeQuery(String sql) throws SQLException {
        List<Object[]> results = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            int columnCount = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                results.add(row);
            }
        }

        return results;
    }

    /**
     * Execute an INSERT, UPDATE, or DELETE statement.
     *
     * @param sql SQL update statement
     * @return number of affected rows
     * @throws SQLException if update execution fails
     */
    public static int executeUpdate(String sql) throws SQLException {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeUpdate();
        }
    }

    /**
     * Execute a query with a single result (e.g., COUNT query).
     *
     * @param sql SQL query that returns a single value
     * @return the first column value from the first row
     * @throws SQLException if query fails
     */
    public static Object executeSingleQuery(String sql) throws SQLException {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        }
    }

    /**
     * Test the connection to jameskylebank database.
     *
     * @return true if connection and ping are successful
     */
    public static boolean testConnection() {
        try {
            DatabaseConnection.ensureMySqlDriverLoaded();
            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                 ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Print connection diagnostics to console.
     * Useful for debugging connection issues.
     */
    public static void printDiagnostics() {
        System.out.println("=== MySQL Connector Diagnostics ===");
        System.out.println("Connector Available: " + isConnectorReady());
        System.out.println("Connector Version: " + getConnectorVersion());
        System.out.println("Connection Test: " + (testConnection() ? "PASS" : "FAIL"));
        System.out.println("\n" + getConnectionStatus());
    }
}
