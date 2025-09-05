package financial.services;

import financial.DTO.CategoryDTO;
import financial.DTO.CategoryResponse;
import financial.entities.CategoryEntity;
import financial.exceptions.NotFoundException;
import financial.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> list(Long userId) {
        return categoryRepository.findByUserIdOrderByNameAsc(userId).stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getCategoryKind(), c.getColor()))
                .toList();
    }
    public CategoryResponse create(Long userId, CategoryDTO dto) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setUserId(userId);
        categoryEntity.setName(dto.name());
        categoryEntity.setCategoryKind(dto.kind());
        categoryEntity.setColor(dto.color());
        categoryEntity = categoryRepository.save(categoryEntity);
        return new CategoryResponse(categoryEntity.getId(), categoryEntity.getName(), categoryEntity.getCategoryKind(), categoryEntity.getColor());
    }

    public CategoryEntity getOwnedCategory(Long userId, Long id) {
        return categoryRepository.findById(id)
                .filter(categoryEntity -> categoryEntity.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }
}
