package financial.services;

import financial.entities.ExpenseCategory;
import financial.repositories.ExpenseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService {
    
    private final ExpenseCategoryRepository expenseCategoryRepository;
    
    public List<ExpenseCategory> getUserCategories(Long userId) {
        return expenseCategoryRepository.findByUserIdAndActiveTrue(userId);
    }
    
    public Optional<ExpenseCategory> getCategoryById(Long categoryId, Long userId) {
        return expenseCategoryRepository.findByIdAndUserIdAndActiveTrue(categoryId, userId);
    }
    
    @Transactional
    public ExpenseCategory createCategory(Long userId, String name, String color) {
        if (expenseCategoryRepository.existsByUserIdAndNameAndActiveTrue(userId, name)) {
            throw new RuntimeException("Categoria com este nome já existe");
        }
        
        ExpenseCategory category = new ExpenseCategory();
        category.setUserId(userId);
        category.setName(name);
        category.setColor(color);
        category.setActive(true);
        
        return expenseCategoryRepository.save(category);
    }
    
    @Transactional
    public ExpenseCategory updateCategory(Long userId, Long categoryId, String name, String color) {
        ExpenseCategory category = getCategoryById(categoryId, userId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        
        // Verifica se o novo nome já existe (exceto para a própria categoria)
        if (!category.getName().equals(name) && 
            expenseCategoryRepository.existsByUserIdAndNameAndActiveTrue(userId, name)) {
            throw new RuntimeException("Categoria com este nome já existe");
        }
        
        category.setName(name);
        category.setColor(color);
        
        return expenseCategoryRepository.save(category);
    }
    
    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        ExpenseCategory category = getCategoryById(categoryId, userId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        
        category.setActive(false);
        expenseCategoryRepository.save(category);
    }
}
