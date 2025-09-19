package financial.repositories;

import financial.entities.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    
    List<ExpenseCategory> findByUserIdAndActiveTrue(Long userId);
    
    Optional<ExpenseCategory> findByIdAndUserIdAndActiveTrue(Long id, Long userId);
    
    Optional<ExpenseCategory> findByUserIdAndNameAndActiveTrue(Long userId, String name);
    
    boolean existsByUserIdAndNameAndActiveTrue(Long userId, String name);
}
