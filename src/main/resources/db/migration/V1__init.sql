-- Schema inicial
CREATE TABLE categories (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            user_id BIGINT NOT NULL,
                            name VARCHAR(80) NOT NULL,
                            kind ENUM('FIXO','VARIAVEL','DIVIDA','INVESTIMENTO') NOT NULL,
                            color VARCHAR(7) NULL
);
CREATE INDEX idx_categories_user ON categories(user_id);


CREATE TABLE budgets (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         month DATE NOT NULL,
                         category_id BIGINT NOT NULL,
                         amount DECIMAL(12,2) NOT NULL,
                         UNIQUE KEY uq_budget (user_id, month, category_id),
                         CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE INDEX idx_budgets_user_month ON budgets(user_id, month);


CREATE TABLE transactions (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              category_id BIGINT NOT NULL,
                              occurred_at DATE NOT NULL,
                              description VARCHAR(255) NULL,
                              amount DECIMAL(12,2) NOT NULL,
                              type ENUM('EXPENSE','INCOME') NOT NULL DEFAULT 'EXPENSE',
                              is_recurring BOOLEAN DEFAULT FALSE,
                              merchant VARCHAR(120) NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_tx_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE INDEX idx_tx_user_month ON transactions(user_id, occurred_at);
CREATE INDEX idx_tx_user_cat ON transactions(user_id, category_id, occurred_at);