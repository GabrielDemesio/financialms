package financial.controllers;

import financial.DTO.MonthlySummary;
import financial.services.ReportService;
import financial.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/monthly-summary")
    public MonthlySummary summary(Authentication authentication,
                                  @RequestParam String month){
        var yearMonth = YearMonth.parse(month);
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return reportService.monthlySummary(userId, yearMonth);
    }
}
