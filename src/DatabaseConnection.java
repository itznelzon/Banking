import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class DatabaseConnection {
    private static final String DB_HOST = resolveString("bank.db.host", "BANK_DB_HOST", "localhost");
    private static final int DB_PORT = resolvePort("bank.db.port", "BANK_DB_PORT", 3306);
    // You can change this in NetBeans VM options using: -Dbank.db.name=jameskylebank
    private static final String DB_NAME = resolveString("bank.db.name", "BANK_DB_NAME", "jameskylebank");
    private static final String DB_USER = resolveString("bank.db.user", "BANK_DB_USER", "root");
    private static final String DB_PASSWORD = resolveString("bank.db.password", "BANK_DB_PASSWORD", "");
    private static final String SERVER_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/?useSSL=false&serverTimezone=UTC";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        ensureDatabaseExists();
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static Connection getJamesKylebankConnection() throws SQLException {
        return getConnectionForDatabase("jameskylebank");
    }

    public static Connection getConnectionForDatabase(String databaseName) throws SQLException {
        String resolvedName = sanitizeDatabaseName(databaseName);
        ensureDatabaseExists(resolvedName);
        String targetUrl = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + resolvedName + "?useSSL=false&serverTimezone=UTC";
        return DriverManager.getConnection(targetUrl, DB_USER, DB_PASSWORD);
    }

    public static String getDatabaseName() {
        return DB_NAME;
    }

    public static String getDatabaseHost() {
        return DB_HOST;
    }

    public static int getDatabasePort() {
        return DB_PORT;
    }

    public static String getDatabaseUser() {
        return DB_USER;
    }

    private static String resolveString(String propertyKey, String envKey, String fallback) {
        String fromProperty = System.getProperty(propertyKey);
        if (fromProperty != null && !fromProperty.trim().isEmpty()) {
            return fromProperty.trim();
        }

        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.trim().isEmpty()) {
            return fromEnv.trim();
        }

        return fallback;
    }

    private static int resolvePort(String propertyKey, String envKey, int fallback) {
        String candidate = System.getProperty(propertyKey);
        if (candidate == null || candidate.trim().isEmpty()) {
            candidate = System.getenv(envKey);
        }

        if (candidate == null || candidate.trim().isEmpty()) {
            return fallback;
        }

        try {
            return Integer.parseInt(candidate.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    public static void ensureMySqlDriverLoaded() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(
                "MySQL JDBC driver not found. Add mysql-connector-j-9.6.0.jar to project libraries.",
                ex
            );
        }
    }

    public static boolean isMySqlConnectorAvailable() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static String getMySqlConnectorVersion() {
        try {
            Class<?> driverClass = Class.forName("com.mysql.cj.jdbc.Driver");
            Package pkg = driverClass.getPackage();
            String implementationVersion = pkg == null ? null : pkg.getImplementationVersion();
            return implementationVersion == null ? "UNKNOWN" : implementationVersion;
        } catch (ClassNotFoundException ex) {
            return "NOT_FOUND";
        }
    }

    private static void ensureDatabaseExists() throws SQLException {
        ensureDatabaseExists(DB_NAME);
    }

    private static void ensureDatabaseExists(String databaseName) throws SQLException {
        String safeName = sanitizeDatabaseName(databaseName);
        try (Connection conn = DriverManager.getConnection(SERVER_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + safeName + "`");
        }
    }

    private static String sanitizeDatabaseName(String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be empty.");
        }

        String clean = databaseName.trim();
        if (!clean.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("Database name contains invalid characters.");
        }
        return clean;
    }

    public static int getTableAccountCount() {
        try (Connection conn = getJamesKylebankConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM accounts");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot count accounts table: " + ex.getMessage(), ex);
        }
    }

    public static int getTableLoginLogCount() {
        try (Connection conn = getJamesKylebankConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM login_logs");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot count login_logs table: " + ex.getMessage(), ex);
        }
    }

    public static String getTableInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Database: ").append(DB_NAME).append("\n");
        sb.append("Host: ").append(DB_HOST).append(":").append(DB_PORT).append("\n");
        sb.append("User: ").append(DB_USER).append("\n");
        sb.append("Connector: ").append(getMySqlConnectorVersion()).append("\n");
        try {
            sb.append("Accounts table rows: ").append(getTableAccountCount()).append("\n");
            sb.append("Login logs table rows: ").append(getTableLoginLogCount()).append("\n");
        } catch (Exception ex) {
            sb.append("Table info error: ").append(ex.getMessage()).append("\n");
        }
        return sb.toString();
    }
}
