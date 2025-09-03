package financial.DTO;

import java.math.BigDecimal;

public record BudgetResponse(
        Long id, Long categoryId, String categoryName, String month, BigDecimal amount
) {}
