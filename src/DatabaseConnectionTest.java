import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        try {
            DatabaseConnection.ensureMySqlDriverLoaded();
            DatabaseManager manager = new DatabaseManager();

            try (Connection conn = DatabaseConnection.getJamesKylebankConnection()) {
                boolean pingOk;
                try (PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                     ResultSet rs = stmt.executeQuery()) {
                    pingOk = rs.next() && rs.getInt(1) == 1;
                }

                int logCount;
                try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM login_logs");
                     ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    logCount = rs.getInt(1);
                }

                System.out.println("=== DATABASE CONNECTION TEST ===");
                System.out.println(DatabaseConnection.getTableInfo());
                System.out.println("Connection ping test (SELECT 1): " + (pingOk ? "PASS" : "FAIL"));
                System.out.println("login_logs rows: " + logCount);

                manager.logLoginAttempt("SYSTEM", "DatabaseConnectionTest", true, "XAMPP DB smoke test");
                System.out.println("Inserted one SYSTEM log row successfully.");
            }
        } catch (IllegalStateException | SQLException ex) {
            System.out.println("Database connection failed: " + ex.getMessage());
        }
    }
}
