public class Bank {
    private String accountHolder;
    private int age;
    private String address;
    private String gmail;
    private String telephone;
    private String accountUsername;
    private double balance;
    private double savingsBalance;
    private int pin;
    private double loanAmount;
    private double loanInterest;
    private boolean hasActiveLoan;
    private double loan;
    public Bank(String accountHolder, int age, String address, String gmail, String telephone, 
                String accountUsername, double balance, int pin, double loan) {
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
        this.loanInterest = 0.05;
        this.hasActiveLoan = false;
        this.loan = 0.0;
    }
    
    public void checkBalance() {
        System.out.println("\n--- Account Information ---");
        System.out.println("Account Holder: " + accountHolder);
        System.out.println("Age: " + age);
        System.out.println("Address: " + address);
        System.out.println("Gmail: " + gmail);
        System.out.println("Telephone: " + telephone);
        System.out.println("Username: " + accountUsername);
        System.out.printf("Current Balance: %.2f%n", balance);
        System.out.printf("Savings Balance: %.2f%n", savingsBalance);
        System.out.printf("Total Balance: %.2f%n", (balance + savingsBalance));
        if (hasActiveLoan) {
            System.out.printf("Outstanding Loan: %.2f%n", loanAmount);
        }
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.printf(" %.2f deposited successfully.%n", amount);
            System.out.printf("New Balance: %.2f%n", balance);
        } else {
            System.out.println("Invalid deposit amount!");
        }
    }
    
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance && !hasActiveLoan) {
            balance -= amount;
            System.out.printf("%.2f withdrawn successfully.%n", amount);
            System.out.printf("New Balance: %.2f%n", balance);
        } else if (amount > balance) {
            System.out.println("Insufficient funds!");
        } else {
            System.out.println("Invalid withdrawal amount!");
        } 
    }

    public void payloan() {
        if (balance < loan) {
            System.out.print("You dont have enough credits.(yes/no): ");
        } else {
            balance -= loan;
            hasActiveLoan = false;
            loan = 0.0;
            System.out.println("New Balance " + balance);
        }
    }
    
    public void transferToSavings(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid transfer amount!");
            return;
        }
        
        if (amount > balance) {
            System.out.println("Insufficient funds in checking account!");
            System.out.printf("Available balance: %.2f%n", balance);
            return;
        }
        
        balance -= amount;
        savingsBalance += amount;
        System.out.printf("\n %.2f transferred to savings successfully!%n", amount);
        System.out.printf("Checking Balance: %.2f%n", balance);
        System.out.printf("Savings Balance: %.2f%n", savingsBalance);
    }
    
    public void withdrawFromSavings(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount!");
            return;
        }
        
        if (amount > savingsBalance) {
            System.out.println("Insufficient funds in savings account!");
            System.out.printf("Available savings balance: %.2f%n", savingsBalance);
            return;
        }
        
        savingsBalance -= amount;
        balance += amount;
        System.out.printf("\n %.2f withdrawn from savings successfully!%n", amount);
        System.out.printf("Checking Balance: %.2f%n", balance);
        System.out.printf("Savings Balance: %.2f%n", savingsBalance);
    }
    
    public void viewSavingsBalance() {
        System.out.println("\n--- Savings Account ---");
        System.out.printf("Savings Balance: %.2f%n", savingsBalance);
    }
    
    public void requestLoan(double amount) {
        if (hasActiveLoan) {
            System.out.println("\nYou already have an active loan!");
            System.out.printf("Outstanding loan amount: %.2f%n", loanAmount);
            System.out.println("Please pay off your current loan before requesting a new one.");
            return;
        }
        
        if (amount <= 0) {
            System.out.println("Invalid loan amount!");
            return;
        }
        
        double maxLoanAmount = balance * 10;
        
        if (amount > maxLoanAmount) {
            System.out.println("\n Loan request denied!");
            System.out.printf("Maximum loan amount based on your balance: %.2f%n", maxLoanAmount);
            return;
        }
        
        double totalLoanWithInterest = amount + (amount * loanInterest);
        loanAmount = totalLoanWithInterest;
        balance += amount;
        hasActiveLoan = true;
        loan += totalLoanWithInterest;
        
        System.out.println("\n Loan approved!");
        System.out.printf("Loan amount: %.2f%n", amount);
        System.out.printf("Interest rate: %.0f%%%n", loanInterest * 100);
        System.out.printf("Total to repay: %.2f%n", totalLoanWithInterest);
        System.out.printf("New Balance: %.2f%n", balance);
    }
    
    public void viewLoanStatus() {
        System.out.println("\n--- Loan Status ---");
        if (hasActiveLoan) {
            System.out.printf("Outstanding Loan: %.2f%n", loanAmount);
            System.out.printf("Interest Rate: %.0f%%%n", loanInterest * 100);
            System.out.println("\nTo repay your loan, use the Withdraw option.");
            System.out.println("(Note: In this simple version, manual repayment tracking is needed)");
        } else {
            System.out.println("No active loans.");
        }
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
        return hasActiveLoan;
    }
    
    public double getLoanAmount() {
        return loanAmount;
    }
    
    public double getLoanInterest() {
        return loanInterest;
    }
}
