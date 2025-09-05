package financial.DTO;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummary(
        String month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal net,
        List<CategoryTotal> byCategory
) {}
