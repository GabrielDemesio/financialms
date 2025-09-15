-- Criação da tabela de usuários (MySQL 8+ / 9+)
CREATE TABLE IF NOT EXISTS users (
                                     id         BIGINT NOT NULL AUTO_INCREMENT,
                                     name       VARCHAR(150) NOT NULL,
                                     email      VARCHAR(255) NOT NULL,
                                     cpf        CHAR(11) NULL,
                                     password   VARCHAR(255) NOT NULL,
                                     role       VARCHAR(30)  NOT NULL DEFAULT 'USER',
                                     created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                     PRIMARY KEY (id),
                                     UNIQUE KEY uk_users_email (email),
                                     UNIQUE KEY uk_users_cpf   (cpf)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
