package financial.DTO;

import financial.entities.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        Long categoryId,
        String categoryName,
        LocalDate occurredAt,
        String description,
        BigDecimal amount,
        TransactionType type,
        boolean recurring,
        String merchant
) {}
