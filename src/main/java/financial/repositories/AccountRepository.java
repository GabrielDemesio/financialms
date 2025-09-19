package financial.repositories;

import financial.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByUserIdAndActiveTrue(Long userId);
    
    Optional<Account> findByAccountNumberAndActiveTrue(String accountNumber);
    
    Optional<Account> findByIdAndUserIdAndActiveTrue(Long id, Long userId);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.userId = :userId AND a.active = true")
    long countActiveAccountsByUserId(@Param("userId") Long userId);
}
