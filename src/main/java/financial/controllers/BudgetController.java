package financial.controllers;


import financial.DTO.BudgetDTO;
import financial.DTO.BudgetResponse;
import financial.entities.Budget;
import financial.services.BudgetService;
import financial.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
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
    public List<BudgetResponse> list(@RequestHeader(value = "X-User-Id",
    required = true) Long userId,
                                     @RequestParam String month){
        var yearMonth = YearMonth.parse(month);
        return budgetService.list(UserHeader.getOrDefault(userId), yearMonth);
    }
    @PostMapping
    public BudgetResponse upsert(@RequestHeader(value = "X-User-Id",
    required = false) Long userId,
                                 @Valid @RequestBody BudgetDTO budgetDTO){
        return budgetService.upsert(UserHeader.getOrDefault(userId), budgetDTO);
    }
}
