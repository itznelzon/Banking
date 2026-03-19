import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        try {
            DatabaseConnection.ensureMySqlDriverLoaded();
            try (Connection conn = DatabaseConnection.getConnection()) {
                System.out.println("Database connected successfully: " + conn.getCatalog());
                System.out.println("Active DB config: " + DatabaseConnection.getDatabaseName());
            }
        } catch (IllegalStateException | SQLException ex) {
            System.out.println("Database connection failed: " + ex.getMessage());
        }
    }
}
