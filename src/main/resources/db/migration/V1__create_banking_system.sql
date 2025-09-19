-- Criação do sistema bancário completo
-- V1: Sistema bancário com contas, transações e controle de gastos

-- Tabela de usuários com CPF
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(120) UNIQUE,
    phone VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_users_cpf (cpf),
    INDEX idx_users_email (email)
);

-- Tabela de contas bancárias
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    account_type ENUM('CHECKING', 'SAVINGS') NOT NULL DEFAULT 'CHECKING',
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_accounts_user_id (user_id),
    INDEX idx_accounts_number (account_number)
);

-- Tabela de transações bancárias (depósito, saque, transferência)
CREATE TABLE bank_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    description VARCHAR(255),
    reference_transaction_id BIGINT NULL,
    occurred_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (reference_transaction_id) REFERENCES bank_transactions(id),
    INDEX idx_bank_transactions_account (account_id),
    INDEX idx_bank_transactions_date (occurred_at),
    INDEX idx_bank_transactions_type (transaction_type)
);

-- Tabela de categorias para controle de gastos
CREATE TABLE expense_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(80) NOT NULL,
    color VARCHAR(7),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_expense_categories_user (user_id),
    UNIQUE KEY uk_expense_categories_user_name (user_id, name)
);

-- Tabela de gastos/despesas categorizadas
CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    merchant VARCHAR(120),
    occurred_at DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES expense_categories(id),
    INDEX idx_expenses_user (user_id),
    INDEX idx_expenses_category (category_id),
    INDEX idx_expenses_date (occurred_at),
    INDEX idx_expenses_user_date (user_id, occurred_at)
);

-- Tabela de orçamentos mensais por categoria
CREATE TABLE budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    budget_month DATE NOT NULL,
    limit_amount DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES expense_categories(id),
    INDEX idx_budgets_user (user_id),
    INDEX idx_budgets_category (category_id),
    INDEX idx_budgets_period (budget_month),
    UNIQUE KEY uk_budgets_user_category_period (user_id, category_id, budget_month)
);

-- Categorias padrão serão criadas quando o usuário se registrar
