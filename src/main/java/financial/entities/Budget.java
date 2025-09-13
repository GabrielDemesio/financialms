package financial.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budgets")
@Getter @Setter
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false) // <- snake_case, igual ao banco
    private Long userId;

    @Column(nullable = false)
    private LocalDate month;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // REMOVIDOS: transaction_type e is_recurring (não existem em budgets)
}
