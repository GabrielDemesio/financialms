package financial.services;

import financial.entities.Budget;
import financial.repositories.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final ExpenseService expenseService;
    
    public List<Budget> getUserBudgets(Long userId) {
        return budgetRepository.findByUserId(userId);
    }
    
    public List<Budget> getUserBudgetsByMonth(Long userId, LocalDate yearMonth) {
        // Normaliza para o primeiro dia do mês
        LocalDate firstDayOfMonth = yearMonth.withDayOfMonth(1);
        return budgetRepository.findByUserIdAndYearMonth(userId, firstDayOfMonth);
    }
    
    public Optional<Budget> getBudgetByCategoryAndMonth(Long userId, Long categoryId, LocalDate yearMonth) {
        LocalDate firstDayOfMonth = yearMonth.withDayOfMonth(1);
        return budgetRepository.findByUserIdAndCategoryIdAndYearMonth(userId, categoryId, firstDayOfMonth);
    }
    
    @Transactional
    public Budget createOrUpdateBudget(Long userId, Long categoryId, LocalDate yearMonth, BigDecimal limitAmount) {
        LocalDate firstDayOfMonth = yearMonth.withDayOfMonth(1);
        
        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndCategoryIdAndYearMonth(userId, categoryId, firstDayOfMonth);
        
        Budget budget;
        if (existingBudget.isPresent()) {
            budget = existingBudget.get();
            budget.setLimitAmount(limitAmount);
        } else {
            budget = new Budget();
            budget.setUserId(userId);
            budget.setCategoryId(categoryId);
            budget.setYearMonth(firstDayOfMonth);
            budget.setLimitAmount(limitAmount);
        }
        
        return budgetRepository.save(budget);
    }
    
    @Transactional
    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        
        if (!budget.getUserId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }
        
        budgetRepository.delete(budget);
    }
    
    public BigDecimal getBudgetUsage(Long userId, Long categoryId, LocalDate yearMonth) {
        LocalDate startOfMonth = yearMonth.withDayOfMonth(1);
        LocalDate endOfMonth = yearMonth.withDayOfMonth(yearMonth.lengthOfMonth());
        
        return expenseService.getCategoryExpensesTotalByPeriod(userId, categoryId, startOfMonth, endOfMonth);
    }
    
    public BigDecimal getBudgetRemaining(Long userId, Long categoryId, LocalDate yearMonth) {
        Optional<Budget> budget = getBudgetByCategoryAndMonth(userId, categoryId, yearMonth);
        if (budget.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal used = getBudgetUsage(userId, categoryId, yearMonth);
        BigDecimal remaining = budget.get().getLimitAmount().subtract(used);
        
        return remaining.max(BigDecimal.ZERO);
    }
    
    public boolean isBudgetExceeded(Long userId, Long categoryId, LocalDate yearMonth) {
        Optional<Budget> budget = getBudgetByCategoryAndMonth(userId, categoryId, yearMonth);
        if (budget.isEmpty()) {
            return false;
        }
        
        BigDecimal used = getBudgetUsage(userId, categoryId, yearMonth);
        return used.compareTo(budget.get().getLimitAmount()) > 0;
    }
}
