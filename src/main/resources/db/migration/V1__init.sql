/* ============================================================================
   V1__init.sql  |  Schema inicial do financialms (MySQL 8+)
   ----------------------------------------------------------------------------
   Observações:
   - Usa ENGINE=InnoDB e utf8mb4.
   - Índices criados dentro dos CREATE TABLE.
   - FKs usam ON DELETE CASCADE quando o registro é do usuário.
   - Ajuste nomes/tamanhos conforme necessidade do JPA.
   ============================================================================ */

-- Recomendado em DEV: garantir fuso e collation padronizados
SET NAMES utf8mb4;
SET time_zone = '+00:00';

-- ============================================================================
-- USERS
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
                                     id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name         VARCHAR(150)  NOT NULL,
    email        VARCHAR(255)  NOT NULL,
    cpf          CHAR(11)      NULL,
    password     VARCHAR(255)  NOT NULL,
    role         VARCHAR(30)   NOT NULL DEFAULT 'USER',
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uc_users_email UNIQUE (email),
    CONSTRAINT uc_users_cpf   UNIQUE (cpf)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- CATEGORIES (por usuário opcionalmente; nome único por usuário)
-- ============================================================================
CREATE TABLE IF NOT EXISTS categories (
                                          id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          user_id      BIGINT       NULL,
                                          name         VARCHAR(120) NOT NULL,
    type         ENUM('INCOME','EXPENSE') NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uc_cat_user_name UNIQUE (user_id, name),
    CONSTRAINT FK_CATEGORIES_ON_USER FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE,
    KEY idx_categories_user (user_id),
    KEY idx_categories_type (type)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- BUDGETS (planejamento por categoria/mês)
-- ============================================================================
CREATE TABLE IF NOT EXISTS budgets (
                                       id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       user_id          BIGINT       NOT NULL,
                                       category_id      BIGINT       NOT NULL,
                                       period_year      INT          NOT NULL,
                                       period_month     TINYINT      NOT NULL,
                                       limit_amount     DECIMAL(15,2) NOT NULL,
    transaction_type ENUM('INCOME','EXPENSE') NOT NULL DEFAULT 'EXPENSE',
    is_recurring     BIT(1)       NOT NULL DEFAULT 0,
    merchant         VARCHAR(120) NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_budgets_month CHECK (period_month BETWEEN 1 AND 12),
    CONSTRAINT uc_budget_user_cat_period UNIQUE (user_id, category_id, period_year, period_month),
    CONSTRAINT FK_BUDGETS_ON_USER FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT FK_BUDGETS_ON_CATEGORYID FOREIGN KEY (category_id)
    REFERENCES categories (id) ON DELETE RESTRICT,
    KEY idx_budgets_user_period (user_id, period_year, period_month),
    KEY idx_budgets_category (category_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- INVOICES (fatura parcelada) + suas parcelas
-- ============================================================================
CREATE TABLE IF NOT EXISTS invoices (
                                        id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        user_id            BIGINT       NOT NULL,
                                        product_name       VARCHAR(255) NOT NULL,
    total_installments INT          NOT NULL,
    total_value        DECIMAL(15,2) NOT NULL,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_invoices_installments CHECK (total_installments >= 1),
    CONSTRAINT FK_INVOICES_ON_USER FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE,
    KEY idx_invoices_user (user_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS invoice_installments (
                                                    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                    invoice_id          BIGINT       NOT NULL,
                                                    installment_number  INT          NOT NULL,
                                                    due_date            DATE         NOT NULL,
                                                    value               DECIMAL(15,2) NOT NULL,
    is_paid             BIT(1)       NOT NULL DEFAULT 0,
    paid_at             TIMESTAMP    NULL,
    CONSTRAINT uc_invoice_installment UNIQUE (invoice_id, installment_number),
    CONSTRAINT chk_installment_number CHECK (installment_number >= 1),
    CONSTRAINT FK_INVOICE_ITEMS_ON_INVOICE FOREIGN KEY (invoice_id)
    REFERENCES invoices (id) ON DELETE CASCADE,
    KEY idx_invoice_items_invoice (invoice_id),
    KEY idx_invoice_items_due_date (due_date)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- TRANSACTIONS (movimentações gerais)
-- ============================================================================
CREATE TABLE IF NOT EXISTS transactions (
                                            id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            user_id      BIGINT        NOT NULL,
                                            category_id  BIGINT        NULL,
                                            invoice_id   BIGINT        NULL,
                                            amount       DECIMAL(15,2) NOT NULL,
    type         ENUM('INCOME','EXPENSE','TRANSFER') NOT NULL,
    description  VARCHAR(255)  NULL,
    merchant     VARCHAR(120)  NULL,
    is_recurring BIT(1)        NOT NULL DEFAULT 0,
    occurred_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_TRANSACTIONS_ON_USER FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT FK_TRANSACTIONS_ON_CATEGORY FOREIGN KEY (category_id)
    REFERENCES categories (id) ON DELETE SET NULL,
    CONSTRAINT FK_TRANSACTIONS_ON_INVOICE FOREIGN KEY (invoice_id)
    REFERENCES invoices (id) ON DELETE SET NULL,
    KEY idx_transactions_user_date (user_id, occurred_at),
    KEY idx_transactions_category (category_id),
    KEY idx_transactions_invoice (invoice_id),
    KEY idx_transactions_type (type)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- DEPOSITS (entradas avulsas)
-- ============================================================================
CREATE TABLE IF NOT EXISTS deposits (
                                        id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        user_id      BIGINT        NOT NULL,
                                        amount       DECIMAL(15,2) NOT NULL,
    description  VARCHAR(255)  NULL,
    occurred_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_DEPOSITS_ON_USER FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE,
    KEY idx_deposits_user_date (user_id, occurred_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- INVESTMENTS (registros de investimento/financeiro)
-- ============================================================================
CREATE TABLE IF NOT EXISTS investments (
                                           id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           user_id      BIGINT        NOT NULL,
                                           asset        VARCHAR(20)   NULL,
    broker       VARCHAR(120)  NULL,
    operation    ENUM('BUY','SELL','DEPOSIT','WITHDRAW') NOT NULL DEFAULT 'BUY',
    shares       DECIMAL(18,6) NULL,
    price        DECIMAL(15,6) NULL,
    amount       DECIMAL(15,2) NOT NULL,
    occurred_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_INVESTMENTS_ON_USER FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE,
    KEY idx_investments_user_date (user_id, occurred_at),
    KEY idx_investments_asset (asset)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ---------------------------------------------------------------------------
   Opcional (apenas DEV): inserir um usuário para testes.
   -> Gere um hash BCrypt compatível com teu PasswordEncoder e substitua abaixo.
   INSERT INTO users (name, email, cpf, password, role)
   VALUES ('Dev', 'dev@example.com', '12345678901', '<BCrypt>', 'USER');
--------------------------------------------------------------------------- */
