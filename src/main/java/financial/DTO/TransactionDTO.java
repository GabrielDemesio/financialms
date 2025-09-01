package financial.DTO;

import financial.entities.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDTO(
        @NotNull Long categoryId,
        @NotNull LocalDate occurredAt,
        @NotNull @DecimalMin("0.00") BigDecimal amount,
        @NotNull TransactionType type,
        String description,
        boolean recurring,
        String merchant
) {}


public record TransactionResponse(
        Long id, Long categoryId, String categoryName, LocalDate occurredAt,
        String description, BigDecimal amount, TransactionType type, boolean recurring, String merchant
) {}
