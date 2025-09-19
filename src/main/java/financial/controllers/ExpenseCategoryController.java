package financial.controllers;

import financial.entities.ExpenseCategory;
import financial.services.ExpenseCategoryService;
import financial.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public List<ExpenseCategory> getUserCategories(Authentication authentication) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return expenseCategoryService.getUserCategories(userId);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseCategory createCategory(Authentication authentication, 
                                         @Valid @RequestBody CreateExpenseCategoryDTO categoryDTO) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return expenseCategoryService.createCategory(userId, categoryDTO.name(), categoryDTO.color());
    }
    
    @GetMapping("/{categoryId}")
    public ExpenseCategory getCategory(Authentication authentication, @PathVariable Long categoryId) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return expenseCategoryService.getCategoryById(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }
    
    @PutMapping("/{categoryId}")
    public ExpenseCategory updateCategory(Authentication authentication, 
                                         @PathVariable Long categoryId,
                                         @Valid @RequestBody CreateExpenseCategoryDTO categoryDTO) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return expenseCategoryService.updateCategory(userId, categoryId, categoryDTO.name(), categoryDTO.color());
    }
    
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(Authentication authentication, @PathVariable Long categoryId) {
        Long userId = AuthUtils.getCurrentUserId(authentication);
        expenseCategoryService.deleteCategory(userId, categoryId);
    }
    
    public record CreateExpenseCategoryDTO(
        @jakarta.validation.constraints.NotBlank(message = "Nome é obrigatório")
        @jakarta.validation.constraints.Size(max = 80, message = "Nome deve ter no máximo 80 caracteres")
        String name,
        
        @jakarta.validation.constraints.Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato #RRGGBB")
        String color
    ) {}
}
