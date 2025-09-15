-- V4__constraints_and_budget_extras.sql (substitui o conteúdo da tua V4__.sql)

-- Acrescentar colunas novas em budgets (se ainda não existirem):
ALTER TABLE budgets
    ADD COLUMN IF NOT EXISTS transaction_type VARCHAR(10) NOT NULL DEFAULT 'EXPENSE',
    ADD COLUMN IF NOT EXISTS is_recurring     BIT(1)      NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS merchant         VARCHAR(120) NULL;

-- Garantir UNIQUE em users.email:
ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

-- Garantir FKs (só se não existirem ainda):
-- MySQL não tem "ADD CONSTRAINT IF NOT EXISTS", então você pode checar antes ou nomear consistentemente e aceitar o erro benigno em dev.
-- Uma forma defensiva é testar via information_schema e executar condicionalmente. Exemplo simples:

-- FK budgets.category_id -> categories.id
DO
    BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = DATABASE()
          AND table_name = 'budgets'
          AND constraint_name = 'FK_BUDGETS_ON_CATEGORYID'
    ) THEN
ALTER TABLE budgets
    ADD CONSTRAINT FK_BUDGETS_ON_CATEGORYID
        FOREIGN KEY (category_id) REFERENCES categories (id);
END IF;
END;

-- FK transactions.category_id -> categories.id
DO
    BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = DATABASE()
          AND table_name = 'transactions'
          AND constraint_name = 'FK_TRANSACTIONS_ON_CATEGORY'
    ) THEN
ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_CATEGORY
        FOREIGN KEY (category_id) REFERENCES categories (id);
END IF;
END;
