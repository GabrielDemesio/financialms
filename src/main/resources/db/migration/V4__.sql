CREATE TABLE budgets
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    user_id          BIGINT                NOT NULL,
    month            date                  NOT NULL,
    category_id      BIGINT                NOT NULL,
    amount           DECIMAL(12, 2)        NOT NULL,
    transaction_type VARCHAR(10)           NOT NULL,
    is_recurring     BIT(1)                NOT NULL,
    merchant         VARCHAR(120)          NULL,
    CONSTRAINT pk_budgets PRIMARY KEY (id)
);

CREATE TABLE categories
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    user_id       BIGINT                NOT NULL,
    name          VARCHAR(80)           NOT NULL,
    category_kind VARCHAR(20)           NOT NULL,
    color         VARCHAR(7)            NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE transactions
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    user_id          BIGINT                NOT NULL,
    category_id      BIGINT                NOT NULL,
    ocurred_at       date                  NOT NULL,
    `description`    VARCHAR(255)          NULL,
    amount           DECIMAL(12, 2)        NOT NULL,
    transaction_type VARCHAR(10)           NOT NULL,
    is_recurring     BIT(1)                NOT NULL,
    merchant         VARCHAR(120)          NULL,
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    name       VARCHAR(100)          NOT NULL,
    email      VARCHAR(150)          NOT NULL,
    password   VARCHAR(255)          NOT NULL,
    phone      VARCHAR(15)           NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NULL,
    is_active  BIT(1)                NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE budgets
    ADD CONSTRAINT FK_BUDGETS_ON_CATEGORYID FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);