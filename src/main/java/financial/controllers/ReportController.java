package financial.controllers;


import financial.DTO.MonthlySummary;
import financial.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/monthly-summary")
    public MonthlySummary summary(@RequestHeader(value = "X-User-Id",
    required = false) Long userId,
                                  @RequestParam String month){
        var yearMonth = YearMonth.parse(month);
        return reportService.monthlySummary(UserHeader.getOrDefault(userId), yearMonth);
    }
}
