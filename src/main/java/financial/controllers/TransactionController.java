package financial.controllers;

import financial.DTO.TransactionDTO;
import financial.DTO.TransactionResponse;
import financial.services.TransactionService;
import financial.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionResponse> list(Authentication authentication,
                                          @RequestParam String month){
        var yearMonth = YearMonth.parse(month);
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return transactionService.listByMonth(userId, yearMonth);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(Authentication authentication,
                                      @Valid @RequestBody TransactionDTO transactionDTO){
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return transactionService.create(userId, transactionDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication,
                       @PathVariable Long id){
        Long userId = AuthUtils.getCurrentUserId(authentication);
        transactionService.delete(userId, id);
    }
}
