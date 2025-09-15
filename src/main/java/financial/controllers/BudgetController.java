package financial.controllers;

import financial.DTO.BudgetDTO;
import financial.DTO.BudgetResponse;
import financial.services.BudgetService;
import financial.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @GetMapping
    public List<BudgetResponse> list(Authentication authentication,
                                     @RequestParam String month){
        var yearMonth = YearMonth.parse(month);
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return budgetService.list(userId, yearMonth);
    }

    @PostMapping
    public BudgetResponse upsert(Authentication authentication,
                                 @Valid @RequestBody BudgetDTO budgetDTO){
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return budgetService.upsert(userId, budgetDTO);
    }
}
