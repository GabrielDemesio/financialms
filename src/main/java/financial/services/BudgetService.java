package financial.services;


import financial.DTO.BudgetDTO;
import financial.DTO.BudgetResponse;
import financial.entities.Budget;
import financial.entities.CategoryEntity;
import financial.repositories.BudgetRepository;
import financial.utils.YearMonthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryService categoryService;

    public List<BudgetResponse> list(Long userId, YearMonth yearMonth) {
        var list = budgetRepository.findByUserIdAndMonth(userId, YearMonthUtils.startOf(yearMonth));
        return list.stream().map(b -> new BudgetResponse(
                b.getId(), b.getCategory().getId(), b.getCategory().getName(), b.getMonth().toString(), b.getAmount())).toList();
    }
    public BudgetResponse upsert(Long userId, BudgetDTO budgetDTO) {
        CategoryEntity categoryEntity = categoryService.getOwnedCategory(userId, budgetDTO.categoryId());
        var monthDate = YearMonthUtils.startOf(budgetDTO.month());

        var existing = budgetRepository.findByUserIdAndMonth(userId, monthDate).stream()
                .filter(b -> b.getCategory().getId().equals(budgetDTO.categoryId()))
                .findFirst()
                .orElse(null);

        Budget budget = (existing != null) ? existing : new Budget();
        budget.setUserId(userId);
        budget.setCategory(categoryEntity);
        budget.setMonth(monthDate);
        budget.setAmount(budgetDTO.amount());

        budget =  budgetRepository.save(budget);

        return new BudgetResponse(budget.getId(), categoryEntity.getId(), categoryEntity.getName(), budget.getMonth().toString(), budget.getAmount());
    }
}
