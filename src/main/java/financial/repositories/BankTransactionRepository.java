package financial.repositories;

import financial.entities.BankTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    
    Page<BankTransaction> findByAccountIdOrderByOccurredAtDesc(Long accountId, Pageable pageable);
    
    List<BankTransaction> findByAccountIdAndOccurredAtBetweenOrderByOccurredAtDesc(
        Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT bt FROM BankTransaction bt WHERE bt.accountId = :accountId " +
           "AND bt.occurredAt >= :startDate AND bt.occurredAt <= :endDate " +
           "ORDER BY bt.occurredAt DESC")
    List<BankTransaction> findAccountTransactionsByPeriod(
        @Param("accountId") Long accountId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
}
