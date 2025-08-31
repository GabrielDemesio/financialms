package financial.repository;

import financial.entities.TransactionEntity;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo {
    @Query("""
     SELECT new com.app.api.dto.CategoryTotalDTO(t.category.id, t.category.name,
              SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END))
     FROM Transaction t
     WHERE t.userId = :userId AND t.occurredAt >= :start AND t.occurredAt < :end
     GROUP BY t.category.id, t.category.name
     ORDER BY 3 DESC
  """)
    List<CategoryTotalDTO> totalsByCategory(Long userId, LocalDate start, LocalDate end);

    List<TransactionEntity> findByUserIdAndOccuredAtBetween(Long userId, LocalDate start, LocalDate end);

}
