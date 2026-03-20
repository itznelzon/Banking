import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class BankSwingUI extends JFrame {
    private static final String SCREEN_WELCOME = "welcome";
    private static final String SCREEN_REGISTER = "register";
    private static final String SCREEN_LOGIN = "login";
    private static final String SCREEN_ADMIN_LOGIN = "adminLogin";
    private static final String SCREEN_USER_DASHBOARD = "userDashboard";
    private static final String SCREEN_ADMIN_DASHBOARD = "adminDashboard";

    private static final Color PRIMARY = new Color(12, 94, 114);
    private static final Color PRIMARY_DARK = new Color(8, 63, 77);
    private static final Color ACCENT = new Color(242, 169, 59);
    private static final Color SOFT_BG = new Color(243, 248, 250);
    private static final Color CARD_BG = new Color(255, 255, 255, 235);
    private static final Color TEXT_DARK = new Color(32, 40, 45);

    private final BankSystem bankSystem;
    private final InputValidator validator;
    private final DecimalFormat moneyFormat;

    private final CardLayout cardLayout;
    private final JPanel cards;

    private Bank currentAccount;
    private static final String ACTIVE_DB = DatabaseConnection.getDatabaseName();
    private static final String ADMIN_LOCALHOST_URL =
        "http://localhost/phpmyadmin/index.php?route=/database/structure&db=" + ACTIVE_DB;
    private static final String ACCOUNTS_TABLE_URL =
        "http://localhost/phpmyadmin/index.php?route=/table/structure&db=" + ACTIVE_DB + "&table=accounts";

    private JTextField regNameField;
    private JTextField regAgeField;
    private JTextField regAddressField;
    private JTextField regGmailField;
    private JTextField regTelephoneField;
    private JTextField regUsernameField;
    private JPasswordField regPinField;

    private JTextField loginUsernameField;
    private JPasswordField loginPinField;

    private JTextField adminUsernameField;
    private JPasswordField adminPasswordField;

    private JLabel dashboardHeaderLabel;
    private JLabel checkingValueLabel;
    private JLabel savingsValueLabel;
    private JLabel loanValueLabel;
    private JLabel totalValueLabel;
    private JLabel netWorthValueLabel;

    private DefaultTableModel adminTableModel;
    private JTable adminTable;

    public BankSwingUI(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        this.validator = new InputValidator();
        this.moneyFormat = new DecimalFormat("#,##0.00");

        setTitle("CAGUIOA BANK - Smart Banking Desk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 680));
        setSize(1100, 740);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setOpaque(false);

        cards.add(buildWelcomeScreen(), SCREEN_WELCOME);
        cards.add(buildRegisterScreen(), SCREEN_REGISTER);
        cards.add(buildLoginScreen(), SCREEN_LOGIN);
        cards.add(buildAdminLoginScreen(), SCREEN_ADMIN_LOGIN);
        cards.add(buildUserDashboardScreen(), SCREEN_USER_DASHBOARD);
        cards.add(buildAdminDashboardScreen(), SCREEN_ADMIN_DASHBOARD);

        GradientPanel root = new GradientPanel();
        root.setLayout(new BorderLayout());
        root.add(cards, BorderLayout.CENTER);
        setContentPane(root);

        showScreen(SCREEN_WELCOME);
    }

    private JPanel buildWelcomeScreen() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        JPanel card = makeCardPanel(new GridBagLayout(), 620, 420);
        GridBagConstraints gbc = baseGbc();

        JLabel title = new JLabel("CAGUIOA BANK", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 46));
        title.setForeground(TEXT_DARK);

        JLabel subtitle = new JLabel("The Choice of the People", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        subtitle.setForeground(PRIMARY_DARK);

        JLabel highlight = new JLabel("Only 5% Loan Interest", SwingConstants.CENTER);
        highlight.setFont(new Font("Segoe UI", Font.BOLD, 18));
        highlight.setForeground(ACCENT.darker());

        JButton registerBtn = primaryButton("Create Client Account");
        registerBtn.addActionListener(e -> showScreen(SCREEN_REGISTER));

        JButton loginBtn = primaryButton("Client Login Page");
        loginBtn.addActionListener(e -> showScreen(SCREEN_LOGIN));

        JButton adminBtn = secondaryButton("Admin Localhost");
        adminBtn.addActionListener(e -> openAdminLocalhost());

        gbc.insets = new Insets(12, 20, 4, 20);
        gbc.gridy = 0;
        card.add(title, gbc);

        gbc.insets = new Insets(4, 20, 6, 20);
        gbc.gridy = 1;
        card.add(subtitle, gbc);

        gbc.gridy = 2;
        card.add(highlight, gbc);

        gbc.insets = new Insets(28, 40, 10, 40);
        gbc.gridy = 3;
        card.add(registerBtn, gbc);

        gbc.insets = new Insets(10, 40, 10, 40);
        gbc.gridy = 4;
        card.add(loginBtn, gbc);

        gbc.insets = new Insets(10, 40, 16, 40);
        gbc.gridy = 5;
        card.add(adminBtn, gbc);

        content.add(card);
        return content;
    }

    private JPanel buildRegisterScreen() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        JPanel card = makeCardPanel(new GridBagLayout(), 700, 530);
        GridBagConstraints gbc = baseGbc();

        JLabel title = sectionTitle("Register Client Account");
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 10, 18, 10);
        card.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);

        regNameField = new JTextField();
        regAgeField = new JTextField();
        regAddressField = new JTextField();
        regGmailField = new JTextField();
        regTelephoneField = new JTextField();
        regUsernameField = new JTextField();
        regPinField = new JPasswordField();

        addFormRow(card, gbc, 1, "Full Name", regNameField);
        addFormRow(card, gbc, 2, "Age", regAgeField);
        addFormRow(card, gbc, 3, "Address", regAddressField);
        addFormRow(card, gbc, 4, "Gmail", regGmailField);
        addFormRow(card, gbc, 5, "Telephone", regTelephoneField);
        addFormRow(card, gbc, 6, "Username", regUsernameField);
        addFormRow(card, gbc, 7, "6-digit PIN", regPinField);

        char registerPinMask = regPinField.getEchoChar();
        JCheckBox showRegisterPin = new JCheckBox("Show PIN");
        showRegisterPin.setOpaque(false);
        showRegisterPin.setForeground(PRIMARY_DARK);
        showRegisterPin.addActionListener(e -> regPinField.setEchoChar(showRegisterPin.isSelected() ? (char) 0 : registerPinMask));

        gbc.gridy = 8;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(2, 10, 8, 10);
        card.add(showRegisterPin, gbc);

        JButton registerBtn = primaryButton("Register");
        registerBtn.addActionListener(e -> handleRegister());

        JButton backBtn = textButton("Back");
        backBtn.addActionListener(e -> showScreen(SCREEN_WELCOME));

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.add(registerBtn);
        actions.add(backBtn);

        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 8, 10);
        card.add(actions, gbc);

        form.add(card);
        return form;
    }

    private JPanel buildLoginScreen() {
        JPanel holder = new JPanel(new GridBagLayout());
        holder.setOpaque(false);

        JPanel card = makeCardPanel(new GridBagLayout(), 620, 390);
        GridBagConstraints gbc = baseGbc();

        JLabel title = sectionTitle("User Login");
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 10, 20, 10);
        card.add(title, gbc);

        loginUsernameField = new JTextField();
        loginPinField = new JPasswordField();

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
        addFormRow(card, gbc, 1, "Username", loginUsernameField);
        addFormRow(card, gbc, 2, "PIN", loginPinField);

        char loginPinMask = loginPinField.getEchoChar();
        JCheckBox showLoginPin = new JCheckBox("Show PIN");
        showLoginPin.setOpaque(false);
        showLoginPin.setForeground(PRIMARY_DARK);
        showLoginPin.addActionListener(e -> loginPinField.setEchoChar(showLoginPin.isSelected() ? (char) 0 : loginPinMask));

        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(2, 10, 8, 10);
        card.add(showLoginPin, gbc);

        JButton loginBtn = primaryButton("Login");
        loginBtn.addActionListener(e -> handleUserLogin());

        JButton backBtn = textButton("Back");
        backBtn.addActionListener(e -> showScreen(SCREEN_WELCOME));

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.add(loginBtn);
        actions.add(backBtn);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(22, 10, 10, 10);
        card.add(actions, gbc);

        holder.add(card);
        return holder;
    }

    private JPanel buildAdminLoginScreen() {
        JPanel holder = new JPanel(new GridBagLayout());
        holder.setOpaque(false);

        JPanel card = makeCardPanel(new GridBagLayout(), 620, 390);
        GridBagConstraints gbc = baseGbc();

        JLabel title = sectionTitle("Admin Login");
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 10, 20, 10);
        card.add(title, gbc);

        adminUsernameField = new JTextField();
        adminPasswordField = new JPasswordField();

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
        addFormRow(card, gbc, 1, "Admin Username", adminUsernameField);
        addFormRow(card, gbc, 2, "Admin Password", adminPasswordField);

        JButton loginBtn = primaryButton("Open Admin Panel");
        loginBtn.addActionListener(e -> handleAdminLogin());

        JButton backBtn = textButton("Back");
        backBtn.addActionListener(e -> showScreen(SCREEN_WELCOME));

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.add(loginBtn);
        actions.add(backBtn);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(22, 10, 10, 10);
        card.add(actions, gbc);

        holder.add(card);
        return holder;
    }

    private JPanel buildUserDashboardScreen() {
        JPanel wrapper = new JPanel(new BorderLayout(14, 14));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel header = makeCardPanel(new BorderLayout(), 0, 88);
        dashboardHeaderLabel = new JLabel("Welcome", SwingConstants.LEFT);
        dashboardHeaderLabel.setForeground(TEXT_DARK);
        dashboardHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JButton logoutBtn = textButton("Logout");
        logoutBtn.addActionListener(e -> {
            currentAccount = null;
            showScreen(SCREEN_WELCOME);
        });

        header.add(dashboardHeaderLabel, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);

        JPanel center = new JPanel(new GridLayout(1, 2, 14, 14));
        center.setOpaque(false);

        JPanel summaryCard = makeCardPanel(new GridBagLayout(), 0, 0);
        summaryCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        GridBagConstraints gbc = baseGbc();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        checkingValueLabel = summaryLine(summaryCard, gbc, 0, "Checking Balance");
        savingsValueLabel = summaryLine(summaryCard, gbc, 1, "Savings Balance");
        loanValueLabel = summaryLine(summaryCard, gbc, 2, "Outstanding Loan");
        totalValueLabel = summaryLine(summaryCard, gbc, 3, "Total Balance");
        netWorthValueLabel = summaryLine(summaryCard, gbc, 4, "Net Worth");

        JPanel actionCard = makeCardPanel(new GridLayout(4, 2, 10, 10), 0, 0);
        actionCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JButton depositBtn = secondaryButton("Deposit");
        depositBtn.addActionListener(e -> handleAmountAction("Deposit", amount -> {
            bankSystem.deposit(currentAccount, amount);
            return "Deposit successful.";
        }));

        JButton withdrawBtn = secondaryButton("Withdraw");
        withdrawBtn.addActionListener(e -> handleAmountAction("Withdraw", amount -> {
            bankSystem.withdraw(currentAccount, amount);
            return "Withdrawal successful.";
        }));

        JButton toSavingsBtn = secondaryButton("Transfer to Savings");
        toSavingsBtn.addActionListener(e -> handleAmountAction("Transfer to Savings", amount -> {
            bankSystem.transferToSavings(currentAccount, amount);
            return "Funds transferred to savings.";
        }));

        JButton fromSavingsBtn = secondaryButton("Withdraw from Savings");
        fromSavingsBtn.addActionListener(e -> handleAmountAction("Withdraw from Savings", amount -> {
            bankSystem.withdrawFromSavings(currentAccount, amount);
            return "Funds moved from savings to checking.";
        }));

        JButton requestLoanBtn = secondaryButton("Request Loan");
        requestLoanBtn.addActionListener(e -> handleAmountAction("Request Loan", amount -> {
            double totalToRepay = bankSystem.requestLoan(currentAccount, amount);
            return "Loan approved. Total repayment: " + formatMoney(totalToRepay);
        }));

        JButton payLoanBtn = secondaryButton("Pay Loan In Full");
        payLoanBtn.addActionListener(e -> {
            try {
                bankSystem.payLoanInFull(currentAccount);
                refreshUserDashboard();
                showInfo("Loan paid successfully.");
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });

        JButton refreshBtn = primaryButton("Refresh Balances");
        refreshBtn.addActionListener(e -> refreshUserDashboard());

        JButton showLoanLimitBtn = textButton("Show Max Loan Offer");
        showLoanLimitBtn.addActionListener(e -> showInfo("Max loan you can request now: " + formatMoney(currentAccount.getMaxLoanAmount())));

        actionCard.add(depositBtn);
        actionCard.add(withdrawBtn);
        actionCard.add(toSavingsBtn);
        actionCard.add(fromSavingsBtn);
        actionCard.add(requestLoanBtn);
        actionCard.add(payLoanBtn);
        actionCard.add(refreshBtn);
        actionCard.add(showLoanLimitBtn);

        center.add(summaryCard);
        center.add(actionCard);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(center, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildAdminDashboardScreen() {
        JPanel wrapper = new JPanel(new BorderLayout(14, 14));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel header = makeCardPanel(new BorderLayout(), 0, 88);
        JLabel title = new JLabel("Admin Dashboard", SwingConstants.LEFT);
        title.setForeground(TEXT_DARK);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JButton logoutBtn = textButton("Logout");
        logoutBtn.addActionListener(e -> showScreen(SCREEN_WELCOME));

        header.add(title, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);

        JPanel tableCard = makeCardPanel(new BorderLayout(), 0, 0);
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        adminTableModel = new DefaultTableModel(
            new Object[] {"ID", "Username", "Holder", "Checking", "Savings", "Loan"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        adminTable = new JTable(adminTableModel);
        adminTable.setRowHeight(26);
        adminTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adminTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(adminTable);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 6, 10, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton refreshBtn = primaryButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAdminTable());

        JButton detailsBtn = secondaryButton("View Details");
        detailsBtn.addActionListener(e -> showSelectedAccountDetails());

        JButton deleteBtn = secondaryButton("Delete Account");
        deleteBtn.addActionListener(e -> deleteSelectedAccount());

        JButton totalBtn = textButton("Total Accounts");
        totalBtn.addActionListener(e -> showInfo("Total registered accounts: " + bankSystem.getTotalAccounts()));

        JButton phpMyAdminBtn = textButton("Open phpMyAdmin");
        phpMyAdminBtn.addActionListener(e -> openPhpMyAdmin());

        JButton logsBtn = textButton("View Login Logs");
        logsBtn.addActionListener(e -> showLoginLogs());

        actions.add(refreshBtn);
        actions.add(detailsBtn);
        actions.add(deleteBtn);
        actions.add(totalBtn);
        actions.add(phpMyAdminBtn);
        actions.add(logsBtn);

        tableCard.add(actions, BorderLayout.SOUTH);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(tableCard, BorderLayout.CENTER);
        return wrapper;
    }

    private void handleUserLogin() {
        try {
            String username = loginUsernameField.getText();
            String pin = new String(loginPinField.getPassword());
            currentAccount = bankSystem.loginAccount(username, pin);

            loginPinField.setText("");
            dashboardHeaderLabel.setText("Welcome, " + currentAccount.getAccountHolder());
            refreshUserDashboard();
            showScreen(SCREEN_USER_DASHBOARD);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleAdminLogin() {
        String username = adminUsernameField.getText();
        String password = new String(adminPasswordField.getPassword());

        if (bankSystem.loginAdmin(username, password)) {
            adminPasswordField.setText("");
            refreshAdminTable();
            showScreen(SCREEN_ADMIN_DASHBOARD);
            return;
        }

        showError("Invalid admin credentials.");
    }

    private void handleRegister() {
        try {
            String name = regNameField.getText();
            int age = Integer.parseInt(regAgeField.getText().trim());
            String address = regAddressField.getText();
            String gmail = regGmailField.getText();
            String telephone = regTelephoneField.getText().trim();
            String username = regUsernameField.getText();
            String pin = new String(regPinField.getPassword());

            bankSystem.registerAccount(name, age, address, gmail, telephone, username, pin);
            clearRegistrationForm();
            showInfo("Client account registered successfully.");
            showScreen(SCREEN_LOGIN);
        } catch (NumberFormatException ex) {
            showError("Age must be a number.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (IllegalStateException ex) {
            showError("Database registration failed: " + ex.getMessage());
        }
    }

    private void handleAmountAction(String actionName, AmountAction action) {
        String rawAmount = JOptionPane.showInputDialog(this, "Enter amount for " + actionName + ":", actionName, JOptionPane.QUESTION_MESSAGE);
        if (rawAmount == null) {
            return;
        }

        try {
            double amount = Double.parseDouble(rawAmount.trim());
            String amountError = validator.validateMoneyAmount(amount);
            if (amountError != null) {
                showError(amountError);
                return;
            }

            String message = action.perform(amount);
            refreshUserDashboard();
            showInfo(message);
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void refreshUserDashboard() {
        if (currentAccount == null) {
            return;
        }

        checkingValueLabel.setText(formatMoney(currentAccount.getBalance()));
        savingsValueLabel.setText(formatMoney(currentAccount.getSavingsBalance()));
        loanValueLabel.setText(formatMoney(currentAccount.getLoanAmount()));
        totalValueLabel.setText(formatMoney(currentAccount.getTotalBalance()));
        netWorthValueLabel.setText(formatMoney(currentAccount.getNetWorth()));
    }

    private void refreshAdminTable() {
        adminTableModel.setRowCount(0);
        List<Bank> accounts = bankSystem.getAllAccounts();

        for (int i = 0; i < accounts.size(); i++) {
            Bank account = accounts.get(i);
            adminTableModel.addRow(new Object[] {
                account.getAccountId(),
                account.getAccountUsername(),
                account.getAccountHolder(),
                formatMoney(account.getBalance()),
                formatMoney(account.getSavingsBalance()),
                formatMoney(account.getLoanAmount())
            });
        }
    }

    private void showSelectedAccountDetails() {
        int selected = adminTable.getSelectedRow();
        if (selected < 0) {
            showError("Please select an account from the table.");
            return;
        }

        try {
            Bank account = bankSystem.getAccountByIndex(selected);
            String details =
                "Account ID: " + account.getAccountId() + "\n" +
                "Username: " + account.getAccountUsername() + "\n" +
                "Account Holder: " + account.getAccountHolder() + "\n" +
                "Age: " + account.getAge() + "\n" +
                "Address: " + account.getAddress() + "\n" +
                "Gmail: " + account.getGmail() + "\n" +
                "Telephone: " + account.getTelephone() + "\n\n" +
                "Checking Balance: " + formatMoney(account.getBalance()) + "\n" +
                "Savings Balance: " + formatMoney(account.getSavingsBalance()) + "\n" +
                "Loan Balance: " + formatMoney(account.getLoanAmount()) + "\n" +
                "Net Worth: " + formatMoney(account.getNetWorth());

            JOptionPane.showMessageDialog(this, details, "Account Details", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void showLoginLogs() {
        try {
            List<String[]> logs = bankSystem.getRecentLoginLogs(200);

            DefaultTableModel logModel = new DefaultTableModel(
                new Object[] {"ID", "Role", "Username", "Success", "Notes", "Logged At"},
                0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (String[] logRow : logs) {
                logModel.addRow(logRow);
            }

            JTable logTable = new JTable(logModel);
            logTable.setRowHeight(24);
            logTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            logTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

            JScrollPane scrollPane = new JScrollPane(logTable);
            scrollPane.setPreferredSize(new Dimension(860, 360));

            JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Recent Login Logs",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IllegalStateException ex) {
            showError("Cannot load login logs: " + ex.getMessage());
        }
    }

    private void deleteSelectedAccount() {
        int selected = adminTable.getSelectedRow();
        if (selected < 0) {
            showError("Please select an account to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete selected account permanently?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            bankSystem.deleteAccountByIndex(selected);
            refreshAdminTable();
            showInfo("Account deleted successfully.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private JLabel summaryLine(JPanel panel, GridBagConstraints gbc, int row, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(PRIMARY_DARK);

        JLabel value = new JLabel("0.00");
        value.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        value.setForeground(TEXT_DARK);

        gbc.gridy = row * 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 6, 2, 6);
        panel.add(label, gbc);

        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 6, 10, 6);
        panel.add(value, gbc);

        return value;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent input) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(PRIMARY_DARK);

        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        input.setPreferredSize(new Dimension(320, 34));

        if (input instanceof JTextField textField) {
            textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(183, 196, 204)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
        }

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(input, gbc);
    }

    private JPanel makeCardPanel(java.awt.LayoutManager layout, int prefWidth, int prefHeight) {
        JPanel card = new JPanel(layout);
        card.setOpaque(true);
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 222, 228), 1),
            new EmptyBorder(14, 14, 14, 14)
        ));

        if (prefWidth > 0 && prefHeight > 0) {
            card.setPreferredSize(new Dimension(prefWidth, prefHeight));
        }

        return card;
    }

    private JLabel sectionTitle(String title) {
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 30));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(ACCENT);
        button.setForeground(new Color(48, 36, 8));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        return button;
    }

    private JButton textButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(SOFT_BG);
        button.setForeground(PRIMARY_DARK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        return button;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void showScreen(String screenName) {
        cardLayout.show(cards, screenName);
    }

    private void clearRegistrationForm() {
        regNameField.setText("");
        regAgeField.setText("");
        regAddressField.setText("");
        regGmailField.setText("");
        regTelephoneField.setText("");
        regUsernameField.setText("");
        regPinField.setText("");
    }

    private String formatMoney(double value) {
        return moneyFormat.format(value);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openPhpMyAdmin() {
        try {
            if (!Desktop.isDesktopSupported()) {
                showError("Desktop browse is not supported on this machine.");
                return;
            }
            Desktop.getDesktop().browse(URI.create(ACCOUNTS_TABLE_URL));
        } catch (IOException | SecurityException ex) {
            showError("Could not open phpMyAdmin. Make sure XAMPP Apache is running.\n" + ex.getMessage());
        }
    }

    private void openAdminLocalhost() {
        try {
            if (!Desktop.isDesktopSupported()) {
                showError("Desktop browse is not supported on this machine.");
                return;
            }
            Desktop.getDesktop().browse(URI.create(ADMIN_LOCALHOST_URL));
        } catch (IOException | SecurityException ex) {
            showError("Could not open admin localhost.\n" + ex.getMessage());
        }
    }

    @FunctionalInterface
    private interface AmountAction {
        String perform(double amount);
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(
                0,
                0,
                new Color(224, 241, 247),
                w,
                h,
                new Color(252, 247, 232)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(30, 108, 129, 22));
            g2.fillOval((int) (w * 0.68), -110, 340, 340);
            g2.fillOval(-160, (int) (h * 0.58), 400, 320);
            g2.dispose();
        }
    }
}
