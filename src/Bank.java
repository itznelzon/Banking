public class Bank {
    private static final double LOAN_INTEREST_RATE = 0.05;

    private final String accountHolder;
    private final int age;
    private final String address;
    private final String gmail;
    private final String telephone;
    private final String accountUsername;
    private final int pin;

    private double balance;
    private double savingsBalance;
    private double loanAmount;

    public Bank(String accountHolder, int age, String address, String gmail, String telephone,
                String accountUsername, double balance, int pin, double ignoredLoan) {
        this.accountHolder = accountHolder;
        this.age = age;
        this.address = address;
        this.gmail = gmail;
        this.telephone = telephone;
        this.accountUsername = accountUsername;
        this.balance = balance;
        this.savingsBalance = 0.0;
        this.pin = pin;
        this.loanAmount = 0.0;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds in checking balance.");
        }
        balance -= amount;
    }

    public void transferToSavings(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds in checking balance.");
        }
        balance -= amount;
        savingsBalance += amount;
    }

    public void withdrawFromSavings(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (amount > savingsBalance) {
            throw new IllegalArgumentException("Insufficient funds in savings balance.");
        }
        savingsBalance -= amount;
        balance += amount;
    }

    public double requestLoan(double requestedAmount) {
        if (hasActiveLoan()) {
            throw new IllegalStateException("Please pay your active loan before requesting a new one.");
        }
        if (requestedAmount <= 0) {
            throw new IllegalArgumentException("Loan amount must be greater than zero.");
        }
        double maxLoanAmount = getMaxLoanAmount();
        if (requestedAmount > maxLoanAmount) {
            throw new IllegalArgumentException(String.format("Maximum allowable loan is %.2f", maxLoanAmount));
        }

        double totalWithInterest = requestedAmount * (1 + LOAN_INTEREST_RATE);
        balance += requestedAmount;
        loanAmount = totalWithInterest;
        return totalWithInterest;
    }

    public void payLoanInFull() {
        if (!hasActiveLoan()) {
            throw new IllegalStateException("No active loan to pay.");
        }
        if (balance < loanAmount) {
            throw new IllegalArgumentException("Insufficient checking balance to pay this loan.");
        }
        balance -= loanAmount;
        loanAmount = 0.0;
    }

    public double getMaxLoanAmount() {
        return balance * 5;
    }

    public double getTotalBalance() {
        return balance + savingsBalance;
    }

    public double getNetWorth() {
        return getTotalBalance() - loanAmount;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public int getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    public String getGmail() {
        return gmail;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAccountUsername() {
        return accountUsername;
    }

    public double getBalance() {
        return balance;
    }

    public double getSavingsBalance() {
        return savingsBalance;
    }

    public int getPin() {
        return pin;
    }

    public boolean hasActiveLoan() {
        return loanAmount > 0;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public double getLoanInterest() {
        return LOAN_INTEREST_RATE;
    }
}
