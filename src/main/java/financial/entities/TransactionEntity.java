package financial.entities;

import financial.entities.enums.TransactionType;
import jakarta.persistence.*;
import jdk.jfr.Category;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="transactions")
@Getter
@Setter
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

    @Column(name = "ocurred_at", nullable = false)
    private LocalDate occuredAt;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, precision =  12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType transactionType;

    @Column(nullable = false)
    private boolean isRecurring = false;

    @Column(length = 120)
    private String merchant;
}
