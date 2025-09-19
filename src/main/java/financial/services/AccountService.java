package financial.services;

import financial.entities.Account;
import financial.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
    
    private final AccountRepository accountRepository;
    
    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserIdAndActiveTrue(userId);
    }
    
    public Optional<Account> getAccountById(Long accountId, Long userId) {
        return accountRepository.findByIdAndUserIdAndActiveTrue(accountId, userId);
    }
    
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumberAndActiveTrue(accountNumber);
    }
    
    @Transactional
    public Account createAccount(Long userId, Account.AccountType accountType) {
        Account account = new Account();
        account.setUserId(userId);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setActive(true);
        
        return accountRepository.save(account);
    }
    
    @Transactional
    public Account updateBalance(Long accountId, BigDecimal newBalance) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
    
    public BigDecimal getAccountBalance(Long accountId, Long userId) {
        return getAccountById(accountId, userId)
            .map(Account::getBalance)
            .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }
    
    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        
        // Gera um número de conta de 10 dígitos
        for (int i = 0; i < 10; i++) {
            accountNumber.append(random.nextInt(10));
        }
        
        // Verifica se já existe, se sim, gera outro
        String number = accountNumber.toString();
        while (accountRepository.findByAccountNumberAndActiveTrue(number).isPresent()) {
            accountNumber = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                accountNumber.append(random.nextInt(10));
            }
            number = accountNumber.toString();
        }
        
        return number;
    }
}
