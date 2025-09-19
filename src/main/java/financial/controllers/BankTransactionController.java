package financial.controllers;

import financial.entities.BankTransaction;
import financial.services.BankTransactionService;
import financial.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/bank-transactions")
@RequiredArgsConstructor
public class BankTransactionController {
    
    private final BankTransactionService bankTransactionService;
    
    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public BankTransaction deposit(Authentication authentication, 
                                  @Valid @RequestBody DepositDTO depositDTO) {
        return bankTransactionService.deposit(
            depositDTO.accountId(), 
            depositDTO.amount(), 
            depositDTO.description()
        );
    }
    
    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public BankTransaction withdraw(Authentication authentication, 
                                   @Valid @RequestBody WithdrawDTO withdrawDTO) {
        return bankTransactionService.withdraw(
            withdrawDTO.accountId(), 
            withdrawDTO.amount(), 
            withdrawDTO.description()
        );
    }
    
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public void transfer(Authentication authentication, 
                        @Valid @RequestBody TransferDTO transferDTO) {
        bankTransactionService.transfer(
            transferDTO.fromAccountId(), 
            transferDTO.toAccountId(), 
            transferDTO.amount(), 
            transferDTO.description()
        );
    }
    
    @GetMapping("/account/{accountId}")
    public Page<BankTransaction> getAccountTransactions(
            Authentication authentication,
            @PathVariable Long accountId,
            Pageable pageable) {
        return bankTransactionService.getAccountTransactions(accountId, pageable);
    }
    
    @GetMapping("/account/{accountId}/period")
    public List<BankTransaction> getAccountTransactionsByPeriod(
            Authentication authentication,
            @PathVariable Long accountId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return bankTransactionService.getAccountTransactionsByPeriod(accountId, startDate, endDate);
    }
    
    public record DepositDTO(
        @jakarta.validation.constraints.NotNull Long accountId,
        @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.DecimalMin("0.01") BigDecimal amount,
        String description
    ) {}
    
    public record WithdrawDTO(
        @jakarta.validation.constraints.NotNull Long accountId,
        @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.DecimalMin("0.01") BigDecimal amount,
        String description
    ) {}
    
    public record TransferDTO(
        @jakarta.validation.constraints.NotNull Long fromAccountId,
        @jakarta.validation.constraints.NotNull Long toAccountId,
        @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.DecimalMin("0.01") BigDecimal amount,
        String description
    ) {}
}
