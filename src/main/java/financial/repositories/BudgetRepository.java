package financial.repositories;

import financial.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BudgetRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByUserIdAndOccuredAtBetween(Long userId, LocalDate start, LocalDate end);
    @Query("""
        SELECT t.category.id, t.category.name,
        SUM(CASE WHEN t.type = com.fincoach.domain.entity.enums.TransactionType.EXPENSE THEN t.amount ELSE 0 END)
        FROM Transaction t
        WHERE t.userId = :userId AND t.occurredAt >= :start AND t.occurredAt < :end
        GROUP BY t.category.id, t.category.name
        ORDER BY 3 DESC
    """)
    List<Object[]> totalsByCategory(@Param("userId") Long userId,
                                    @Param("start") LocalDate start,
                                    @Param("end") LocalDate end);
    @Query("""
        SELECT COALESCE(SUM(CASE WHEN t.type = com.fincoach.domain.entity.enums.TransactionType.INCOME THEN t.amount ELSE 0 END),0),
        COALESCE(SUM(CASE WHEN t.type = com.fincoach.domain.entity.enums.TransactionType.EXPENSE THEN t.amount ELSE 0 END),0)
        FROM Transaction t
        WHERE t.userId = :userId AND t.occurredAt >= :start AND t.occurredAt < :end
    """)
    Object[] incomeExpenseTotals(@Param("userId") Long userId,
                                 @Param("start") LocalDate start,
                                 @Param("end") LocalDate end);

    long countByUserIdAndOcurredAtBetweenAndRecurringTrue(Long userId, LocalDate start, LocalDate end);
}
