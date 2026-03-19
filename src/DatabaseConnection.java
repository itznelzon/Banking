import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConnection {
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3306;
    // You can change this in NetBeans VM options using: -Dbank.db.name=jameskylebanks
    private static final String DB_NAME = System.getProperty("bank.db.name", "jameskylebanks");
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String SERVER_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/?useSSL=false&serverTimezone=UTC";
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        ensureDatabaseExists();
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static String getDatabaseName() {
        return DB_NAME;
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

    private static void ensureDatabaseExists() throws SQLException {
        try (Connection conn = DriverManager.getConnection(SERVER_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "`");
        }
    }
}
