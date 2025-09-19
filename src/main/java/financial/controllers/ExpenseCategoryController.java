package financial.controllers;

import financial.DTO.expense.ExpenseCategoryDTO;
import financial.entities.ExpenseCategory;
import financial.services.ExpenseCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense-categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    @GetMapping
    public ResponseEntity<List<ExpenseCategory>> getAllCategories(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ExpenseCategory> categories = expenseCategoryService.getUserCategories(userId);
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<ExpenseCategory> createCategory(
            @Valid @RequestBody ExpenseCategoryDTO categoryDTO,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        ExpenseCategory category = expenseCategoryService.createCategory(userId, categoryDTO.name(), categoryDTO.color());
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseCategory> getCategoryById(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        ExpenseCategory category = expenseCategoryService.getCategoryById(id, userId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseCategory> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseCategoryDTO categoryDTO,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        ExpenseCategory category = expenseCategoryService.updateCategory(id, userId, categoryDTO.name(), categoryDTO.color());
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        expenseCategoryService.deleteCategory(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof financial.entities.UserEntity) {
            return ((financial.entities.UserEntity) authentication.getPrincipal()).getId();
        }
        throw new RuntimeException("Usuário não autenticado");
    }
}
