import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
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
    private static final String SCREEN_UNIFIED_AUTH = "unifiedAuth";
    private static final String SCREEN_REGISTER = "register";
    private static final String SCREEN_LOGIN = "login";
    private static final String SCREEN_ADMIN_LOGIN = "adminLogin";
    private static final String SCREEN_ADMIN_REGISTER = "adminRegister";
    private static final String SCREEN_USER_DASHBOARD = "userDashboard";
    private static final String SCREEN_ADMIN_DASHBOARD = "adminDashboard";

    // Land Bank Professional Color Palette - Dark Green & Gold
    private static final Color PRIMARY = new Color(0, 102, 64);          // Land Bank Dark Green
    private static final Color PRIMARY_DARK = new Color(0, 77, 48);      // Darker Green
    private static final Color ACCENT = new Color(212, 175, 55);         // Elegant Gold
    private static final Color SOFT_BG = new Color(244, 249, 246);       // Light Greenish-White
    private static final Color TEXT_DARK = new Color(20, 35, 28);        // Dark Forest Green
    private static final Color TEXT_LIGHT = new Color(90, 105, 95);      // Muted Green-Gray
    private static final Color BORDER_COLOR = new Color(200, 220, 210);  // Light Green Border

    private final BankSystem bankSystem;
    private final InputValidator validator;
    private final DecimalFormat moneyFormat;

    private final CardLayout cardLayout;
    private final JPanel cards;

    private Bank currentAccount;
    private final boolean isLocalhost;

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
        this.isLocalhost = detectLocalhost();

        setTitle("CAGUIOA BANK - Smart Banking Desk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 680));
        setSize(1100, 740);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setOpaque(false);

        cards.add(buildWelcomeScreen(), SCREEN_WELCOME);
        cards.add(buildUnifiedAuthScreen(), SCREEN_UNIFIED_AUTH);
        cards.add(buildRegisterScreen(), SCREEN_REGISTER);
        cards.add(buildLoginScreen(), SCREEN_LOGIN);
        cards.add(buildAdminLoginScreen(), SCREEN_ADMIN_LOGIN);
        cards.add(buildAdminRegisterScreen(), SCREEN_ADMIN_REGISTER);
        cards.add(buildUserDashboardScreen(), SCREEN_USER_DASHBOARD);
        cards.add(buildAdminDashboardScreen(), SCREEN_ADMIN_DASHBOARD);

        GradientPanel root = new GradientPanel();
        root.setLayout(new BorderLayout());
        root.add(cards, BorderLayout.CENTER);
        setContentPane(root);

        if (isLocalhost) {
            showScreen(SCREEN_ADMIN_LOGIN);
        } else {
            showScreen(SCREEN_WELCOME);
        }
    }

    private boolean detectLocalhost() {
        try {
            String hostname = java.net.InetAddress.getLocalHost().getHostName();
            String hostaddr = java.net.InetAddress.getLocalHost().getHostAddress();
            boolean isLocal = hostname.equalsIgnoreCase("localhost") || 
                   hostaddr.equals("127.0.0.1") || 
                   hostname.contains("local");
            return isLocal;
        } catch (java.net.UnknownHostException | SecurityException ex) {
            return false;
        }
    }

    private JPanel buildWelcomeScreen() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        JPanel card = makeCardPanel(new GridBagLayout(), 620, 420);
        GridBagConstraints gbc = baseGbc();

        JLabel title = new JLabel("CAGUIOA BANK", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 44));
        title.setForeground(PRIMARY);

        JLabel subtitle = new JLabel("The Choice of the People", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitle.setForeground(TEXT_LIGHT);

        JLabel highlight = new JLabel("💰 Only 5% Loan Interest - 24/7 Banking", SwingConstants.CENTER);
        highlight.setFont(new Font("Segoe UI", Font.BOLD, 16));
        highlight.setForeground(ACCENT);

        JButton registerBtn = primaryButton("Create Account");
        registerBtn.addActionListener(e -> showScreen(SCREEN_REGISTER));

        JButton loginBtn = primaryButton("Log In Account");
        loginBtn.addActionListener(e -> showScreen(SCREEN_LOGIN));

        JButton adminBtn = secondaryButton("Admin Access");
        adminBtn.addActionListener(e -> showScreen(SCREEN_ADMIN_LOGIN));

        gbc.insets = new Insets(20, 20, 8, 20);
        gbc.gridy = 0;
        card.add(title, gbc);

        gbc.insets = new Insets(8, 20, 4, 20);
        gbc.gridy = 1;
        card.add(subtitle, gbc);

        gbc.insets = new Insets(4, 20, 24, 20);
        gbc.gridy = 2;
        card.add(highlight, gbc);

        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        card.add(registerBtn, gbc);

        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        card.add(loginBtn, gbc);

        // Admin button - compressed spacing
        gbc.insets = new Insets(8, 20, 12, 20);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        card.add(adminBtn, gbc);

        content.add(card);
        return content;
    }

    private JPanel buildUnifiedAuthScreen() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        // Scrollable container for all three auth sections
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new javax.swing.BoxLayout(scrollContent, javax.swing.BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);

        int sectionWidth = 650;

        // ============ REGISTER SECTION ============
        JPanel registerSection = makeCardPanel(new GridBagLayout(), sectionWidth, 400);
        GridBagConstraints gbc = baseGbc();

        JLabel regTitle = sectionTitle("📝 Create New Account");
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        registerSection.add(regTitle, gbc);

        gbc.gridwidth = 1;
        regNameField = new JTextField();
        regAgeField = new JTextField();
        regAddressField = new JTextField();
        regGmailField = new JTextField();
        regTelephoneField = new JTextField();
        regUsernameField = new JTextField();
        regPinField = new JPasswordField();

        addFormRow(registerSection, gbc, 1, "Full Name", regNameField);
        addFormRow(registerSection, gbc, 2, "Age", regAgeField);
        addFormRow(registerSection, gbc, 3, "Address", regAddressField);
        addFormRow(registerSection, gbc, 4, "Gmail", regGmailField);
        addFormRow(registerSection, gbc, 5, "Telephone", regTelephoneField);
        addFormRow(registerSection, gbc, 6, "Username", regUsernameField);
        addFormRow(registerSection, gbc, 7, "6-digit PIN", regPinField);

        char regPinMask = regPinField.getEchoChar();
        JCheckBox showRegPin = new JCheckBox("Show PIN");
        showRegPin.setOpaque(false);
        showRegPin.setForeground(PRIMARY_DARK);
        showRegPin.addActionListener(e -> regPinField.setEchoChar(showRegPin.isSelected() ? (char) 0 : regPinMask));

        gbc.gridy = 8;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(2, 0, 8, 0);
        registerSection.add(showRegPin, gbc);

        JButton regBtn = primaryButton("Register");
        regBtn.addActionListener(e -> handleRegister());

        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerSection.add(regBtn, gbc);

        // ============ LOGIN SECTION ============
        JPanel loginSection = makeCardPanel(new GridBagLayout(), sectionWidth, 280);
        gbc = baseGbc();

        JLabel loginTitle = sectionTitle("🔐 User Login");
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        loginSection.add(loginTitle, gbc);

        gbc.gridwidth = 1;
        loginUsernameField = new JTextField();
        loginPinField = new JPasswordField();

        addFormRow(loginSection, gbc, 1, "Username", loginUsernameField);
        addFormRow(loginSection, gbc, 2, "PIN", loginPinField);

        char loginPinMask = loginPinField.getEchoChar();
        JCheckBox showLoginPin = new JCheckBox("Show PIN");
        showLoginPin.setOpaque(false);
        showLoginPin.setForeground(PRIMARY_DARK);
        showLoginPin.addActionListener(e -> loginPinField.setEchoChar(showLoginPin.isSelected() ? (char) 0 : loginPinMask));

        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(2, 0, 8, 0);
        loginSection.add(showLoginPin, gbc);

        JButton loginBtn = primaryButton("Login");
        loginBtn.addActionListener(e -> handleUserLogin());

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginSection.add(loginBtn, gbc);

        // ============ ADMIN LOGIN SECTION ============
        JPanel adminSection = makeCardPanel(new GridBagLayout(), sectionWidth, 280);
        gbc = baseGbc();

        JLabel adminTitle = sectionTitle("🛡️ Admin Access");
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        adminSection.add(adminTitle, gbc);

        gbc.gridwidth = 1;
        adminUsernameField = new JTextField();
        adminPasswordField = new JPasswordField();

        addFormRow(adminSection, gbc, 1, "Admin Username", adminUsernameField);
        addFormRow(adminSection, gbc, 2, "Admin Password", adminPasswordField);

        char adminPwMask = adminPasswordField.getEchoChar();
        JCheckBox showAdminPw = new JCheckBox("Show Password");
        showAdminPw.setOpaque(false);
        showAdminPw.setForeground(PRIMARY_DARK);
        showAdminPw.addActionListener(e -> adminPasswordField.setEchoChar(showAdminPw.isSelected() ? (char) 0 : adminPwMask));

        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(2, 0, 8, 0);
        adminSection.add(showAdminPw, gbc);

        JButton adminLoginBtn = secondaryButton("Open Admin Panel");
        adminLoginBtn.addActionListener(e -> handleAdminLogin());

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        adminSection.add(adminLoginBtn, gbc);

        // ============ ADD ALL SECTIONS TO SCROLL ============
        JPanel spacer1 = new JPanel();
        spacer1.setOpaque(false);
        spacer1.setMaximumSize(new Dimension(sectionWidth, 16));

        JPanel spacer2 = new JPanel();
        spacer2.setOpaque(false);
        spacer2.setMaximumSize(new Dimension(sectionWidth, 16));

        scrollContent.add(javax.swing.Box.createVerticalStrut(8));
        scrollContent.add(registerSection);
        scrollContent.add(spacer1);
        scrollContent.add(loginSection);
        scrollContent.add(spacer2);
        scrollContent.add(adminSection);
        scrollContent.add(javax.swing.Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        main.add(scrollPane, BorderLayout.CENTER);

        // Back button
        JButton backBtn = textButton("Back to Home");
        backBtn.addActionListener(e -> showScreen(SCREEN_WELCOME));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backBtn);

        main.add(bottomPanel, BorderLayout.SOUTH);

        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);

        GridBagConstraints ct = new GridBagConstraints();
        ct.gridx = 0;
        ct.gridy = 0;
        ct.fill = GridBagConstraints.BOTH;
        ct.weightx = 1;
        ct.weighty = 1;
        ct.insets = new Insets(20, 20, 20, 20);

        container.add(main, ct);
        return container;
    }

    private JPanel buildRegisterScreen() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        JPanel card = makeCardPanel(new GridBagLayout(), 700, 670);
        GridBagConstraints gbc = baseGbc();

        JLabel title = sectionTitle("Create New Client Account");
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(title, gbc);

        gbc.gridwidth = 1;

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
        gbc.insets = new Insets(20, 10, 12, 10);
        card.add(actions, gbc);

        // Link to login screen
        JLabel alreadyHaveAccount = new JLabel("I already have an account");
        alreadyHaveAccount.setForeground(ACCENT);
        alreadyHaveAccount.setFont(new Font("Segoe UI", Font.BOLD, 11));
        alreadyHaveAccount.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        alreadyHaveAccount.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showScreen(SCREEN_LOGIN);
            }
        });

        JPanel linkPanel = new JPanel();
        linkPanel.setOpaque(false);
        linkPanel.add(alreadyHaveAccount);

        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 12, 10);
        card.add(linkPanel, gbc);

        // Policies/Terms text
        JLabel policiesLabel = new JLabel("<html><center>By creating an account, you agree to our Terms of Service<br/>and Privacy Policy. This system is secure and protected.</center></html>");
        policiesLabel.setForeground(TEXT_LIGHT);
        policiesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        policiesLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 11;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 8, 10);
        card.add(policiesLabel, gbc);

        form.add(card);
        return form;
    }

    private JPanel buildLoginScreen() {
        JPanel holder = new JPanel(new GridBagLayout());
        holder.setOpaque(false);

        JPanel card = makeCardPanel(new GridBagLayout(), 620, 480);
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
        gbc.insets = new Insets(22, 10, 12, 10);
        card.add(actions, gbc);

        // Link to register screen
        JLabel dontHaveAccount = new JLabel("I don't have an account");
        dontHaveAccount.setForeground(ACCENT);
        dontHaveAccount.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dontHaveAccount.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dontHaveAccount.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showScreen(SCREEN_REGISTER);
            }
        });

        JPanel linkPanel = new JPanel();
        linkPanel.setOpaque(false);
        linkPanel.add(dontHaveAccount);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 12, 10);
        card.add(linkPanel, gbc);

        // Policies/Terms text
        JLabel policiesLabel = new JLabel("<html><center>Secure Banking System - All data encrypted and protected<br/>For assistance, please contact our support team.</center></html>");
        policiesLabel.setForeground(TEXT_LIGHT);
        policiesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        policiesLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 8, 10);
        card.add(policiesLabel, gbc);

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

        JButton backBtn = textButton(isLocalhost ? "Exit" : "Back");
        backBtn.addActionListener(e -> {
            adminUsernameField.setText("");
            adminPasswordField.setText("");
            if (isLocalhost) {
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                System.exit(0);
            } else {
                showScreen(SCREEN_WELCOME);
            }
        });

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

    private JPanel buildAdminRegisterScreen() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        JPanel card = makeCardPanel(new GridBagLayout(), 700, 560);
        GridBagConstraints gbc = baseGbc();

        JLabel title = sectionTitle("Admin - Create New Client Account");
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 10, 18, 10);
        card.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);

        JTextField adminRegNameField = new JTextField();
        JTextField adminRegAgeField = new JTextField();
        JTextField adminRegAddressField = new JTextField();
        JTextField adminRegGmailField = new JTextField();
        JTextField adminRegTelephoneField = new JTextField();
        JTextField adminRegUsernameField = new JTextField();
        JPasswordField adminRegPinField = new JPasswordField();

        addFormRow(card, gbc, 1, "Full Name", adminRegNameField);
        addFormRow(card, gbc, 2, "Age", adminRegAgeField);
        addFormRow(card, gbc, 3, "Address", adminRegAddressField);
        addFormRow(card, gbc, 4, "Gmail", adminRegGmailField);
        addFormRow(card, gbc, 5, "Telephone", adminRegTelephoneField);
        addFormRow(card, gbc, 6, "Username", adminRegUsernameField);
        addFormRow(card, gbc, 7, "6-digit PIN", adminRegPinField);

        JButton createBtn = primaryButton("Create Client Account");
        createBtn.addActionListener(e -> {
            try {
                String name = adminRegNameField.getText();
                int age = Integer.parseInt(adminRegAgeField.getText().trim());
                String address = adminRegAddressField.getText();
                String gmail = adminRegGmailField.getText();
                String telephone = adminRegTelephoneField.getText().trim();
                String username = adminRegUsernameField.getText();
                String pin = new String(adminRegPinField.getPassword());

                bankSystem.registerAccount(name, age, address, gmail, telephone, username, pin);
                showInfo("Client account \"" + username + "\" created successfully.");
                adminRegNameField.setText("");
                adminRegAgeField.setText("");
                adminRegAddressField.setText("");
                adminRegGmailField.setText("");
                adminRegTelephoneField.setText("");
                adminRegUsernameField.setText("");
                adminRegPinField.setText("");
                showScreen(SCREEN_ADMIN_DASHBOARD);
            } catch (NumberFormatException ex) {
                showError("Age must be a number.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            } catch (IllegalStateException ex) {
                showError("Database error: " + ex.getMessage());
            }
        });

        JButton backBtn = textButton("Back to Dashboard");
        backBtn.addActionListener(e -> showScreen(SCREEN_ADMIN_DASHBOARD));

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.add(createBtn);
        actions.add(backBtn);

        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 8, 10);
        card.add(actions, gbc);

        form.add(card);
        return form;
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
        JLabel title = new JLabel("Admin - Registered Accounts & Management", SwingConstants.LEFT);
        title.setForeground(TEXT_DARK);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JButton logoutBtn = textButton(isLocalhost ? "Exit Admin" : "Logout");
        logoutBtn.addActionListener(e -> {
            if (isLocalhost) {
                System.exit(0);
            } else {
                showScreen(SCREEN_WELCOME);
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);

        JPanel tableCard = makeCardPanel(new BorderLayout(), 0, 0);
        tableCard.setBorder(new EmptyBorder(14, 14, 14, 14));

        adminTableModel = new DefaultTableModel(
            new Object[] {"ID", "Username", "Holder Name", "Checking", "Savings", "Loan"}, 0
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

        JButton logsBtn = textButton("View Login Logs");
        logsBtn.addActionListener(e -> showLoginLogs());

        actions.add(refreshBtn);
        actions.add(detailsBtn);
        actions.add(deleteBtn);
        actions.add(totalBtn);
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
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(PRIMARY);

        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        input.setPreferredSize(new Dimension(320, 38));
        input.setForeground(TEXT_DARK);

        if (input instanceof JTextField || input instanceof JPasswordField) {
            JTextField textField = (JTextField) input;
            textField.setBackground(SOFT_BG);
            textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            textField.setCaretColor(PRIMARY);
        }

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.insets = new Insets(6, 0, 6, 8);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        panel.add(input, gbc);
    }

    private JPanel makeCardPanel(java.awt.LayoutManager layout, int prefWidth, int prefHeight) {
        JPanel card = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Subtle gradient background - light green to lighter green
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(235, 248, 242),
                    getWidth(), getHeight(), new Color(245, 250, 248)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                // Draw elegant border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        if (prefWidth > 0 && prefHeight > 0) {
            card.setPreferredSize(new Dimension(prefWidth, prefHeight));
        }

        return card;
    }

    private JLabel sectionTitle(String title) {
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(PRIMARY);
        return label;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_DARK);
                } else if (getModel().isArmed()) {
                    g2.setColor(new Color(37, 117, 230));
                } else {
                    g2.setColor(PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g);
            }
        };
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(255, 152, 0));
                } else if (getModel().isArmed()) {
                    g2.setColor(new Color(255, 193, 7));
                } else {
                    g2.setColor(ACCENT);
                }
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g);
            }
        };
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        return button;
    }

    private JButton textButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(230, 235, 245));
                } else if (getModel().isArmed()) {
                    g2.setColor(new Color(240, 245, 250));
                } else {
                    g2.setColor(SOFT_BG);
                }
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                super.paintComponent(g);
            }
        };
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(PRIMARY_DARK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(10, 14, 10, 14));
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
            // Dark Green background to showcase green & gold design
            GradientPaint gp = new GradientPaint(
                0,
                0,
                new Color(0, 77, 48),
                w,
                h,
                new Color(0, 102, 64)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // Subtle accent circles with darker green tint
            g2.setColor(new Color(0, 51, 32, 15));
            g2.fillOval((int) (w * 0.68), -110, 340, 340);
            g2.fillOval(-160, (int) (h * 0.58), 400, 320);
            g2.dispose();
        }
    }
}
