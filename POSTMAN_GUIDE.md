# 📮 Guia da Collection Postman - Financial Banking System

## 🚀 Como Importar e Usar

### 1. **Importar no Postman**
1. Abra o Postman
2. Clique em **Import**
3. Arraste os arquivos:
   - `postman_collection.json`
   - `postman_environment.json`
4. Selecione o environment **"Financial Banking System - Local"**

### 2. **Configuração Inicial**
- **Base URL:** `http://localhost:8080` (já configurado)
- **JWT Token:** Será preenchido automaticamente após login
- Certifique-se que a aplicação está rodando na porta 8080

## 🔄 Fluxo de Teste Recomendado

### **Passo 1: Autenticação** 🔐
1. **Register User** - Criar um novo usuário
2. **Login** - Fazer login (JWT token será salvo automaticamente)

### **Passo 2: Operações Bancárias** 🏦
1. **Create Account** - Criar conta bancária
2. **Get My Accounts** - Listar contas (account_id será salvo automaticamente)
3. **Deposit Money** - Fazer depósito inicial
4. **Get Account Balance** - Verificar saldo
5. **Withdraw Money** - Fazer saque
6. **Transfer Money** - Fazer transferência (precisa de outra conta)
7. **Get Transaction History** - Ver histórico

### **Passo 3: Controle de Gastos** 💰
1. **Create Expense Category** - Criar categoria de gasto
2. **Get Expense Categories** - Listar categorias
3. **Add Expense** - Adicionar gasto
4. **Get My Expenses** - Ver todos os gastos
5. **Get Expenses by Month** - Ver gastos do mês

### **Passo 4: Orçamentos** 📊
1. **Create Budget** - Criar orçamento mensal
2. **Get My Budgets** - Ver orçamentos
3. **Get Budget by Month** - Ver orçamento específico

### **Passo 5: Perfil** 👤
1. **Get My Profile** - Ver perfil
2. **Update Profile** - Atualizar dados

## 🔧 Variáveis Automáticas

A collection possui scripts que automaticamente:
- **Salvam o JWT token** após login
- **Salvam o user_id** após registro
- **Salvam o account_id** após criar conta
- **Validam respostas** com testes automáticos

## 📝 Dados de Exemplo

### **Usuário de Teste:**
```json
{
    "cpf": "12345678901",
    "name": "João Silva",
    "email": "joao@email.com",
    "password": "senha123",
    "phone": "11999999999"
}
```

### **Login:**
```json
{
    "cpf": "12345678901",
    "password": "senha123"
}
```

### **Depósito:**
```json
{
    "accountId": "{{account_id}}",
    "amount": 1000.00,
    "description": "Depósito inicial"
}
```

### **Categoria de Gasto:**
```json
{
    "name": "Alimentação",
    "color": "#FF6B6B"
}
```

### **Gasto:**
```json
{
    "categoryId": 1,
    "amount": 25.50,
    "description": "Almoço no restaurante"
}
```

## 🎯 Endpoints Principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/register` | Registrar usuário |
| POST | `/auth/login` | Login com CPF |
| GET | `/accounts` | Listar contas |
| POST | `/accounts` | Criar conta |
| POST | `/bank-transactions/deposit` | Depósito |
| POST | `/bank-transactions/withdraw` | Saque |
| POST | `/bank-transactions/transfer` | Transferência |
| GET | `/bank-transactions/account/{id}` | Histórico |
| GET | `/expense-categories` | Categorias |
| POST | `/expenses` | Adicionar gasto |
| GET | `/expenses` | Listar gastos |
| POST | `/budgets` | Criar orçamento |
| GET | `/users/profile` | Ver perfil |

## ⚠️ Observações Importantes

1. **Ordem dos Testes:** Siga a ordem recomendada para evitar erros
2. **JWT Token:** Válido por tempo limitado, refaça login se expirar
3. **Account ID:** Necessário para operações bancárias
4. **CPF Único:** Cada usuário deve ter CPF único
5. **Saldo:** Verifique saldo antes de saques/transferências

## 🐛 Troubleshooting

- **401 Unauthorized:** Token expirado, faça login novamente
- **404 Not Found:** Verifique se a aplicação está rodando
- **400 Bad Request:** Verifique os dados enviados
- **500 Internal Error:** Verifique logs da aplicação

## 🎉 Pronto para Testar!

Agora você pode testar completamente o sistema bancário! 🏦✨
