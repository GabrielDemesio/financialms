package financial.entities;

import financial.entities.enums.CategoryKind;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter @Setter
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_kind", nullable = false, length = 20) // <- coluna correta
    private CategoryKind categoryKind;

    @Column(length = 7)
    private String color;
}
