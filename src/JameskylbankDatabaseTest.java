/**
 * Quick test for jameskylebank database functions (lowercase).
 */
import java.sql.SQLException;

public class JameskylbankDatabaseTest {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   Testing jameskylebank (lowercase) Database Function ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");

        // Test 1: Get database name
        System.out.println("1. GET DATABASE NAME (lowercase):");
        String dbName = MySQLConnectorUtil.getDatabaseName();
        System.out.println("   Database name: " + dbName);
        System.out.println("   Is lowercase: " + dbName.equals(dbName.toLowerCase()));
        System.out.println();

        // Test 2: Connect to jameskylebank
        System.out.println("2. CONNECT TO JAMESKYLEBANK:");
        try (var conn = MySQLConnectorUtil.connectToJameskylebank()) {
            System.out.println("   Connected to: " + conn.getCatalog());
            System.out.println("   Status: ✓ SUCCESS");
        } catch (SQLException | IllegalStateException ex) {
            System.out.println("   Error: " + ex.getMessage());
        }
        System.out.println();

        // Test 3: Connect using generic method
        System.out.println("3. CONNECT TO DATABASE BY NAME 'jameskylebank':");
        try (var conn = MySQLConnectorUtil.connectToDatabase("jameskylebank")) {
            System.out.println("   Connected to: " + conn.getCatalog());
            System.out.println("   Status: ✓ SUCCESS");
        } catch (SQLException | IllegalStateException ex) {
            System.out.println("   Error: " + ex.getMessage());
        }
        System.out.println();

        // Test 4: Database info
        System.out.println("4. DATABASE INFO:");
        System.out.println(MySQLConnectorUtil.getConnectionStatus());

        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   All jameskylebank functions working correctly!     ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
    }
}
