package financial.controllers;

import financial.DTO.BudgetDTO;
import financial.entities.Budget;
import financial.services.BudgetService;
import financial.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @GetMapping
    public List<Budget> list(Authentication authentication,
                                     @RequestParam String month){
        var yearMonth = YearMonth.parse(month);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return budgetService.getUserBudgetsByMonth(userId, firstDayOfMonth);
    }

    @PostMapping
    public Budget createOrUpdate(Authentication authentication,
                                 @Valid @RequestBody BudgetDTO budgetDTO){
        Long userId = AuthUtils.getCurrentUserId(authentication);
        LocalDate yearMonth = budgetDTO.month().atDay(1);
        return budgetService.createOrUpdateBudget(userId, budgetDTO.categoryId(), yearMonth, budgetDTO.amount());
    }
}
