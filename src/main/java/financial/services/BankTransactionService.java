package financial.services;

import financial.entities.Account;
import financial.entities.BankTransaction;
import financial.repositories.AccountRepository;
import financial.repositories.BankTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankTransactionService {
    
    private final BankTransactionRepository bankTransactionRepository;
    private final AccountRepository accountRepository;
    
    public Page<BankTransaction> getAccountTransactions(Long accountId, Pageable pageable) {
        return bankTransactionRepository.findByAccountIdOrderByOccurredAtDesc(accountId, pageable);
    }
    
    public List<BankTransaction> getAccountTransactionsByPeriod(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return bankTransactionRepository.findAccountTransactionsByPeriod(accountId, startDate, endDate);
    }
    
    @Transactional
    public BankTransaction deposit(Long accountId, BigDecimal amount, String description) {
        Account account = getAccount(accountId);
        
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        BankTransaction transaction = new BankTransaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType(BankTransaction.TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(description != null ? description : "Depósito");
        
        return bankTransactionRepository.save(transaction);
    }
    
    @Transactional
    public BankTransaction withdraw(Long accountId, BigDecimal amount, String description) {
        Account account = getAccount(accountId);
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }
        
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        BankTransaction transaction = new BankTransaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType(BankTransaction.TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(description != null ? description : "Saque");
        
        return bankTransactionRepository.save(transaction);
    }
    
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        Account fromAccount = getAccount(fromAccountId);
        Account toAccount = getAccount(toAccountId);
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }
        
        // Debita da conta origem
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
        fromAccount.setBalance(newFromBalance);
        accountRepository.save(fromAccount);
        
        // Credita na conta destino
        BigDecimal newToBalance = toAccount.getBalance().add(amount);
        toAccount.setBalance(newToBalance);
        accountRepository.save(toAccount);
        
        // Cria transação de saída
        BankTransaction outTransaction = new BankTransaction();
        outTransaction.setAccountId(fromAccountId);
        outTransaction.setTransactionType(BankTransaction.TransactionType.TRANSFER_OUT);
        outTransaction.setAmount(amount);
        outTransaction.setBalanceAfter(newFromBalance);
        outTransaction.setDescription(description != null ? description : "Transferência enviada para " + toAccount.getAccountNumber());
        BankTransaction savedOutTransaction = bankTransactionRepository.save(outTransaction);
        
        // Cria transação de entrada
        BankTransaction inTransaction = new BankTransaction();
        inTransaction.setAccountId(toAccountId);
        inTransaction.setTransactionType(BankTransaction.TransactionType.TRANSFER_IN);
        inTransaction.setAmount(amount);
        inTransaction.setBalanceAfter(newToBalance);
        inTransaction.setDescription(description != null ? description : "Transferência recebida de " + fromAccount.getAccountNumber());
        inTransaction.setReferenceTransactionId(savedOutTransaction.getId());
        bankTransactionRepository.save(inTransaction);
        
        // Atualiza a referência na transação de saída
        savedOutTransaction.setReferenceTransactionId(inTransaction.getId());
        bankTransactionRepository.save(savedOutTransaction);
    }
    
    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }
}
