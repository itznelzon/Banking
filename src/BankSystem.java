import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BankSystem {
    private final List<Bank> allAccounts;
    private final InputValidator validator;
    private final DatabaseManager databaseManager;

    public BankSystem() {
        this.allAccounts = new ArrayList<>();
        this.validator = new InputValidator();
        this.databaseManager = new DatabaseManager();
        reloadAccountsFromDatabase();
    }

    public final void reloadAccountsFromDatabase() {
        allAccounts.clear();
        allAccounts.addAll(databaseManager.loadAccounts());
    }

    public Bank registerAccount(String accountHolder, int age, String address, String gmail,
                                String telephone, String accountUsername, String pinText) {
        validateRegistrationInput(accountHolder, age, address, gmail, telephone, accountUsername, pinText);

        String normalizedUsername = accountUsername.trim();

        if (databaseManager.accountUsernameExists(normalizedUsername)) {
            throw new IllegalArgumentException("Username is already taken. Choose another one.");
        }

        String hashedPin = PasswordUtil.hashPassword(pinText);
        Bank account = new Bank(
            accountHolder.trim(),
            age,
            address.trim(),
            gmail.trim(),
            telephone,
            normalizedUsername,
            0.0,
            hashedPin,
            0.0
        );

        int newAccountId = databaseManager.insertAccount(account);
        account.setAccountId(newAccountId);
        allAccounts.add(account);
        return account;
    }

    public void deposit(Bank account, double amount) {
        account.deposit(amount);
        databaseManager.updateAccountFinancials(account);
        databaseManager.logTransaction(account.getAccountId(), account.getAccountUsername(), "DEPOSIT", amount, "Deposited to checking account", account.getBalance(), account.getSavingsBalance());
    }

    public void withdraw(Bank account, double amount) {
        account.withdraw(amount);
        databaseManager.updateAccountFinancials(account);
        databaseManager.logTransaction(account.getAccountId(), account.getAccountUsername(), "WITHDRAW", amount, "Withdrew from checking account", account.getBalance(), account.getSavingsBalance());
    }

    public void transferToSavings(Bank account, double amount) {
        account.transferToSavings(amount);
        databaseManager.updateAccountFinancials(account);
        databaseManager.logTransaction(account.getAccountId(), account.getAccountUsername(), "TRANSFER_TO_SAVINGS", amount, "Transferred to savings account", account.getBalance(), account.getSavingsBalance());
    }

    public void withdrawFromSavings(Bank account, double amount) {
        account.withdrawFromSavings(amount);
        databaseManager.updateAccountFinancials(account);
        databaseManager.logTransaction(account.getAccountId(), account.getAccountUsername(), "WITHDRAW_FROM_SAVINGS", amount, "Withdrew from savings account", account.getBalance(), account.getSavingsBalance());
    }

    public double requestLoan(Bank account, double amount) {
        double totalLoan = account.requestLoan(amount);
        databaseManager.updateAccountFinancials(account);
        databaseManager.logTransaction(account.getAccountId(), account.getAccountUsername(), "REQUEST_LOAN", amount, "Loan requested - Total: " + totalLoan, account.getBalance(), account.getSavingsBalance());
        return totalLoan;
    }

    public void payLoanInFull(Bank account) {
        double loanPaid = account.getLoanAmount();
        account.payLoanInFull();
        databaseManager.updateAccountFinancials(account);
        databaseManager.logTransaction(account.getAccountId(), account.getAccountUsername(), "PAY_LOAN", loanPaid, "Loan paid in full", account.getBalance(), account.getSavingsBalance());
    }

    public Bank loginAccount(String accountUsername, String pinText) {
        String usernameForLog = accountUsername == null ? "" : accountUsername.trim();

        String usernameError = validator.validateUsername(accountUsername);
        if (usernameError != null) {
            databaseManager.logLoginAttempt("CLIENT", usernameForLog, false, usernameError);
            throw new IllegalArgumentException(usernameError);
        }

        String pinError = validator.validatePin(pinText);
        if (pinError != null) {
            databaseManager.logLoginAttempt("CLIENT", usernameForLog, false, pinError);
            throw new IllegalArgumentException(pinError);
        }

        Bank account = databaseManager.findAccountByUsername(usernameForLog);
        if (account != null) {
            String storedPin = account.getPinHash();
            if (storedPin != null && PasswordUtil.verifyPassword(pinText, storedPin)) {
                if (!PasswordUtil.isPbkdf2Hash(storedPin)) {
                    String hashedPin = PasswordUtil.hashPassword(pinText);
                    account.setPinHash(hashedPin);
                    databaseManager.updateAccountPinHash(account.getAccountId(), hashedPin);
                }

                for (int i = 0; i < allAccounts.size(); i++) {
                    if (allAccounts.get(i).getAccountId() == account.getAccountId()) {
                        allAccounts.set(i, account);
                        databaseManager.logLoginAttempt("CLIENT", usernameForLog, true, "Login successful");
                        return account;
                    }
                }

                allAccounts.add(account);
                databaseManager.logLoginAttempt("CLIENT", usernameForLog, true, "Login successful");
                return account;
            }
        }

        databaseManager.logLoginAttempt("CLIENT", usernameForLog, false, "Invalid username or PIN");
        throw new IllegalArgumentException("Invalid username or PIN.");
    }

    public boolean loginAdmin(String username, String password) {
        if (username == null || password == null) {
            databaseManager.logLoginAttempt("ADMIN", "", false, "Missing username or password");
            return false;
        }

        String usernameForLog = username.trim();
        boolean success = databaseManager.validateAdminCredentials(usernameForLog, password);
        databaseManager.logLoginAttempt("ADMIN", usernameForLog, success, success ? "Login successful" : "Invalid admin credentials");
        return success;
    }

    public int getTotalAccounts() {
        reloadAccountsFromDatabase();
        return allAccounts.size();
    }

    public List<Bank> getAllAccounts() {
        reloadAccountsFromDatabase();
        return Collections.unmodifiableList(allAccounts);
    }

    public Bank getAccountByIndex(int index) {
        if (index < 0 || index >= allAccounts.size()) {
            throw new IllegalArgumentException("Invalid account selection.");
        }
        return allAccounts.get(index);
    }

    public void deleteAccountByIndex(int index) {
        if (index < 0 || index >= allAccounts.size()) {
            throw new IllegalArgumentException("Invalid account selection.");
        }
        Bank removed = allAccounts.remove(index);
        databaseManager.deleteAccount(removed.getAccountId());
    }

    public boolean isUsernameTaken(String username) {
        String cleanUsername = username == null ? "" : username.trim();
        return databaseManager.accountUsernameExists(cleanUsername);
    }

    public List<String[]> getRecentLoginLogs(int limit) {
        return databaseManager.loadRecentLoginLogs(limit);
    }

    private void validateRegistrationInput(String accountHolder, int age, String address, String gmail,
                                           String telephone, String accountUsername, String pinText) {
        String[] errors = {
            validator.validateName(accountHolder),
            validator.validateAge(age),
            validator.validateAddress(address),
            validator.validateGmail(gmail),
            validator.validateTelephone(telephone),
            validator.validateUsername(accountUsername),
            validator.validatePin(pinText)
        };

        for (String error : errors) {
            if (error != null) {
                throw new IllegalArgumentException(error);
            }
        }
    }
}
