package financial.services;


import financial.DTO.CategoryTotal;
import financial.DTO.MonthlySummary;
import financial.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static financial.utils.YearMonthUtils.startOf;
import static financial.utils.YearMonthUtils.startOfNext;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;

    public MonthlySummary monthlySummary(Long userId, YearMonth yearMonth){
        var start = startOf(yearMonth); var end = startOfNext(yearMonth);

        Object[] inOut = transactionRepository.incomeExpenseTotals(userId, start, end);
        BigDecimal totalIncome = (BigDecimal) inOut[0];
        BigDecimal totalExpense = (BigDecimal) inOut[1];
        BigDecimal net = totalIncome.subtract(totalExpense);

        List<CategoryTotal> byCategory = new ArrayList<>();
        for (Object[] row : transactionRepository.totalsByCategory(userId, start, end)) {
            Long categoryId = ((Number) row[0]).longValue();
            String name =  (String) row[1];

            BigDecimal total = (BigDecimal) row[2];
            byCategory.add(new CategoryTotal(categoryId, name, total));
        }
        return new MonthlySummary(yearMonth.toString(), totalIncome, totalExpense, net, byCategory);
    }
}
