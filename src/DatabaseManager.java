import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    public DatabaseManager() {
        ensureDriverPresent();
        ensureSchema();
    }

    public boolean validateAdminCredentials(String username, String password) {
        String sql = "SELECT COUNT(*) FROM admins WHERE username = ? AND password = ?";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot validate admin credentials in MySQL. " + ex.getMessage(), ex);
        }
    }

    public List<Bank> loadAccounts() {
        List<Bank> accounts = new ArrayList<>();
        String sql = "SELECT id, account_holder, age, address, gmail, telephone, username, pin, checking_balance, savings_balance, loan_amount FROM accounts ORDER BY id";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Bank account = new Bank(
                    rs.getInt("id"),
                    rs.getString("account_holder"),
                    rs.getInt("age"),
                    rs.getString("address"),
                    rs.getString("gmail"),
                    rs.getString("telephone"),
                    rs.getString("username"),
                    rs.getDouble("checking_balance"),
                    rs.getDouble("savings_balance"),
                    rs.getDouble("loan_amount"),
                    rs.getInt("pin")
                );
                accounts.add(account);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot load accounts from MySQL. " + ex.getMessage(), ex);
        }

        return accounts;
    }

    public int insertAccount(Bank account) {
        String sql = "INSERT INTO accounts (account_holder, age, address, gmail, telephone, username, pin, checking_balance, savings_balance, loan_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindAccountFields(stmt, account);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new IllegalStateException("Account insert succeeded but no generated ID was returned.");
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot insert account into MySQL. " + ex.getMessage(), ex);
        }
    }

    public void updateAccountFinancials(Bank account) {
        String sql = "UPDATE accounts SET checking_balance = ?, savings_balance = ?, loan_amount = ? WHERE id = ?";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, account.getBalance());
            stmt.setDouble(2, account.getSavingsBalance());
            stmt.setDouble(3, account.getLoanAmount());
            stmt.setInt(4, account.getAccountId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot update account balances in MySQL. " + ex.getMessage(), ex);
        }
    }

    public void deleteAccount(int accountId) {
        String sql = "DELETE FROM accounts WHERE id = ?";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot delete account from MySQL. " + ex.getMessage(), ex);
        }
    }

    private void bindAccountFields(PreparedStatement stmt, Bank account) throws SQLException {
        stmt.setString(1, account.getAccountHolder());
        stmt.setInt(2, account.getAge());
        stmt.setString(3, account.getAddress());
        stmt.setString(4, account.getGmail());
        stmt.setString(5, account.getTelephone());
        stmt.setString(6, account.getAccountUsername());
        stmt.setInt(7, account.getPin());
        stmt.setDouble(8, account.getBalance());
        stmt.setDouble(9, account.getSavingsBalance());
        stmt.setDouble(10, account.getLoanAmount());
    }

    private void ensureSchema() {
        String createAccountsTableSql =
            "CREATE TABLE IF NOT EXISTS accounts (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "account_holder VARCHAR(120) NOT NULL," +
            "age INT NOT NULL," +
            "address VARCHAR(255) NOT NULL," +
            "gmail VARCHAR(180) NOT NULL," +
            "telephone VARCHAR(20) NOT NULL," +
            "username VARCHAR(80) NOT NULL UNIQUE," +
            "pin INT NOT NULL," +
            "checking_balance DOUBLE NOT NULL DEFAULT 0," +
            "savings_balance DOUBLE NOT NULL DEFAULT 0," +
            "loan_amount DOUBLE NOT NULL DEFAULT 0" +
            ")";

        String createAdminsTableSql =
            "CREATE TABLE IF NOT EXISTS admins (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "username VARCHAR(80) NOT NULL UNIQUE," +
            "password VARCHAR(120) NOT NULL" +
            ")";

        String seedAdminSql =
            "INSERT INTO admins (username, password) VALUES ('Admin', 'P@&&Word$') " +
            "ON DUPLICATE KEY UPDATE username = username";

        try (Connection conn = openConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createAccountsTableSql);
            stmt.execute(createAdminsTableSql);
            stmt.execute(seedAdminSql);
        } catch (SQLException ex) {
            throw new IllegalStateException(
                "Unable to initialize MySQL schema. Check if XAMPP MySQL is running and database '"
                    + DatabaseConnection.getDatabaseName() + "' exists. " + ex.getMessage(),
                ex
            );
        }
    }

    private Connection openConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    private void ensureDriverPresent() {
        DatabaseConnection.ensureMySqlDriverLoaded();
    }
}
