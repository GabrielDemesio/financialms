# Sistema de Autenticação - FinancialMS

## Visão Geral

O sistema agora possui autenticação completa com JWT (JSON Web Tokens), similar ao sistema bancário do Itaú. Cada usuário tem sua própria conta e todas as transações, categorias e orçamentos são vinculados ao usuário autenticado.

## Funcionalidades Implementadas

### 🔐 Autenticação
- **Registro de usuários** com validação de dados
- **Login seguro** com JWT
- **Validação de tokens** em todas as rotas protegidas
- **Perfil do usuário** autenticado

### 🏦 Isolamento de Dados
- Cada usuário só acessa seus próprios dados
- Transações vinculadas ao usuário autenticado
- Categorias personalizadas por usuário
- Orçamentos individuais por conta

## Endpoints de Autenticação

### Registro
```http
POST /auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "phone": "11999999999"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "name": "João Silva",
  "email": "joao@example.com",
  "expiresAt": "2025-09-16T10:30:00"
}
```

### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Resposta:** (mesmo formato do registro)

### Perfil do Usuário
```http
GET /auth/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Resposta:**
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@example.com",
  "phone": "11999999999",
  "createdAt": "2025-09-15T08:00:00"
}
```

## Como Usar

### 1. Registrar um Usuário
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "password": "senha123",
    "phone": "11999999999"
  }'
```

### 2. Fazer Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

### 3. Usar o Token nas Requisições
```bash
# Listar transações (agora requer autenticação)
curl -X GET "http://localhost:8080/transactions?month=2025-09" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"

# Criar categoria
curl -X POST http://localhost:8080/categories \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mercado",
    "kind": "VARIAVEL",
    "color": "#4CAF50"
  }'
```

## Mudanças nos Endpoints Existentes

### ⚠️ BREAKING CHANGES

Todos os endpoints agora requerem autenticação JWT. O header `X-User-Id` foi removido.

**Antes:**
```http
GET /transactions?month=2025-09
X-User-Id: 1
```

**Agora:**
```http
GET /transactions?month=2025-09
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Endpoints Protegidos
- `GET/POST /categories` - Categorias do usuário
- `GET/POST /budgets` - Orçamentos do usuário  
- `GET/POST/DELETE /transactions` - Transações do usuário
- `GET /reports/monthly-summary` - Relatórios do usuário
- `GET /insights` - Insights do usuário

### Endpoints Públicos
- `POST /auth/register` - Registro
- `POST /auth/login` - Login
- `GET /auth/validate` - Validação de token

## Configuração

### Variáveis de Ambiente
```bash
# Chave secreta para JWT (recomendado em produção)
JWT_SECRET=sua_chave_secreta_muito_segura_aqui

# Tempo de expiração do token em millisegundos (padrão: 24h)
JWT_EXPIRATION=86400000
```

### Banco de Dados
A migração `V3__create_users_table.sql` será executada automaticamente, criando a tabela `users`.

## Segurança

### 🔒 Recursos de Segurança
- Senhas criptografadas com BCrypt
- Tokens JWT com expiração
- Validação de entrada em todos os endpoints
- Isolamento completo de dados por usuário

### 🛡️ Boas Práticas
- Use HTTPS em produção
- Configure uma chave JWT forte
- Monitore tentativas de login
- Implemente rate limiting se necessário

## Testes

Execute os testes de autenticação:
```bash
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=AuthControllerTest
```

## Próximos Passos

Para uma implementação completa tipo banco, considere adicionar:
- Recuperação de senha por email
- Autenticação de dois fatores (2FA)
- Logs de auditoria
- Bloqueio de conta após tentativas falhadas
- Refresh tokens
- Roles e permissões granulares
