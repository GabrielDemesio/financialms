CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    name       VARCHAR(100)          NOT NULL,
    email      VARCHAR(150)          NOT NULL,
    password   VARCHAR(255)          NOT NULL,
    phone      VARCHAR(15)           NULL,
    created_at DATETIME              NOT NULL,
    updated_at DATETIME              NULL,
    is_active  BIT(1)                NOT NULL DEFAULT 1,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- Criar índice para email para otimizar consultas de login
CREATE INDEX idx_users_email ON users (email);

-- Criar índice para active para otimizar consultas de usuários ativos
CREATE INDEX idx_users_active ON users (is_active);
