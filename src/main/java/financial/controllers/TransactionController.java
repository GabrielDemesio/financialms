package financial.controllers;


import financial.DTO.TransactionDTO;
import financial.DTO.TransactionResponse;
import financial.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionResponse> list(@RequestHeader(value = "X-User-Id",
     required = false) Long userId,
                                          @RequestParam String month){
        var yearMonth = YearMonth.parse(month);
        return transactionService.listByMonth(UserHeader.getOrDefault(userId), yearMonth);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@RequestHeader(value = "X-User-Id",
    required = false)Long userId,
                                      @Valid @RequestBody TransactionDTO transactionDTO){
        return transactionService.create(UserHeader.getOrDefault(userId), transactionDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                       @PathVariable Long id){
        transactionService.delete(UserHeader.getOrDefault(userId), id);
    }
}
