package financial.DTO;

import java.math.BigDecimal;
import java.util.List;

public record ReportDTO(String month,
                        BigDecimal totalIncome,
                        BigDecimal totalExpense,
                        BigDecimal net,
                        List<CategoryTotal> byCategory
) {}


public record CategoryTotal(Long categoryId, String name, BigDecimal total) {}