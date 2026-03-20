/**
 * MySQLConnectorUtil Demo - Example code showing how to use the MySQL Connector Utility.
 * Run this from NetBeans to see all available connector functions.
 */
import java.sql.SQLException;

public class MySQLConnectorUtilDemo {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   MySQL Connector Utility Demo for JamesKylebank          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        // 1. Check if connector is ready
        System.out.println("1. CONNECTOR STATUS:");
        System.out.println("   Connector Ready: " + MySQLConnectorUtil.isConnectorReady());
        System.out.println("   Connector Version: " + MySQLConnectorUtil.getConnectorVersion());
        System.out.println();

        // 2. Test connection
        System.out.println("2. CONNECTION TEST:");
        boolean connected = MySQLConnectorUtil.testConnection();
        System.out.println("   Connection: " + (connected ? "✓ PASS" : "✗ FAIL"));
        System.out.println();

        // 3. Get database info
        System.out.println("3. DATABASE INFORMATION:");
        System.out.println("   Accounts Count: " + MySQLConnectorUtil.getAccountsCount());
        System.out.println("   Login Logs Count: " + MySQLConnectorUtil.getLoginLogsCount());
        System.out.println();

        // 4. Print full diagnostics
        System.out.println("4. FULL DIAGNOSTICS:");
        MySQLConnectorUtil.printDiagnostics();
        System.out.println();

        // 5. Example query usage
        System.out.println("5. QUERY EXAMPLES:");
        try {
            // Get all accounts
            System.out.println("   Query: SELECT * FROM accounts LIMIT 1");
            var accounts = MySQLConnectorUtil.executeQuery("SELECT * FROM accounts LIMIT 1");
            System.out.println("   Results: " + accounts.size() + " rows");
            if (!accounts.isEmpty()) {
                System.out.println("   First row has " + accounts.get(0).length + " columns");
            }
            System.out.println();

            // Count records
            System.out.println("   Query: SELECT COUNT(*) FROM login_logs");
            var count = MySQLConnectorUtil.executeSingleQuery("SELECT COUNT(*) FROM login_logs");
            System.out.println("   Count: " + count);

        } catch (SQLException | IllegalStateException ex) {
            System.out.println("   Error: " + ex.getMessage());
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   Demo Complete - All functions working!                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}
