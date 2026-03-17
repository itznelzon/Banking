import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

public class BankSystem {
    public static ArrayList<Bank> accounts = new ArrayList<>();
    private ArrayList<Bank> allAccounts;
    private Scanner scanner;
    private InputValidator validator;
    private static final char[] ADMIN_USERNAME = {'A', 'd', 'm', 'i', 'n'};
    private static final char[] ADMIN_PASSWORD = {'P', '@', '&', '&', 'W', 'o', 'r', 'd','$'};
    
    public BankSystem(Scanner scanner) {
        this.scanner = scanner;
        this.allAccounts = new ArrayList<>(); 
        this.validator = new InputValidator();
    }
     
    public void run() {
        boolean systemRunning = true;
        Bank currentAccount = null;
        
        while (systemRunning) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Register New Account");
            System.out.println("2. Login to Existing Account");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit System");
            System.out.print("Choose an option (1-4): ");
            
            int mainChoice = scanner.nextInt();
            scanner.nextLine();
            
            switch (mainChoice) {
                case 1:
                    currentAccount = registerAccount();
                    if (currentAccount != null) {
                        allAccounts.add(currentAccount);
                    }
                    break;
                    
                case 2:
                    if (allAccounts.isEmpty()) {
                        System.out.println("\n");
                        System.out.println("  No accounts registered yet!");
                        System.out.println("  Redirecting to Account Registration...");
                        System.out.println("\n");
                        
                        currentAccount = registerAccount();
                        if (currentAccount != null) {
                            allAccounts.add(currentAccount);
                            System.out.println("\nWould you like to login now? (yes/no): ");
                            String loginNow = scanner.nextLine().trim().toLowerCase();
                            if (loginNow.equals("yes") || loginNow.equals("y")) {
                                accountMenu(currentAccount);
                            }
                        }
                    } else {
                        loginAccount();
                    }
                    break;
                    
                case 3:
                    adminLogin();
                    break;
                    
                case 4:
                    System.out.println("\nThank you for using our bank system. Goodbye!");
                    systemRunning = false;
                    break;
                    
                default:
                    System.out.println("Invalid choice! Please select 1-4.");
            }
        }
    }
    
    // ============ USER ACCOUNT METHODS ============
    
    private Bank registerAccount() {
        System.out.println("\n<===Register Account===>");
        
        String accountHolder = validator.getValidName(scanner);
        int age = validator.getValidAge(scanner);
        String address = validator.getValidAddress(scanner);
        String gmail = validator.getValidGmail(scanner);
        String telephone = validator.getValidTelephone(scanner);
        
        System.out.print("Enter Username: ");
        String accountUsername = scanner.nextLine();
        
        for (Bank acc : allAccounts) {
            if (acc.getAccountUsername().equals(accountUsername)) {
                System.out.println("Error: Username already exists! Please use a different username.");
                return null;
            }
        }
        
        int accPIN = validator.getValidPIN(scanner);
        
        Bank newAccount = new Bank(accountHolder, age, address, gmail, telephone, accountUsername, 0.0, accPIN, 0.0);
        
        System.out.println("\n Account created successfully!");
        System.out.println("Starting balance: 0.00");
        
        return newAccount;
    }
    
    private void loginAccount() {
        System.out.println("\n<===Login===>");
        
        int attempts = 0;
        int maxAttempts = 3;
        
        while (attempts < maxAttempts) {
            System.out.print("Enter Username: ");
            String loginAccountUsername = scanner.nextLine();
            
            System.out.print("Enter your 6-digit PIN: ");
            String loginPinInput = scanner.nextLine();
            
            if (loginPinInput.matches("\\d{6}")) {
                int loginPin = Integer.parseInt(loginPinInput);
                
                Bank foundAccount = null;
                for (Bank acc : allAccounts) {
                    if (acc.getAccountUsername().equals(loginAccountUsername) && acc.getPin() == loginPin) {
                        foundAccount = acc;
                        break;
                    }
                }
                
                if (foundAccount != null) {
                    System.out.println("\n Login successful!");
                    accountMenu(foundAccount);
                    return;
                } else {
                    attempts++;
                    System.out.println(" Invalid username or PIN. Attempts remaining: " + (maxAttempts - attempts));
                }
            } else {
                System.out.println(" Invalid PIN format! PIN must be exactly 6 digits.");
                attempts++;
            }
        }
        
        System.out.println("\nToo many failed attempts. Returning to main menu.");
    }
    
    private void accountMenu(Bank account) {
        boolean running = true;
        
        while (running) {
            System.out.println("\n--- Bank Menu ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Loan");
            System.out.println("5. View Loan Status");
            System.out.println("6. Transfer to Savings");
            System.out.println("7. Withdraw from Savings");
            System.out.println("8. View Savings Balance");
            System.out.println("9. Logout");
            System.out.print("Choose an option (1-9): ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    account.checkBalance();
                    break;
                    
                case 2:
                    System.out.print("Enter deposit amount: ");
                    double depositAmount = scanner.nextDouble();
                    scanner.nextLine();
                    account.deposit(depositAmount);
                    break;
                    
                case 3:
                    if (account.hasActiveLoan()) {
                        System.out.print("The system detected that you have loans would you like to pay it now? (yes/no): ");
                        String input = scanner.nextLine().toLowerCase();
                        if (input.equals("yes")) {
                            account.payloan();
                        }
                    }
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawAmount = scanner.nextDouble();
                    scanner.nextLine();


                    account.withdraw(withdrawAmount);
                    break;
                    
                case 4:
                    if (account.getBalance() <= 0) {
                        System.out.println("\n");
                        System.out.println("   LOAN REQUEST DENIED");
                        System.out.println("");
                        System.out.println("You need to have a balance in your");
                        System.out.println("account before applying for a loan.");
                        System.out.println("\n Please deposit money first!");
                        System.out.println("");
                        
                        System.out.print("\nWould you like to deposit now? (yes/no): ");
                        String depositChoice = scanner.nextLine().trim().toLowerCase();
                        
                        if (depositChoice.equals("yes") || depositChoice.equals("y")) {
                            System.out.print("Enter deposit amount: ");
                            double depositAmount2 = scanner.nextDouble();
                            scanner.nextLine();
                            account.deposit(depositAmount2);
                            
                            if (account.getBalance() > 0) {
                                System.out.println("\n You can now apply for a loan!");
                                System.out.print("Enter loan amount: ");
                                double loanAmount = scanner.nextDouble();
                                scanner.nextLine();
                                account.requestLoan(loanAmount);
                            }
                        }
                    } else {
                        System.out.print("Enter loan amount: ");
                        double loanAmount = scanner.nextDouble();
                        scanner.nextLine();
                        account.requestLoan(loanAmount);
                    }
                    break;
                    
                case 5:
                    account.viewLoanStatus();
                    break;
                    
                case 6:
                    System.out.print("Enter amount to transfer to savings: ");
                    double transferAmount = scanner.nextDouble();
                    scanner.nextLine();
                    account.transferToSavings(transferAmount);
                    break;
                    
                case 7:
                    System.out.print("Enter amount to withdraw from savings: ");
                    double withdrawSavingsAmount = scanner.nextDouble();
                    scanner.nextLine();
                    account.withdrawFromSavings(withdrawSavingsAmount);
                    break;
                    
                case 8:
                    account.viewSavingsBalance();
                    break;
                    
                case 9:
                    System.out.println("Logging out... Thank you for banking with us!");
                    running = false;
                    break;
                    
                default:
                    System.out.println("Invalid choice! Please select 1-9.");
            }
        }
    }
    
    // ============ ADMIN METHODS ============
    
    private void adminLogin() {
        System.out.println("\n<===Admin Login===>");
        
        int attempts = 0;
        int maxAttempts = 3;
        
        while (attempts < maxAttempts) {
            System.out.print("Enter admin username: ");
            String username = scanner.nextLine();
            
            System.out.print("Enter admin password: ");
            String password = scanner.nextLine();
            
            // Convert String inputs to char[] for comparison
            char[] usernameChars = username.toCharArray();
            char[] passwordChars = password.toCharArray();
            
            if (Arrays.equals(usernameChars, ADMIN_USERNAME) && Arrays.equals(passwordChars, ADMIN_PASSWORD)) {
                // Clear sensitive data from memory
                Arrays.fill(usernameChars, '\0');
                Arrays.fill(passwordChars, '\0');
                
                System.out.println("\nAdmin login successful!");
                adminMenu();
                return;
            } else {
                // Clear sensitive data from memory
                Arrays.fill(usernameChars, '\0');
                Arrays.fill(passwordChars, '\0');
                
                attempts++;
                System.out.println(" Invalid credentials. Attempts remaining: " + (maxAttempts - attempts));
            }
        }
        
        System.out.println("\nToo many failed attempts. Returning to main menu.");
    }
    
    private void adminMenu() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View All Registered Accounts");
            System.out.println("2. View Account Details");
            System.out.println("3. Delete Account");
            System.out.println("4. Total Number of Accounts");
            System.out.println("5. Logout");
            System.out.print("Choose an option (1-5): ");
            
            int choice;

if (scanner.hasNextInt()) {
    choice = scanner.nextInt();
    scanner.nextLine();
} else {
    System.out.println("Invalid input! Please enter numbers only (1-5).");
    scanner.nextLine(); // clear wrong input
    continue; // balik sa menu
}
            
            switch (choice) {
                case 1:
                    viewAllAccounts();
                    break;
                    
                case 2:
                    viewAccountDetails();
                    break;
                    
                case 3:
                    deleteAccount();
                    break;
                    
                case 4:
                    System.out.println("\nTotal Registered Accounts: " + allAccounts.size());
                    break;
                    
                case 5:
                    System.out.println("Logging out from admin panel...");
                    running = false;
                    break;
                    
                default:
                    System.out.println("Invalid choice! Please select 1-5.");
            }
        }
    }
    
    private void viewAllAccounts() {
        System.out.println("\nALL REGISTERED ACCOUNTS");
        
        if (allAccounts.isEmpty()) {
            System.out.println("No accounts registered yet.");
            return;
        }
        
        System.out.println("Total Accounts: " + allAccounts.size());
        System.out.println("---------------------------------------------");
        
        for (int i = 0; i < allAccounts.size(); i++) {
            Bank account = allAccounts.get(i);
            System.out.println("\nAccount ID: " + (i + 1));
            System.out.println("Account Holder: " + account.getAccountHolder());
            System.out.println("Age: " + account.getAge());
            System.out.println("Address: " + account.getAddress());
            System.out.println("Gmail: " + account.getGmail());
            System.out.println("Telephone: " + account.getTelephone());
            System.out.println("Username: " + account.getAccountUsername());
            System.out.printf("Balance: %.2f%n", account.getBalance());
            System.out.printf("Savings: %.2f%n", account.getSavingsBalance());
            System.out.printf("Total: %.2f%n", (account.getBalance() + account.getSavingsBalance()));
            System.out.println("Has Active Loan: " + (account.hasActiveLoan() ? "Yes" : "No"));
            System.out.println("---------------------------------------------");
        }
    }
    
    private void viewAccountDetails() {
        System.out.println("\n<===View Account Details===>");
        
        if (allAccounts.isEmpty()) {
            System.out.println("No accounts registered yet!");
            return;
        }
        
        System.out.println("\n--- Quick Account List ---");
        for (int i = 0; i < allAccounts.size(); i++) {
            Bank account = allAccounts.get(i);
            System.out.println("Account ID: " + (i + 1) + " | Username: " + account.getAccountUsername() + 
                             " | Fullname: " + account.getAccountHolder());
        }
        
        System.out.print("\nEnter Account ID to view full details (1-" + allAccounts.size() + "): ");
        
        if (!scanner.hasNextInt()) {
            System.out.println("Error: Please enter a valid number!");
            scanner.nextLine();
            return;
        }
        
        int accountId = scanner.nextInt();
        scanner.nextLine();
        
        if (accountId < 1 || accountId > allAccounts.size()) {
            System.out.println("Error: Invalid Account ID! Please enter a number between 1 and " + allAccounts.size());
            return;
        }
        
        Bank foundAccount = allAccounts.get(accountId - 1);
        
        System.out.println("\n");
        System.out.println("         \tFULL ACCOUNT DETAILS  \t                      ");
        System.out.println("");
        System.out.println();
        System.out.println(" IDENTIFICATION INFO ");
        System.out.println(" Account ID         : " + accountId);
        System.out.println(" Username           : " + foundAccount.getAccountUsername());
        System.out.println(" Account Holder     : " + foundAccount.getAccountHolder());
        System.out.println(" PIN (Secured)      : ******");
        System.out.println("");
        System.out.println();
        System.out.println(" \tPERSONAL INFO \t");
        System.out.println(" Age                : " + foundAccount.getAge() + " years old");
        System.out.println(" Address            : " + foundAccount.getAddress());
        System.out.println(" Gmail              : " + foundAccount.getGmail());
        System.out.println(" Telephone          : " + foundAccount.getTelephone());
        System.out.println("");
        System.out.println();
        System.out.println(" \tFINANCIAL INFO\t ");
        System.out.printf(" Checking Balance   : %.2f%n", foundAccount.getBalance());
        System.out.printf(" Savings Balance    : %.2f%n", foundAccount.getSavingsBalance());
        System.out.printf(" TOTAL BALANCE      : %.2f%n", (foundAccount.getBalance() + foundAccount.getSavingsBalance()));
        System.out.println("");
        System.out.println();
        System.out.println("\t LOAN INFORMATION\t");
        System.out.println(" Has Active Loan    : " + (foundAccount.hasActiveLoan() ? "YES" : "NO"));
        if (foundAccount.hasActiveLoan()) {
            System.out.printf(" Outstanding Loan   : %.2f%n", foundAccount.getLoanAmount());
            System.out.printf(" Interest Rate      : %.0f%%%n", foundAccount.getLoanInterest() * 100);
        } else {
            System.out.println(" Outstanding Loan   : 0.00");
            System.out.println(" Status             : No active loans");
        }
        System.out.println("");
        System.out.println();
        System.out.println("\t ACCOUNT SUMMARY \t");
        System.out.printf(" Net Worth          : %.2f%n", 
            (foundAccount.getBalance() + foundAccount.getSavingsBalance() - 
            (foundAccount.hasActiveLoan() ? foundAccount.getLoanAmount() : 0)));
        System.out.println(" Account Status     : ACTIVE");
        System.out.println("\n");
        System.out.println();
        
        System.out.println("What would you like to do?");
        System.out.println("1. Delete this account");
        System.out.println("2. Return to admin menu");
        System.out.print("Choose an option (1-2): ");
        
        int option = scanner.nextInt();
        scanner.nextLine();
        
        if (option == 1) {
            System.out.print("\n Are you sure you want to delete this account? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("yes") || confirmation.equals("y")) {
                String deletedUsername = foundAccount.getAccountUsername();
                String deletedHolder = foundAccount.getAccountHolder();
                
                allAccounts.remove(accountId - 1);
                
                System.out.println("\n Account deleted successfully!");
                System.out.println("Deleted Account - ID: " + accountId + " | Username: " + deletedUsername + 
                                 " | Fullname: " + deletedHolder);
                System.out.println("Remaining accounts: " + allAccounts.size());
            } else {
                System.out.println("\n Account deletion cancelled.");
            }
        } else {
            System.out.println("\nReturning to admin menu...");
        }
    }
    
    private void deleteAccount() {
        System.out.println("\n<===Delete Account===>");
        
        if (allAccounts.isEmpty()) {
            System.out.println("No accounts to delete!");
            return;
        }
        
        System.out.println("\n--- Current Accounts ---");
        for (int i = 0; i < allAccounts.size(); i++) {
            Bank account = allAccounts.get(i);
            System.out.println("Account ID: " + (i + 1) + " | Username: " + account.getAccountUsername() + 
                             " | Fullname: " + account.getAccountHolder());
        }
        
        System.out.print("\nEnter Account ID to delete (1-" + allAccounts.size() + "): ");
        
        if (!scanner.hasNextInt()) {
            System.out.println("Error: Please enter a valid number!");
            scanner.nextLine();
            return;
        }
        
        int accountId = scanner.nextInt();
        scanner.nextLine();
        
        if (accountId < 1 || accountId > allAccounts.size()) {
            System.out.println("Error: Invalid Account ID! Please enter a number between 1 and " + allAccounts.size());
            return;
        }
        
        Bank accountToDelete = allAccounts.get(accountId - 1);
        
        System.out.println("\n--- Account Details ---");
        System.out.println("Account ID: " + accountId);
        System.out.println("Username: " + accountToDelete.getAccountUsername());
        System.out.println("Account Holder: " + accountToDelete.getAccountHolder());
        System.out.printf("Total Balance: %.2f%n", (accountToDelete.getBalance() + accountToDelete.getSavingsBalance()));
        
        System.out.print("\nAre you sure you want to delete this account? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("yes") || confirmation.equals("y")) {
            String deletedUsername = accountToDelete.getAccountUsername();
            String deletedHolder = accountToDelete.getAccountHolder();
            
            allAccounts.remove(accountId - 1);
            
            System.out.println("\n Account deleted successfully!");
            System.out.println("Deleted Account - ID: " + accountId + " | Username: " + deletedUsername + 
                             " | Holder: " + deletedHolder);
            System.out.println("Remaining accounts: " + allAccounts.size());
        } else {
            System.out.println("\n Account deletion cancelled.");
        }
    }
}