package financial.controllers;


import financial.DTO.InsightDTO;
import financial.DTO.TransactionResponse;
import financial.services.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightService insightService;

    @GetMapping
    public List<InsightDTO> insightDTOS(@RequestHeader (value = "X-User-Id", required = false) Long userId,
                                        @RequestParam String month){

        var yearMonth = YearMonth.parse(month);
        return insightService.generate(UserHeader.getOrDefault(userId), yearMonth);
    }
}
