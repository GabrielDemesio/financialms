package financial.services;


import financial.DTO.TransactionResponse;
import financial.entities.TransactionEntity;
import financial.exceptions.NotFoundException;
import financial.repositories.TransactionRepository;
import financial.DTO.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public List<TransactionResponse> listByMonth(Long userId, java.time.YearMonth month) {
        var start = financial.utils.YearMonthUtils.startOf(month);
        var end = financial.utils.YearMonthUtils.startOfNext(month);
        return transactionRepository.findByUserIdAndOccurredAtBetween(userId, start, end).stream().map(t -> new TransactionResponse(
                t.getId(), t.getCategoryEntity().getId(), t.getCategoryEntity().getName(), t.getOccuredAt(),
                t.getDescription(), t.getAmount(), t.getTransactionType(), t.isRecurring(), t.getMerchant())).toList();
    }
    public TransactionResponse create(Long userId, TransactionDTO transactionDTO) {
        var categoryEntity = categoryService.getOwnedCategory(userId, transactionDTO.categoryId());
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setUserId(userId);
        transactionEntity.setCategoryEntity(categoryEntity);
        transactionEntity.setOccuredAt(transactionDTO.occurredAt());
        transactionEntity.setDescription(transactionDTO.description());
        transactionEntity.setAmount(transactionDTO.amount());
        transactionEntity.setTransactionType(transactionDTO.type());
        transactionEntity.setRecurring(transactionDTO.recurring());
        transactionEntity.setMerchant(transactionDTO.merchant());
        transactionEntity.setCategoryEntity(categoryEntity);

        transactionEntity = transactionRepository.save(transactionEntity);
        return new TransactionResponse(
                transactionEntity.getId(), categoryEntity.getId(), categoryEntity.getName(), transactionEntity.getOccuredAt(),
                transactionEntity.getDescription(), transactionEntity.getAmount(), transactionEntity.getTransactionType(), transactionEntity.isRecurring(), transactionEntity.getMerchant()
        );
    }
    public void delete(Long userId, Long id) {
        var transactionEntity = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction not found"));
        if (!transactionEntity.getUserId().equals(userId)) throw new NotFoundException("Transaction not found");
        transactionRepository.delete(transactionEntity);
    }
}
