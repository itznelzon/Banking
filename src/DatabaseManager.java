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
        String sql = "SELECT id, password FROM admins WHERE username = ?";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int adminId = rs.getInt("id");
                    String storedPassword = rs.getString("password");

                    boolean isValid = PasswordUtil.verifyPassword(password, storedPassword);
                    if (isValid && !PasswordUtil.isPbkdf2Hash(storedPassword)) {
                        updateAdminPasswordHash(adminId, PasswordUtil.hashPassword(password));
                    }
                    return isValid;
                }
            }

            return false;
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot validate admin credentials in MySQL. " + ex.getMessage(), ex);
        }
    }

    public void logLoginAttempt(String role, String username, boolean success, String notes) {
        String sql = "INSERT INTO login_logs (role, username, success, notes) VALUES (?, ?, ?, ?)";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            stmt.setString(2, username == null ? "" : username);
            stmt.setBoolean(3, success);
            stmt.setString(4, notes);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot save login log into MySQL. " + ex.getMessage(), ex);
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
                    rs.getString("pin")
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

    public boolean accountUsernameExists(String username) {
        String sql = "SELECT 1 FROM accounts WHERE username = ? LIMIT 1";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot check account username in MySQL. " + ex.getMessage(), ex);
        }
    }

    public Bank findAccountByUsername(String username) {
        String sql =
            "SELECT id, account_holder, age, address, gmail, telephone, username, pin, checking_balance, savings_balance, loan_amount " +
            "FROM accounts WHERE username = ? LIMIT 1";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return new Bank(
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
                    rs.getString("pin")
                );
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot load account by username from MySQL. " + ex.getMessage(), ex);
        }
    }

    public List<String[]> loadRecentLoginLogs(int limit) {
        int safeLimit = Math.max(1, limit);
        List<String[]> logs = new ArrayList<>();
        String sql = "SELECT id, role, username, success, notes, logged_at FROM login_logs ORDER BY id DESC LIMIT ?";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, safeLimit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new String[] {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("role"),
                        rs.getString("username"),
                        rs.getBoolean("success") ? "TRUE" : "FALSE",
                        rs.getString("notes"),
                        rs.getString("logged_at")
                    });
                }
            }

            return logs;
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot load login logs from MySQL. " + ex.getMessage(), ex);
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

    public void updateAccountPinHash(int accountId, String pinHash) {
        String sql = "UPDATE accounts SET pin = ? WHERE id = ?";

        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pinHash);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot update account PIN hash in MySQL. " + ex.getMessage(), ex);
        }
    }

    private void bindAccountFields(PreparedStatement stmt, Bank account) throws SQLException {
        stmt.setString(1, account.getAccountHolder());
        stmt.setInt(2, account.getAge());
        stmt.setString(3, account.getAddress());
        stmt.setString(4, account.getGmail());
        stmt.setString(5, account.getTelephone());
        stmt.setString(6, account.getAccountUsername());
        stmt.setString(7, account.getPinHash());
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
            "pin VARCHAR(255) NOT NULL," +
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

        String createLoginLogsTableSql =
            "CREATE TABLE IF NOT EXISTS login_logs (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "role VARCHAR(20) NOT NULL," +
            "username VARCHAR(80) NOT NULL," +
            "success BOOLEAN NOT NULL," +
            "notes VARCHAR(255)," +
            "logged_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
            ")";

        try (Connection conn = openConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createAccountsTableSql);
            stmt.execute(createAdminsTableSql);
            stmt.execute(createLoginLogsTableSql);
            migrateLegacyAccountPins(conn);
            ensureDefaultAdmin(conn);
            upgradeLegacyAdminPasswords(conn);
        } catch (SQLException ex) {
            throw new IllegalStateException(
                "Unable to initialize MySQL schema. Check if XAMPP MySQL is running and database '"
                    + DatabaseConnection.getDatabaseName() + "' exists. " + ex.getMessage(),
                ex
            );
        }
    }

    private void migrateLegacyAccountPins(Connection conn) throws SQLException {
        String typeQuery =
            "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'accounts' AND COLUMN_NAME = 'pin'";

        String dataType = null;
        try (PreparedStatement stmt = conn.prepareStatement(typeQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                dataType = rs.getString("DATA_TYPE");
            }
        }

        if (dataType == null || dataType.equalsIgnoreCase("varchar")) {
            return;
        }

        List<int[]> legacyPins = new ArrayList<>();
        String selectPinsSql = "SELECT id, pin FROM accounts";
        try (PreparedStatement stmt = conn.prepareStatement(selectPinsSql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                legacyPins.add(new int[] {rs.getInt("id"), rs.getInt("pin")});
            }
        }

        try (Statement alterStmt = conn.createStatement()) {
            alterStmt.execute("ALTER TABLE accounts MODIFY pin VARCHAR(255) NOT NULL");
        }

        String updateSql = "UPDATE accounts SET pin = ? WHERE id = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            for (int[] item : legacyPins) {
                String hashed = PasswordUtil.hashPassword(String.valueOf(item[1]));
                updateStmt.setString(1, hashed);
                updateStmt.setInt(2, item[0]);
                updateStmt.addBatch();
            }
            if (!legacyPins.isEmpty()) {
                updateStmt.executeBatch();
            }
        }
    }

    private void ensureDefaultAdmin(Connection conn) throws SQLException {
        String existsSql = "SELECT id FROM admins WHERE username = ?";
        try (PreparedStatement existsStmt = conn.prepareStatement(existsSql)) {
            existsStmt.setString(1, "Admin");
            try (ResultSet rs = existsStmt.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
        }

        String insertSql = "INSERT INTO admins (username, password) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, "Admin");
            insertStmt.setString(2, PasswordUtil.hashPassword("P@&&Word$"));
            insertStmt.executeUpdate();
        }
    }

    private void upgradeLegacyAdminPasswords(Connection conn) throws SQLException {
        String selectSql = "SELECT id, password FROM admins";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String storedPassword = rs.getString("password");
                if (!PasswordUtil.isPbkdf2Hash(storedPassword)) {
                    updateAdminPasswordHash(id, PasswordUtil.hashPassword(storedPassword));
                }
            }
        }
    }

    private void updateAdminPasswordHash(int adminId, String hashedPassword) {
        String updateSql = "UPDATE admins SET password = ? WHERE id = ?";
        try (Connection conn = openConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            updateStmt.setString(1, hashedPassword);
            updateStmt.setInt(2, adminId);
            updateStmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot update admin password hash in MySQL. " + ex.getMessage(), ex);
        }
    }

    private Connection openConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    private void ensureDriverPresent() {
        DatabaseConnection.ensureMySqlDriverLoaded();
    }
}
