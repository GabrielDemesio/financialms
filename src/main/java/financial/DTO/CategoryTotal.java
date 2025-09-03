package financial.DTO;

import java.math.BigDecimal;

public record CategoryTotal(Long categoryId, String name, BigDecimal total) {}
