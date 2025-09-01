# FinCoach — Backend (Spring Boot 3 + MySQL)

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)
![Build](https://img.shields.io/badge/build-Maven-blue?logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-informational)

Aplicação de **controle de gastos mensais** com relatórios agregados e **insights** práticos para reduzir despesas. Este repositório contém **apenas o back‑end**.

> **Stack**: Java 21 • Spring Boot 3 • Spring Data JPA • Validation • Flyway • MySQL 8

---

## Sumário

* [✳️ Funcionalidades](#️-funcionalidades)
* [🏗️ Arquitetura](#️-arquitetura)
* [🧰 Requisitos](#-requisitos)
* [🚀 Comece agora](#-comece-agora)

  * [Rodando localmente](#rodando-localmente)
  * [Rodando com Docker](#rodando-com-docker)
* [⚙️ Configuração](#️-configuração)

  * [Variáveis de ambiente](#variáveis-de-ambiente)
  * [Banco & Migrações](#banco--migrações)
* [🧭 API](#-api)

  * [Autorização simulada](#autorização-simulada)
  * [Endpoints](#endpoints)
  * [Exemplos (curl)](#exemplos-curl)
  * [Erros](#erros)
* [📁 Estrutura de pastas](#-estrutura-de-pastas)
* [🗺️ Roadmap](#️-roadmap)
* [🤝 Contribuição](#-contribuição)
* [📜 Licença](#-licença)

---

## ✳️ Funcionalidades

* CRUD de **categorias** de gasto/receita (fixo, variável, dívida, investimento)
* **Orçamentos** por categoria por mês
* **Lançamento de transações** (despesa/receita), com marcação de recorrência
* **Relatório mensal** (totais de receita/depesa, por categoria)
* **Insights** automáticos (estouro de orçamento, recorrências, alerta despesa/renda)

---

## 🏗️ Arquitetura

```
Controller → Service → Repository (JPA) → MySQL
               ↑
             DTOs
               ↑
            Validation
```

* **Flyway** para versionamento do schema (`src/main/resources/db/migration`)
* **DTOs + Bean Validation** para requests/responses
* Timezone padrão: `America/Sao_Paulo`

---

## 🧰 Requisitos

* Java **21**
* Maven **3.9+**
* MySQL **8.0** (ou compatível)

---

## 🚀 Comece agora

### Rodando localmente

1. Crie o banco (ajuste collation se quiser):

```sql
CREATE DATABASE fincoach CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

2. Configure credenciais no `application.yml` (ou via variáveis de ambiente, ver abaixo).

3. Suba a aplicação:

```bash
mvn spring-boot:run
# ou
mvn clean package -DskipTests && java -jar target/fincoach-backend-0.1.0.jar
```

O Flyway executará as migrações automaticamente.

### Rodando com Docker

**Dockerfile** (já previsto no README; crie no root do projeto):

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/fincoach-backend-0.1.0.jar app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
```

**docker-compose.yml** (API + MySQL):

```yaml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: fincoach
      TZ: America/Sao_Paulo
    ports: ["3306:3306"]
    volumes:
      - mysql_data:/var/lib/mysql

  api:
    build: .
    depends_on: [mysql]
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/fincoach?useSSL=false&serverTimezone=America/Sao_Paulo
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    ports: ["8080:8080"]

volumes:
  mysql_data:
```

**Passos**:

```bash
mvn clean package -DskipTests
docker compose up --build
```

---

## ⚙️ Configuração

### Variáveis de ambiente

Você pode sobrescrever o `application.yml` com env vars (útil em Docker/CI/CD):

| Variável                     | Exemplo                                                                              | Descrição        |
| ---------------------------- | ------------------------------------------------------------------------------------ | ---------------- |
| `SPRING_DATASOURCE_URL`      | `jdbc:mysql://localhost:3306/fincoach?useSSL=false&serverTimezone=America/Sao_Paulo` | URL JDBC         |
| `SPRING_DATASOURCE_USERNAME` | `root`                                                                               | Usuário do MySQL |
| `SPRING_DATASOURCE_PASSWORD` | `root`                                                                               | Senha do MySQL   |
| `SERVER_PORT`                | `8080`                                                                               | Porta HTTP       |

### Banco & Migrações

* Migrações Flyway em `src/main/resources/db/migration`
* `V1__init.sql` cria `categories`, `budgets`, `transactions` e índices
* Dica: crie `V2__seed.sql` com dados fake para demo

---

## 🧭 API

Base URL padrão: `http://localhost:8080`

### Autorização simulada

Para o MVP, o ID do usuário é lido do header `X-User-Id`. Se omitido, assume `1`.

> Em produção: substituir por autenticação JWT e `@AuthenticationPrincipal`.

### Endpoints

**Categorias**

* `GET /categories` → lista
* `POST /categories` → cria

**Orçamentos**

* `GET /budgets?month=YYYY-MM` → lista por mês
* `POST /budgets` → cria/atualiza (upsert)

**Transações**

* `GET /transactions?month=YYYY-MM` → lista por mês
* `POST /transactions` → cria
* `DELETE /transactions/{id}` → remove

**Relatórios**

* `GET /reports/monthly-summary?month=YYYY-MM`

**Insights**

* `GET /insights?month=YYYY-MM`

### Exemplos (curl)

```bash
# Criar categoria
curl -s -X POST http://localhost:8080/categories \
  -H 'Content-Type: application/json' \
  -d '{"name":"Mercado","kind":"VARIAVEL","color":"#4CAF50"}'

# Definir orçamento mensal
curl -s -X POST http://localhost:8080/budgets \
  -H 'Content-Type: application/json' \
  -d '{"categoryId":1,"month":"2025-08","amount":1200.00}'

# Lançar transação
curl -s -X POST http://localhost:8080/transactions \
  -H 'Content-Type: application/json' \
  -d '{"categoryId":1,"occurredAt":"2025-08-15","amount":250.00,"type":"EXPENSE","description":"Compras semanais","recurring":false,"merchant":"Assaí"}'

# Resumo mensal e insights
curl -s 'http://localhost:8080/reports/monthly-summary?month=2025-08'
curl -s 'http://localhost:8080/insights?month=2025-08'
```

### Erros

```json
{ "message": "Descrição do erro" }
```

Validação (400):

```json
{
  "message": "Validation failed",
  "errors": { "amount": "must be greater than or equal to 0.00" }
}
```
