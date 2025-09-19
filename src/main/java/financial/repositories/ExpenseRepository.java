package financial.repositories;

import financial.entities.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    Page<Expense> findByUserIdOrderByOccurredAtDesc(Long userId, Pageable pageable);
    
    List<Expense> findByUserIdAndOccurredAtBetweenOrderByOccurredAtDesc(
        Long userId, LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByUserIdAndCategoryIdAndOccurredAtBetween(
        Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId " +
           "AND e.occurredAt >= :startDate AND e.occurredAt <= :endDate")
    BigDecimal sumExpensesByUserAndPeriod(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.userId = :userId " +
           "AND e.categoryId = :categoryId " +
           "AND e.occurredAt >= :startDate AND e.occurredAt <= :endDate")
    BigDecimal sumExpensesByUserCategoryAndPeriod(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}
