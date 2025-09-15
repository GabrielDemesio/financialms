package financial.controllers;

import financial.DTO.InsightDTO;
import financial.services.InsightService;
import financial.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightService insightService;

    @GetMapping
    public List<InsightDTO> insightDTOS(Authentication authentication,
                                        @RequestParam String month){
        var yearMonth = YearMonth.parse(month);
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return insightService.generate(userId, yearMonth);
    }
}
