package financial.controllers;


import financial.DTO.CategoryDTO;
import financial.DTO.CategoryResponse;
import financial.services.CategoryService;
import financial.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> list(Authentication authentication){
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return categoryService.list(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(Authentication authentication,
                                   @Valid @RequestBody CategoryDTO categoryDTO){
        Long userId = AuthUtils.getCurrentUserId(authentication);
        return categoryService.create(userId, categoryDTO);
    }
}
