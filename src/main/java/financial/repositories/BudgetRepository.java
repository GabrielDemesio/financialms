package financial.repositories;

import financial.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    List<Budget> findByUserIdAndYearMonth(Long userId, LocalDate yearMonth);
    
    List<Budget> findByUserId(Long userId);
    
    Optional<Budget> findByUserIdAndCategoryIdAndYearMonth(Long userId, Long categoryId, LocalDate yearMonth);
    
    @Query("SELECT b FROM Budget b WHERE b.userId = :userId " +
           "AND b.yearMonth >= :startDate AND b.yearMonth <= :endDate")
    List<Budget> findByUserIdAndYearMonthBetween(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}
