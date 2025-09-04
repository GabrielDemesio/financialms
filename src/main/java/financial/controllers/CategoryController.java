package financial.controllers;


import financial.DTO.CategoryDTO;
import financial.DTO.CategoryResponse;
import financial.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> list(@RequestHeader(value = "X-User-Id",
    required = false) Long userId){
        return categoryService.list(UserHeader.getOrDefault(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@RequestHeader(value = "X-User-Id",
    required = false) Long userId,
                                   @Valid @RequestBody CategoryDTO categoryDTO){
        return categoryService.create(UserHeader.getOrDefault(userId), categoryDTO);
    }
}
