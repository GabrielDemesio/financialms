package financial.services;


import financial.DTO.InsightDTO;
import financial.repositories.BudgetRepository;
import financial.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static financial.utils.YearMonthUtils.startOf;
import static financial.utils.YearMonthUtils.startOfNext;

@Service
@RequiredArgsConstructor
public class InsightService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public List<InsightDTO> generate(Long userId, YearMonth yearMonth) {
        var start = startOf(yearMonth); var end = startOfNext(yearMonth);
        var budgets = budgetRepository.findByUserIdAndMonth(userId, start);

        Map<Long, BigDecimal> spentByCategory = new HashMap<>();
        for (Object[] row : transactionRepository.totalsByCategory(userId, start, end)) {
            Long categoryId = ((Number) row[0]).longValue();
            BigDecimal total = (BigDecimal) row[2];

            spentByCategory.put(categoryId, total == null ? BigDecimal.ZERO : total);
        }
        List<InsightDTO> out = new ArrayList<>();

        budgets.forEach(budget -> {
            var spent = spentByCategory.getOrDefault(budget.getCategory().getId(), BigDecimal.ZERO);
            if(spent.compareTo(budget.getAmount()) > 0){
                var diff = spent.subtract(budget.getAmount());
                out.add(new InsightDTO(
                        "Estouro em" + budget.getCategory().getName(),
                        "Você excedeu o orçamento da categoria em R$ " + diff + ". Considere reduzir o consumo ou ajustar o teto.",
                        "warning"
                ));
            }
        });

        long recurringCount = transactionRepository.countByUserIdAndOccuredAtBetweenAndRecurringTrue(userId, start, end);
            if (recurringCount > 0) {
                out.add(new InsightDTO(
                        "Assinaturas recorrentes",
                        "Você tem " + recurringCount + " lançamentos recorrentes este mês. Revise oque não usa.",
                        "info"
                ));
            }
            Object[] inOut = transactionRepository.incomeExpenseTotals(userId, start, end);
            var income = (java.math.BigDecimal) inOut[0];
            var expense = (java.math.BigDecimal) inOut[1];
            if(income.compareTo(BigDecimal.ZERO) > 0) {
                var ratio = expense.divide(income, 2, java.math.RoundingMode.HALF_UP);
                if(ratio.compareTo(new BigDecimal("0.70")) > 0) {
                    out.add(new InsightDTO(
                            "Despesas altas vs renda",
                            "Seus gastos representam mais de 70% da renda no mês. Tente reduzir 10% nas categorias variáveis",
                            "warning"
                    ));
                }
        }
            return out;
    }
}
