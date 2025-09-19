package financial.controllers;

import financial.DTO.AccountCreateDTO;
import financial.entities.Account;
import financial.services.AccountService;
import financial.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    
    private final AccountService accountService;
    
    @GetMapping
    public List<Account> getUserAccounts(Authentication authentication) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return accountService.getUserAccounts(userId);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(Authentication authentication, 
                                @Valid @RequestBody AccountCreateDTO accountCreateDTO) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return accountService.createAccount(userId, accountCreateDTO.accountType());
    }
    
    @GetMapping("/{accountId}")
    public Account getAccount(Authentication authentication, @PathVariable Long accountId) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return accountService.getAccountById(accountId, userId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }
    
    @GetMapping("/{accountId}/balance")
    public AccountBalanceResponse getAccountBalance(Authentication authentication, @PathVariable Long accountId) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        Account account = accountService.getAccountById(accountId, userId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        return new AccountBalanceResponse(account.getId(), account.getAccountNumber(), account.getBalance());
    }
    
    public record AccountBalanceResponse(Long id, String accountNumber, java.math.BigDecimal balance) {}
}
