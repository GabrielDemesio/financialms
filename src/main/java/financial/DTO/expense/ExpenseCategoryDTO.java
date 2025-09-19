package financial.DTO.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExpenseCategoryDTO(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    String name,
    
    @NotBlank(message = "Cor é obrigatória")
    @Size(min = 4, max = 7, message = "Cor deve estar no formato #RRGGBB")
    String color
) {}
