public class InputValidator {
    public String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Full name is required.";
        }
        if (!name.trim().matches("[a-zA-Z ]+")) {
            return "Full name must contain only letters and spaces.";
        }
        return null;
    }

    public String validateAge(int age) {
        if (age < 18 || age > 120) {
            return "Age must be between 18 and 120.";
        }
        return null;
    }

    public String validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "Address is required.";
        }
        return null;
    }

    public String validateGmail(String gmail) {
        if (gmail == null || !gmail.trim().matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
            return "Please use a valid gmail.com address.";
        }
        return null;
    }

    public String validateTelephone(String telephone) {
        if (telephone == null || !telephone.matches("\\d{11}")) {
            return "Telephone must contain exactly 11 digits.";
        }
        return null;
    }

    public String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required.";
        }
        if (username.trim().length() < 4) {
            return "Username must be at least 4 characters.";
        }
        return null;
    }

    public String validatePin(String pinValue) {
        if (pinValue == null || !pinValue.matches("\\d{6}")) {
            return "PIN must be exactly 6 digits.";
        }
        return null;
    }

    public String validateMoneyAmount(double amount) {
        if (amount <= 0) {
            return "Amount must be greater than zero.";
        }
        return null;
    }
}