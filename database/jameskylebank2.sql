CREATE DATABASE IF NOT EXISTS jameskylebank2;
USE jameskylebank2;

CREATE TABLE IF NOT EXISTS accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_holder VARCHAR(120) NOT NULL,
    age INT NOT NULL,
    address VARCHAR(255) NOT NULL,
    gmail VARCHAR(180) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    username VARCHAR(80) NOT NULL UNIQUE,
    pin INT NOT NULL,
    checking_balance DOUBLE NOT NULL DEFAULT 0,
    savings_balance DOUBLE NOT NULL DEFAULT 0,
    loan_amount DOUBLE NOT NULL DEFAULT 0
);
