import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankSystem {
    private static final String ADMIN_USERNAME = "Admin";
    private static final String ADMIN_PASSWORD = "P@&&Word$";

    private final List<Bank> allAccounts;
    private final InputValidator validator;

    public BankSystem() {
        this.allAccounts = new ArrayList<>();
        this.validator = new InputValidator();
    }

    public Bank registerAccount(String accountHolder, int age, String address, String gmail,
                                String telephone, String accountUsername, String pinText) {
        validateRegistrationInput(accountHolder, age, address, gmail, telephone, accountUsername, pinText);

        if (isUsernameTaken(accountUsername)) {
            throw new IllegalArgumentException("Username is already taken. Choose another one.");
        }

        int pin = Integer.parseInt(pinText);
        Bank account = new Bank(
            accountHolder.trim(),
            age,
            address.trim(),
            gmail.trim(),
            telephone,
            accountUsername.trim(),
            0.0,
            pin,
            0.0
        );

        allAccounts.add(account);
        return account;
    }

    public Bank loginAccount(String accountUsername, String pinText) {
        String usernameError = validator.validateUsername(accountUsername);
        if (usernameError != null) {
            throw new IllegalArgumentException(usernameError);
        }

        String pinError = validator.validatePin(pinText);
        if (pinError != null) {
            throw new IllegalArgumentException(pinError);
        }

        int pin = Integer.parseInt(pinText);

        for (Bank account : allAccounts) {
            if (account.getAccountUsername().equals(accountUsername.trim()) && account.getPin() == pin) {
                return account;
            }
        }

        throw new IllegalArgumentException("Invalid username or PIN.");
    }

    public boolean loginAdmin(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    public int getTotalAccounts() {
        return allAccounts.size();
    }

    public List<Bank> getAllAccounts() {
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
        allAccounts.remove(index);
    }

    public boolean isUsernameTaken(String username) {
        String cleanUsername = username == null ? "" : username.trim();
        for (Bank account : allAccounts) {
            if (account.getAccountUsername().equalsIgnoreCase(cleanUsername)) {
                return true;
            }
        }
        return false;
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
