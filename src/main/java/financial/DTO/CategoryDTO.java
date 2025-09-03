package financial.DTO;

import financial.entities.enums.CategoryKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryDTO (
    @NotBlank
    String name,
    @NotNull
    CategoryKind kind,

    String color
) {}





