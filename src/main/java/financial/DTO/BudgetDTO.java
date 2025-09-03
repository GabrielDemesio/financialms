package financial.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.YearMonth;

public record BudgetDTO(
        @NotNull Long categoryId,
        @NotNull YearMonth month,
        @NotNull @DecimalMin("0.00") BigDecimal amount
) {}


