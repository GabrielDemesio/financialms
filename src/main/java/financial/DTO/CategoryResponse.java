package financial.DTO;

import financial.entities.enums.CategoryKind;

public record CategoryResponse(
        Long id,
        String name,
        CategoryKind kind,
        String color
) {}
