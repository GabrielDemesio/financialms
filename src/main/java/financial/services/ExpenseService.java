package financial.services;

import financial.entities.Expense;
import financial.repositories.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    
    public Page<Expense> getUserExpenses(Long userId, Pageable pageable) {
        return expenseRepository.findByUserIdOrderByOccurredAtDesc(userId, pageable);
    }
    
    public List<Expense> getUserExpensesByPeriod(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndOccurredAtBetweenOrderByOccurredAtDesc(userId, startDate, endDate);
    }
    
    public List<Expense> getCategoryExpensesByPeriod(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndCategoryIdAndOccurredAtBetween(userId, categoryId, startDate, endDate);
    }
    
    public BigDecimal getTotalExpensesByPeriod(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = expenseRepository.sumExpensesByUserAndPeriod(userId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getCategoryExpensesTotalByPeriod(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = expenseRepository.sumExpensesByUserCategoryAndPeriod(userId, categoryId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional
    public Expense createExpense(Long userId, Long categoryId, BigDecimal amount, String description, String merchant, LocalDate occurredAt) {
        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setCategoryId(categoryId);
        expense.setAmount(amount);
        expense.setDescription(description);
        expense.setMerchant(merchant);
        expense.setOccurredAt(occurredAt != null ? occurredAt : LocalDate.now());
        
        return expenseRepository.save(expense);
    }
    
    @Transactional
    public Expense updateExpense(Long expenseId, Long userId, Long categoryId, BigDecimal amount, String description, String merchant, LocalDate occurredAt) {
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));
        
        if (!expense.getUserId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }
        
        expense.setCategoryId(categoryId);
        expense.setAmount(amount);
        expense.setDescription(description);
        expense.setMerchant(merchant);
        expense.setOccurredAt(occurredAt);
        
        return expenseRepository.save(expense);
    }
    
    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));
        
        if (!expense.getUserId().equals(userId)) {
            throw new RuntimeException("Acesso negado");
        }
        
        expenseRepository.delete(expense);
    }
    
    public Optional<Expense> getExpenseById(Long expenseId, Long userId) {
        return expenseRepository.findById(expenseId)
            .filter(expense -> expense.getUserId().equals(userId));
    }
}
