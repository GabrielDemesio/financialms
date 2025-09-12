package financial.controller;

import financial.domain.Transaction;
import financial.domain.User;
import financial.dto.TransactionRequest;
import financial.repository.TransactionUserRepository;
import financial.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactionsUser")
public class TransactionUserController {
    private final TransactionUserRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionUserController(TransactionUserRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TransactionRequest body, Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).body("Não autenticado.");
        }
        Long userId = (Long) auth.getPrincipal();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("Usuário não encontrado.");

        Transaction t = new Transaction();
        t.setUser(user);
        t.setType(body.type);
        t.setAmount(body.amount);
        t.setDescription(body.description);
        t.setProductName(body.productName);
        t.setInstallments(body.installments);
        t = transactionRepository.save(t);
        return ResponseEntity.ok(t);
    }

    @GetMapping
    public ResponseEntity<?> list(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).body("Não autenticado.");
        }
        Long userId = (Long) auth.getPrincipal();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("Usuário não encontrado.");
        List<Transaction> txs = transactionRepository.findByUserOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(txs);
    }
}
