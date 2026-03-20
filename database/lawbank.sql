CREATE DATABASE IF NOT EXISTS lawbank;
USE lawbank;

DROP PROCEDURE IF EXISTS initialize_lawbank_schema;
DELIMITER $$
CREATE PROCEDURE initialize_lawbank_schema()
BEGIN
    CREATE TABLE IF NOT EXISTS accounts (
        id INT AUTO_INCREMENT PRIMARY KEY,
        account_holder VARCHAR(120) NOT NULL,
        age INT NOT NULL,
        address VARCHAR(255) NOT NULL,
        gmail VARCHAR(180) NOT NULL,
        telephone VARCHAR(20) NOT NULL,
        username VARCHAR(80) NOT NULL UNIQUE,
        pin VARCHAR(255) NOT NULL,
        checking_balance DOUBLE NOT NULL DEFAULT 0,
        savings_balance DOUBLE NOT NULL DEFAULT 0,
        loan_amount DOUBLE NOT NULL DEFAULT 0
    );

    CREATE TABLE IF NOT EXISTS admins (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(80) NOT NULL UNIQUE,
        password VARCHAR(120) NOT NULL
    );

    CREATE TABLE IF NOT EXISTS login_logs (
        id INT AUTO_INCREMENT PRIMARY KEY,
        role VARCHAR(20) NOT NULL,
        username VARCHAR(80) NOT NULL,
        success BOOLEAN NOT NULL,
        notes VARCHAR(255),
        logged_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS transactions (
        id INT AUTO_INCREMENT PRIMARY KEY,
        account_id INT NOT NULL,
        username VARCHAR(80) NOT NULL,
        transaction_type VARCHAR(50) NOT NULL,
        amount DOUBLE NOT NULL,
        description VARCHAR(255),
        checking_balance_after DOUBLE,
        savings_balance_after DOUBLE,
        transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );
END$$
DELIMITER ;

CALL initialize_lawbank_schema();

-- ============================================================================
-- TRANSACTION LOGGING STORED PROCEDURE
-- ============================================================================
-- CALL log_transaction(
--     account_id,           -- The account performing transaction
--     username,             -- Account username
--     transaction_type,     -- DEPOSIT, WITHDRAW, TRANSFER_TO_SAVINGS, etc.
--     amount,              -- Transaction amount
--     description,         -- Transaction details
--     checking_balance_after,   -- Checking balance after transaction
--     savings_balance_after     -- Savings balance after transaction
-- );
-- ============================================================================
DROP PROCEDURE IF EXISTS log_transaction;
DELIMITER $$
CREATE PROCEDURE log_transaction(
    IN p_account_id INT,
    IN p_username VARCHAR(80),
    IN p_transaction_type VARCHAR(50),
    IN p_amount DOUBLE,
    IN p_description VARCHAR(255),
    IN p_checking_balance_after DOUBLE,
    IN p_savings_balance_after DOUBLE
)
BEGIN
    INSERT INTO transactions (
        account_id, 
        username, 
        transaction_type, 
        amount, 
        description, 
        checking_balance_after, 
        savings_balance_after
    ) VALUES (
        p_account_id,
        p_username,
        p_transaction_type,
        p_amount,
        p_description,
        p_checking_balance_after,
        p_savings_balance_after
    );
END$$
DELIMITER ;

-- ============================================================================
-- TRANSACTION TYPES DOCUMENTATION
-- ============================================================================
-- The transactions table tracks all financial operations:
-- 
-- Transaction Types:
-- 1. DEPOSIT - Money deposited to checking account
-- 2. WITHDRAW - Cash withdrawal from checking account
-- 3. TRANSFER_TO_SAVINGS - Transfer from checking to savings account
-- 4. WITHDRAW_FROM_SAVINGS - Cash withdrawal from savings account
-- 5. REQUEST_LOAN - New loan request (amount added to loan_amount)
-- 6. PAY_LOAN - Loan payment (reduces loan_amount)
--
-- Each transaction logs:
-- - account_id: The account ID
-- - username: Account username
-- - transaction_type: One of the above types
-- - amount: Transaction amount
-- - description: Transaction details
-- - checking_balance_after: Checking account balance after transaction
-- - savings_balance_after: Savings account balance after transaction
-- - transaction_date: Timestamp of when transaction occurred
--
-- ============================================================================
-- HOW TO USE THE STORED PROCEDURE
-- ============================================================================
-- CALL log_transaction(account_id, username, transaction_type, amount, description, checking_balance_after, savings_balance_after);
--
-- Example: User deposits $500 to checking account
-- CALL log_transaction(1, 'testuser', 'DEPOSIT', 500, 'Deposited to checking account', 500, 0);
--
-- Example: User withdraws $100 from checking account
-- CALL log_transaction(1, 'testuser', 'WITHDRAW', 100, 'Withdrew from checking account', 400, 0);
--
-- Example: User transfers $200 from checking to savings
-- CALL log_transaction(1, 'testuser', 'TRANSFER_TO_SAVINGS', 200, 'Transferred from checking to savings', 200, 200);
--
-- Example: User withdraws $50 from savings account
-- CALL log_transaction(1, 'testuser', 'WITHDRAW_FROM_SAVINGS', 50, 'Withdrew from savings account', 200, 150);
--
-- Example: User requests a loan of $1000 (5% interest = $1050 total)
-- CALL log_transaction(1, 'testuser', 'REQUEST_LOAN', 1000, 'Loan requested - Total: 1050', 200, 150);
--
-- Example: User pays loan in full
-- CALL log_transaction(1, 'testuser', 'PAY_LOAN', 1050, 'Loan paid in full', 200, 150);
--
-- ============================================================================
