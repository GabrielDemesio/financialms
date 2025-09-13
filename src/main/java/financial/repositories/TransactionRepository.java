package financial.repositories;

import financial.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByUserIdAndOccurredAtBetween(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT t.categoryEntity.id, t.categoryEntity.name, " +
            "SUM(CASE WHEN t.transactionType = financial.entities.enums.TransactionType.EXPENSE THEN t.amount ELSE 0 END) " +
            "FROM TransactionEntity t " +
            "WHERE t.userId = :userId AND t.occurredAt >= :start AND t.occurredAt < :end " +
            "GROUP BY t.categoryEntity.id, t.categoryEntity.name " +
            "ORDER BY 3 DESC")
    List<Object[]> totalsByCategory(@Param("userId") Long userId,
                                    @Param("start") LocalDate start,
                                    @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(CASE WHEN t.transactionType = financial.entities.enums.TransactionType.INCOME THEN t.amount ELSE 0 END),0), " +
            "COALESCE(SUM(CASE WHEN t.transactionType = financial.entities.enums.TransactionType.EXPENSE THEN t.amount ELSE 0 END),0) " +
            "FROM TransactionEntity t " +
            "WHERE t.userId = :userId AND t.occurredAt >= :start AND t.occurredAt < :end")
    Object[] incomeExpenseTotals(@Param("userId") Long userId,
                                 @Param("start") LocalDate start,
                                 @Param("end") LocalDate end);

    @Query(value = """
    SELECT COUNT(*)
    FROM transactions t
    WHERE t.user_id = :userId
      AND t.occurred_at >= :start
      AND t.occurred_at < :end
      AND t.is_recurring = TRUE
    """, nativeQuery = true)
    long countByUserIdAndOccurredAtBetweenAndRecurringTrue(
            @Param("userId") Long userId,
            @Param("start") java.time.LocalDate start,
            @Param("end")   java.time.LocalDate end
    );
}
