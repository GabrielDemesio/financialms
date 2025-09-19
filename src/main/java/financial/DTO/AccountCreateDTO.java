package financial.DTO;

import financial.entities.Account;
import jakarta.validation.constraints.NotNull;

public record AccountCreateDTO(
    @NotNull(message = "Tipo da conta é obrigatório")
    Account.AccountType accountType
) {}
