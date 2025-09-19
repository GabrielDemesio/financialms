package financial.controllers;

import financial.entities.Expense;
import financial.services.ExpenseService;
import financial.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    @GetMapping
    public Page<Expense> getUserExpenses(Authentication authentication, Pageable pageable) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return expenseService.getUserExpenses(userId, pageable);
    }

    @GetMapping("/month/{yearMonth}")
    public List<Expense> getExpensesByMonth(Authentication authentication,
                                           @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return expenseService.getUserExpensesByPeriod(userId, startDate, endDate);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense createExpense(Authentication authentication,
                                @Valid @RequestBody CreateExpenseDTO expenseDTO) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return expenseService.createExpense(
            userId,
            expenseDTO.categoryId(),
            expenseDTO.amount(),
            expenseDTO.description(),
            expenseDTO.merchant(),
            expenseDTO.occurredAt()
        );
    }
    
    @GetMapping("/{expenseId}")
    public Expense getExpense(Authentication authentication, @PathVariable Long expenseId) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return expenseService.getExpenseById(expenseId, userId)
                .orElseThrow(() -> new RuntimeException("Gasto não encontrado"));
    }
    
    @PutMapping("/{expenseId}")
    public Expense updateExpense(Authentication authentication,
                                @PathVariable Long expenseId,
                                @Valid @RequestBody CreateExpenseDTO expenseDTO) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return expenseService.updateExpense(
            expenseId,
            userId,
            expenseDTO.categoryId(),
            expenseDTO.amount(),
            expenseDTO.description(),
            expenseDTO.merchant(),
            expenseDTO.occurredAt()
        );
    }

    @DeleteMapping("/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(Authentication authentication, @PathVariable Long expenseId) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        expenseService.deleteExpense(expenseId, userId);
    }
    
    public record CreateExpenseDTO(
        @jakarta.validation.constraints.NotNull(message = "Categoria é obrigatória")
        Long categoryId,

        @jakarta.validation.constraints.NotNull(message = "Valor é obrigatório")
        @jakarta.validation.constraints.DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal amount,

        @jakarta.validation.constraints.NotBlank(message = "Descrição é obrigatória")
        @jakarta.validation.constraints.Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
        String description,

        @jakarta.validation.constraints.Size(max = 100, message = "Merchant deve ter no máximo 100 caracteres")
        String merchant,

        LocalDate occurredAt
    ) {}
}
