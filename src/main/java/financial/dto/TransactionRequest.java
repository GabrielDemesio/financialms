package financial.dto;

import financial.domain.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransactionRequest {
    @NotNull
    public TransactionType type;

    @NotNull
    @DecimalMin("0.00")
    public BigDecimal amount;

    public String description;
    public String productName;
    public Integer installments;
}
