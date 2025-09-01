Below is a complete **Spring Boot 3 + MySQL** backend MVP for your gastos mensais app, ready to run locally. It includes:

* Flyway migrations (schema V1)
* Entities, Repositories, Services, Controllers
* DTOs + Validation
* Global exception handler
* Simple "current user" approach via `X-User-Id` (default = 1) – sem autenticação por enquanto
* Monthly summary & insights endpoints

> Estrutura do projeto

```
fincoach-backend/
├─ pom.xml
├─ README.md
├─ src/main/java/com/fincoach/FincoachApplication.java
├─ src/main/java/com/fincoach/config/WebConfig.java
├─ src/main/java/com/fincoach/exception/ApiExceptionHandler.java
├─ src/main/java/com/fincoach/exception/NotFoundException.java
├─ src/main/java/com/fincoach/exception/BadRequestException.java
├─ src/main/java/com/fincoach/domain/entity/Category.java
├─ src/main/java/com/fincoach/domain/entity/Transaction.java
├─ src/main/java/com/fincoach/domain/entity/Budget.java
├─ src/main/java/com/fincoach/domain/entity/enums/CategoryKind.java
├─ src/main/java/com/fincoach/domain/entity/enums/TransactionType.java
├─ src/main/java/com/fincoach/domain/repo/CategoryRepository.java
├─ src/main/java/com/fincoach/domain/repo/TransactionRepository.java
├─ src/main/java/com/fincoach/domain/repo/BudgetRepository.java
├─ src/main/java/com/fincoach/dto/CategoryDTO.java
├─ src/main/java/com/fincoach/dto/TransactionDTO.java
├─ src/main/java/com/fincoach/dto/BudgetDTO.java
├─ src/main/java/com/fincoach/dto/ReportDTO.java
├─ src/main/java/com/fincoach/dto/InsightDTO.java
├─ src/main/java/com/fincoach/service/CategoryService.java
├─ src/main/java/com/fincoach/service/TransactionService.java
├─ src/main/java/com/fincoach/service/BudgetService.java
├─ src/main/java/com/fincoach/service/ReportService.java
├─ src/main/java/com/fincoach/service/InsightService.java
├─ src/main/java/com/fincoach/util/YearMonthUtils.java
├─ src/main/resources/application.yml
└─ src/main/resources/db/migration/V1__init.sql
```

---

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.fincoach</groupId>
  <artifactId>fincoach-backend</artifactId>
  <version>0.1.0</version>
  <name>fincoach-backend</name>

  <properties>
    <java.version>21</java.version>
    <spring-boot.version>3.3.2</spring-boot.version>
  </properties>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring-boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
      <groupId>com.mysql</groupId>
      <artifactId>mysql-connector-j</artifactId>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <configuration>
          <excludes>
            <exclude>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
            </exclude>
          </excludes>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

---

## application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fincoach?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate.format_sql: true
      hibernate.jdbc.time_zone: America/Sao_Paulo
  flyway:
    locations: classpath:db/migration
    baseline-on-migrate: true

server:
  port: 8080

logging:
  level:
    org.hibernate.SQL: warn
```

---

## Flyway – V1\_\_init.sql

```sql
-- Schema inicial
CREATE TABLE categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(80) NOT NULL,
  kind ENUM('FIXO','VARIAVEL','DIVIDA','INVESTIMENTO') NOT NULL,
  color VARCHAR(7) NULL
);
CREATE INDEX idx_categories_user ON categories(user_id);

CREATE TABLE budgets (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  month DATE NOT NULL,
  category_id BIGINT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  UNIQUE KEY uq_budget (user_id, month, category_id),
  CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE INDEX idx_budgets_user_month ON budgets(user_id, month);

CREATE TABLE transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  occurred_at DATE NOT NULL,
  description VARCHAR(255) NULL,
  amount DECIMAL(12,2) NOT NULL,
  type ENUM('EXPENSE','INCOME') NOT NULL DEFAULT 'EXPENSE',
  is_recurring BOOLEAN DEFAULT FALSE,
  merchant VARCHAR(120) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_tx_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE INDEX idx_tx_user_month ON transactions(user_id, occurred_at);
CREATE INDEX idx_tx_user_cat ON transactions(user_id, category_id, occurred_at);
```

---

## App principal

```java
// src/main/java/com/fincoach/FincoachApplication.java
package com.fincoach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class FincoachApplication {
  @PostConstruct
  void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
  }
  public static void main(String[] args) {
    SpringApplication.run(FincoachApplication.class, args);
  }
}
```

```java
// src/main/java/com/fincoach/config/WebConfig.java
package com.fincoach.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("http://localhost:4200", "http://127.0.0.1:4200")
      .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
      .allowedHeaders("*")
      .allowCredentials(true);
  }
}
```

---

## Exceptions

```java
// src/main/java/com/fincoach/exception/NotFoundException.java
package com.fincoach.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) { super(message); }
}
```

```java
// src/main/java/com/fincoach/exception/BadRequestException.java
package com.fincoach.exception;

public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) { super(message); }
}
```

```java
// src/main/java/com/fincoach/exception/ApiExceptionHandler.java
package com.fincoach.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> handleNotFound(NotFoundException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<?> handleBadRequest(BadRequestException ex){
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex){
    Map<String,String> errors = new HashMap<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      errors.put(fe.getField(), fe.getDefaultMessage());
    }
    return ResponseEntity.badRequest().body(Map.of("message","Validation failed","errors",errors));
  }
}
```

---

## Entities & Enums

```java
// src/main/java/com/fincoach/domain/entity/enums/CategoryKind.java
package com.fincoach.domain.entity.enums;
public enum CategoryKind { FIXO, VARIAVEL, DIVIDA, INVESTIMENTO }
```

```java
// src/main/java/com/fincoach/domain/entity/enums/TransactionType.java
package com.fincoach.domain.entity.enums;
public enum TransactionType { EXPENSE, INCOME }
```

```java
// src/main/java/com/fincoach/domain/entity/Category.java
package com.fincoach.domain.entity;

import com.fincoach.domain.entity.enums.CategoryKind;
import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;

@Entity @Table(name="categories")
@Getter @Setter
public class Category {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="user_id", nullable=false)
  private Long userId;

  @Column(nullable=false, length=80)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false, length=20)
  private CategoryKind kind;

  @Column(length=7)
  private String color; // ex: #FF9800
}
```

```java
// src/main/java/com/fincoach/domain/entity/Budget.java
package com.fincoach.domain.entity;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name="budgets")
@Getter @Setter
public class Budget {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="user_id", nullable=false)
  private Long userId;

  @Column(nullable=false)
  private LocalDate month; // dia 1 do mês

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="category_id", nullable=false)
  private Category category;

  @Column(nullable=false, precision=12, scale=2)
  private BigDecimal amount;
}
```

```java
// src/main/java/com/fincoach/domain/entity/Transaction.java
package com.fincoach.domain.entity;

import com.fincoach.domain.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name="transactions")
@Getter @Setter
public class Transaction {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="user_id", nullable=false)
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="category_id", nullable=false)
  private Category category;

  @Column(name="occurred_at", nullable=false)
  private LocalDate occurredAt;

  @Column(length=255)
  private String description;

  @Column(nullable=false, precision=12, scale=2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false, length=10)
  private TransactionType type; // EXPENSE | INCOME

  @Column(name="is_recurring", nullable=false)
  private boolean recurring = false;

  @Column(length=120)
  private String merchant;
}
```

---

## Repositories

```java
// src/main/java/com/fincoach/domain/repo/CategoryRepository.java
package com.fincoach.domain.repo;

import com.fincoach.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findByUserIdOrderByNameAsc(Long userId);
}
```

```java
// src/main/java/com/fincoach/domain/repo/BudgetRepository.java
package com.fincoach.domain.repo;

import com.fincoach.domain.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
  List<Budget> findByUserIdAndMonth(Long userId, LocalDate month);
}
```

```java
// src/main/java/com/fincoach/domain/repo/TransactionRepository.java
package com.fincoach.domain.repo;

import com.fincoach.domain.entity.Transaction;
import com.fincoach.domain.entity.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findByUserIdAndOccurredAtBetween(Long userId, LocalDate start, LocalDate end);

  @Query("""
    SELECT t.category.id, t.category.name,
           SUM(CASE WHEN t.type = com.fincoach.domain.entity.enums.TransactionType.EXPENSE THEN t.amount ELSE 0 END)
    FROM Transaction t
    WHERE t.userId = :userId AND t.occurredAt >= :start AND t.occurredAt < :end
    GROUP BY t.category.id, t.category.name
    ORDER BY 3 DESC
  """)
  List<Object[]> totalsByCategory(@Param("userId") Long userId,
                                  @Param("start") LocalDate start,
                                  @Param("end") LocalDate end);

  @Query("""
    SELECT COALESCE(SUM(CASE WHEN t.type = com.fincoach.domain.entity.enums.TransactionType.INCOME THEN t.amount ELSE 0 END),0),
           COALESCE(SUM(CASE WHEN t.type = com.fincoach.domain.entity.enums.TransactionType.EXPENSE THEN t.amount ELSE 0 END),0)
    FROM Transaction t
    WHERE t.userId = :userId AND t.occurredAt >= :start AND t.occurredAt < :end
  """)
  Object[] incomeExpenseTotals(@Param("userId") Long userId,
                               @Param("start") LocalDate start,
                               @Param("end") LocalDate end);

  long countByUserIdAndOccurredAtBetweenAndRecurringTrue(Long userId, LocalDate start, LocalDate end);
}
```

---

## DTOs

```java
// src/main/java/com/fincoach/dto/CategoryDTO.java
package com.fincoach.dto;

import com.fincoach.domain.entity.enums.CategoryKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
  @NotBlank String name,
  @NotNull CategoryKind kind,
  String color
) {}

public record CategoryResponse(
  Long id, String name, CategoryKind kind, String color
) {}
```

```java
// src/main/java/com/fincoach/dto/TransactionDTO.java
package com.fincoach.dto;

import com.fincoach.domain.entity.enums.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
  @NotNull Long categoryId,
  @NotNull LocalDate occurredAt,
  @NotNull @DecimalMin("0.00") BigDecimal amount,
  @NotNull TransactionType type,
  String description,
  boolean recurring,
  String merchant
) {}

public record TransactionResponse(
  Long id, Long categoryId, String categoryName, LocalDate occurredAt,
  String description, BigDecimal amount, TransactionType type, boolean recurring, String merchant
) {}
```

```java
// src/main/java/com/fincoach/dto/BudgetDTO.java
package com.fincoach.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.YearMonth;

public record BudgetRequest(
  @NotNull Long categoryId,
  @NotNull YearMonth month,
  @NotNull @DecimalMin("0.00") BigDecimal amount
) {}

public record BudgetResponse(
  Long id, Long categoryId, String categoryName, String month, BigDecimal amount
) {}
```

```java
// src/main/java/com/fincoach/dto/ReportDTO.java
package com.fincoach.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummary(
  String month,
  BigDecimal totalIncome,
  BigDecimal totalExpense,
  BigDecimal net,
  List<CategoryTotal> byCategory
) {}

public record CategoryTotal(Long categoryId, String name, BigDecimal total) {}
```

```java
// src/main/java/com/fincoach/dto/InsightDTO.java
package com.fincoach.dto;

public record Insight(String title, String message, String severity) {}
```

---

## Utils

```java
// src/main/java/com/fincoach/util/YearMonthUtils.java
package com.fincoach.util;

import java.time.LocalDate;
import java.time.YearMonth;

public final class YearMonthUtils {
  private YearMonthUtils() {}
  public static LocalDate startOf(YearMonth ym) { return ym.atDay(1); }
  public static LocalDate startOfNext(YearMonth ym) { return ym.plusMonths(1).atDay(1); }
}
```

---

## Services

```java
// src/main/java/com/fincoach/service/CategoryService.java
package com.fincoach.service;

import com.fincoach.domain.entity.Category;
import com.fincoach.domain.repo.CategoryRepository;
import com.fincoach.dto.CategoryRequest;
import com.fincoach.dto.CategoryResponse;
import com.fincoach.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository repo;

  public List<CategoryResponse> list(Long userId){
    return repo.findByUserIdOrderByNameAsc(userId).stream()
      .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getKind(), c.getColor()))
      .toList();
  }

  public CategoryResponse create(Long userId, CategoryRequest req){
    Category c = new Category();
    c.setUserId(userId);
    c.setName(req.name());
    c.setKind(req.kind());
    c.setColor(req.color());
    c = repo.save(c);
    return new CategoryResponse(c.getId(), c.getName(), c.getKind(), c.getColor());
  }

  public Category getOwned(Long userId, Long id){
    return repo.findById(id)
      .filter(c -> c.getUserId().equals(userId))
      .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
  }
}
```

```java
// src/main/java/com/fincoach/service/BudgetService.java
package com.fincoach.service;

import com.fincoach.domain.entity.Budget;
import com.fincoach.domain.entity.Category;
import com.fincoach.domain.repo.BudgetRepository;
import com.fincoach.dto.BudgetRequest;
import com.fincoach.dto.BudgetResponse;
import com.fincoach.util.YearMonthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service @RequiredArgsConstructor
public class BudgetService {
  private final BudgetRepository budgetRepo;
  private final CategoryService categoryService;

  public List<BudgetResponse> list(Long userId, YearMonth month){
    var list = budgetRepo.findByUserIdAndMonth(userId, YearMonthUtils.startOf(month));
    return list.stream().map(b -> new BudgetResponse(
      b.getId(), b.getCategory().getId(), b.getCategory().getName(), b.getMonth().toString(), b.getAmount()
    )).toList();
  }

  public BudgetResponse upsert(Long userId, BudgetRequest req){
    Category cat = categoryService.getOwned(userId, req.categoryId());
    var monthDate = YearMonthUtils.startOf(req.month());

    // procura existente
    var existing = budgetRepo.findByUserIdAndMonth(userId, monthDate).stream()
      .filter(b -> b.getCategory().getId().equals(cat.getId()))
      .findFirst()
      .orElse(null);

    Budget b = (existing != null) ? existing : new Budget();
    b.setUserId(userId);
    b.setCategory(cat);
    b.setMonth(monthDate);
    b.setAmount(req.amount());

    b = budgetRepo.save(b);
    return new BudgetResponse(b.getId(), cat.getId(), cat.getName(), b.getMonth().toString(), b.getAmount());
  }
}
```

```java
// src/main/java/com/fincoach/service/TransactionService.java
package com.fincoach.service;

import com.fincoach.domain.entity.Transaction;
import com.fincoach.domain.repo.TransactionRepository;
import com.fincoach.dto.TransactionRequest;
import com.fincoach.dto.TransactionResponse;
import com.fincoach.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class TransactionService {
  private final TransactionRepository repo;
  private final CategoryService categoryService;

  public List<TransactionResponse> listByMonth(Long userId, java.time.YearMonth ym){
    var start = com.fincoach.util.YearMonthUtils.startOf(ym);
    var end = com.fincoach.util.YearMonthUtils.startOfNext(ym);
    return repo.findByUserIdAndOccurredAtBetween(userId, start, end).stream().map(t -> new TransactionResponse(
      t.getId(), t.getCategory().getId(), t.getCategory().getName(), t.getOccurredAt(),
      t.getDescription(), t.getAmount(), t.getType(), t.isRecurring(), t.getMerchant()
    )).toList();
  }

  public TransactionResponse create(Long userId, TransactionRequest req){
    var cat = categoryService.getOwned(userId, req.categoryId());
    Transaction t = new Transaction();
    t.setUserId(userId);
    t.setCategory(cat);
    t.setOccurredAt(req.occurredAt());
    t.setDescription(req.description());
    t.setAmount(req.amount());
    t.setType(req.type());
    t.setRecurring(req.recurring());
    t.setMerchant(req.merchant());
    t = repo.save(t);
    return new TransactionResponse(
      t.getId(), cat.getId(), cat.getName(), t.getOccurredAt(), t.getDescription(), t.getAmount(), t.getType(), t.isRecurring(), t.getMerchant()
    );
  }

  public void delete(Long userId, Long id){
    var t = repo.findById(id).orElseThrow(() -> new NotFoundException("Transação não encontrada"));
    if (!t.getUserId().equals(userId)) throw new NotFoundException("Transação não encontrada");
    repo.delete(t);
  }
}
```

```java
// src/main/java/com/fincoach/service/ReportService.java
package com.fincoach.service;

import com.fincoach.domain.repo.TransactionRepository;
import com.fincoach.dto.CategoryTotal;
import com.fincoach.dto.MonthlySummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static com.fincoach.util.YearMonthUtils.startOf;
import static com.fincoach.util.YearMonthUtils.startOfNext;

@Service @RequiredArgsConstructor
public class ReportService {
  private final TransactionRepository txRepo;

  public MonthlySummary monthly(Long userId, YearMonth ym){
    var start = startOf(ym); var end = startOfNext(ym);

    Object[] inOut = txRepo.incomeExpenseTotals(userId, start, end);
    BigDecimal totalIncome = (BigDecimal) inOut[0];
    BigDecimal totalExpense = (BigDecimal) inOut[1];
    BigDecimal net = totalIncome.subtract(totalExpense);

    List<CategoryTotal> byCategory = new ArrayList<>();
    for (Object[] row : txRepo.totalsByCategory(userId, start, end)) {
      Long catId = ((Number) row[0]).longValue();
      String name = (String) row[1];
      BigDecimal total = (BigDecimal) row[2];
      byCategory.add(new CategoryTotal(catId, name, total));
    }

    return new MonthlySummary(ym.toString(), totalIncome, totalExpense, net, byCategory);
  }
}
```

```java
// src/main/java/com/fincoach/service/InsightService.java
package com.fincoach.service;

import com.fincoach.domain.repo.BudgetRepository;
import com.fincoach.domain.repo.TransactionRepository;
import com.fincoach.dto.Insight;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

import static com.fincoach.util.YearMonthUtils.startOf;
import static com.fincoach.util.YearMonthUtils.startOfNext;

@Service @RequiredArgsConstructor
public class InsightService {
  private final TransactionRepository txRepo;
  private final BudgetRepository budgetRepo;

  public List<Insight> generate(Long userId, YearMonth ym){
    var start = startOf(ym); var end = startOfNext(ym);
    var budgets = budgetRepo.findByUserIdAndMonth(userId, start);

    // mapa de gastos por categoria
    Map<Long, BigDecimal> spentByCat = new HashMap<>();
    for (Object[] row : txRepo.totalsByCategory(userId, start, end)) {
      Long catId = ((Number) row[0]).longValue();
      BigDecimal total = (BigDecimal) row[2];
      spentByCat.put(catId, total == null ? BigDecimal.ZERO : total);
    }

    List<Insight> out = new ArrayList<>();

    // 1) Estouro de orçamento
    budgets.forEach(b -> {
      var spent = spentByCat.getOrDefault(b.getCategory().getId(), BigDecimal.ZERO);
      if (spent.compareTo(b.getAmount()) > 0) {
        var diff = spent.subtract(b.getAmount());
        out.add(new Insight(
          "Estouro em " + b.getCategory().getName(),
          "Você excedeu o orçamento da categoria em R$ " + diff + ". Considere reduzir consumo ou ajustar o teto.",
          "warning"
        ));
      }
    });

    // 2) Assinaturas (recorrentes no mês)
    long recurringCount = txRepo.countByUserIdAndOccurredAtBetweenAndRecurringTrue(userId, start, end);
    if (recurringCount > 0) {
      out.add(new Insight(
        "Assinaturas recorrentes",
        "Você tem " + recurringCount + " lançamentos recorrentes este mês. Revise o que não usa.",
        "info"
      ));
    }

    // 3) Dica geral se despesas > 70% da receita
    Object[] inOut = txRepo.incomeExpenseTotals(userId, start, end);
    var income = (java.math.BigDecimal) inOut[0];
    var expense = (java.math.BigDecimal) inOut[1];
    if (income.compareTo(BigDecimal.ZERO) > 0) {
      var ratio = expense.divide(income, 2, java.math.RoundingMode.HALF_UP);
      if (ratio.compareTo(new BigDecimal("0.70")) > 0) {
        out.add(new Insight(
          "Despesas altas vs renda",
          "Seus gastos representam mais de 70% da renda no mês. Tente reduzir 10% nas categorias variáveis.",
          "warning"
        ));
      }
    }

    return out;
  }
}
```

---

## Controllers (REST)

```java
// Helpers de header: se ausente, usa 1
package com.fincoach.controller;
class UserHeader { static Long getOrDefault(Long header){ return header != null ? header : 1L; } }
```

```java
// src/main/java/com/fincoach/controller/CategoryController.java
package com.fincoach.controller;

import com.fincoach.dto.CategoryRequest;
import com.fincoach.dto.CategoryResponse;
import com.fincoach.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/categories") @RequiredArgsConstructor
public class CategoryController {
  private final CategoryService service;

  @GetMapping
  public List<CategoryResponse> list(@RequestHeader(value="X-User-Id", required=false) Long userId){
    return service.list(UserHeader.getOrDefault(userId));
  }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public CategoryResponse create(@RequestHeader(value="X-User-Id", required=false) Long userId,
                                 @Valid @RequestBody CategoryRequest req){
    return service.create(UserHeader.getOrDefault(userId), req);
  }
}
```

```java
// src/main/java/com/fincoach/controller/BudgetController.java
package com.fincoach.controller;

import com.fincoach.dto.BudgetRequest;
import com.fincoach.dto.BudgetResponse;
import com.fincoach.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController @RequestMapping("/budgets") @RequiredArgsConstructor
public class BudgetController {
  private final BudgetService service;

  @GetMapping
  public List<BudgetResponse> list(@RequestHeader(value="X-User-Id", required=false) Long userId,
                                   @RequestParam String month){
    var ym = YearMonth.parse(month);
    return service.list(UserHeader.getOrDefault(userId), ym);
  }

  @PostMapping
  public BudgetResponse upsert(@RequestHeader(value="X-User-Id", required=false) Long userId,
                               @Valid @RequestBody BudgetRequest req){
    return service.upsert(UserHeader.getOrDefault(userId), req);
  }
}
```

```java
// src/main/java/com/fincoach/controller/TransactionController.java
package com.fincoach.controller;

import com.fincoach.dto.TransactionRequest;
import com.fincoach.dto.TransactionResponse;
import com.fincoach.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController @RequestMapping("/transactions") @RequiredArgsConstructor
public class TransactionController {
  private final TransactionService service;

  @GetMapping
  public List<TransactionResponse> list(@RequestHeader(value="X-User-Id", required=false) Long userId,
                                        @RequestParam String month){
    var ym = YearMonth.parse(month);
    return service.listByMonth(UserHeader.getOrDefault(userId), ym);
  }

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public TransactionResponse create(@RequestHeader(value="X-User-Id", required=false) Long userId,
                                    @Valid @RequestBody TransactionRequest req){
    return service.create(UserHeader.getOrDefault(userId), req);
  }

  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@RequestHeader(value="X-User-Id", required=false) Long userId,
                     @PathVariable Long id){
    service.delete(UserHeader.getOrDefault(userId), id);
  }
}
```

```java
// src/main/java/com/fincoach/controller/ReportController.java
package com.fincoach.controller;

import com.fincoach.dto.MonthlySummary;
import com.fincoach.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController @RequestMapping("/reports") @RequiredArgsConstructor
public class ReportController {
  private final ReportService service;

  @GetMapping("/monthly-summary")
  public MonthlySummary summary(@RequestHeader(value="X-User-Id", required=false) Long userId,
                                @RequestParam String month){
    var ym = YearMonth.parse(month);
    return service.monthly(UserHeader.getOrDefault(userId), ym);
  }
}
```

```java
// src/main/java/com/fincoach/controller/InsightController.java
package com.fincoach.controller;

import com.fincoach.dto.Insight;
import com.fincoach.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController @RequestMapping("/insights") @RequiredArgsConstructor
public class InsightController {
  private final InsightService service;

  @GetMapping
  public List<Insight> insights(@RequestHeader(value="X-User-Id", required=false) Long userId,
                                @RequestParam String month){
    var ym = YearMonth.parse(month);
    return service.generate(UserHeader.getOrDefault(userId), ym);
  }
}
```

---

## README.md

````md
# FinCoach Backend (Spring Boot 3 + MySQL)

Aplicação de backend para controle de **gastos mensais**, com relatórios agregados e **insights** para redução de despesas. Este README explica instalação, execução, configuração, endpoints e exemplos de uso.

---
## Sumário
- [Arquitetura](#arquitetura)
- [Stack](#stack)
- [Requisitos](#requisitos)
- [Configuração](#configuração)
- [Como rodar (Local)](#como-rodar-local)
- [Como rodar (Docker)](#como-rodar-docker)
- [Estrutura de pastas](#estrutura-de-pastas)
- [Banco de Dados & Migrações](#banco-de-dados--migrações)
- [Convenções de Modelagem](#convenções-de-modelagem)
- [API](#api)
  - [Autorização simulada](#autorização-simulada)
  - [Categorias](#categorias)
  - [Orçamentos](#orçamentos)
  - [Transações](#transações)
  - [Relatórios](#relatórios)
  - [Insights](#insights)
  - [Formato de erro](#formato-de-erro)
- [Boas práticas & Próximos passos](#boas-práticas--próximos-passos)

---
## Arquitetura
- **Camadas**: Controller → Service → Repository (JPA) → MySQL
- **Migrações**: Flyway (`db/migration`)
- **DTOs + Validation**: requests/response com Bean Validation
- **Relatórios**: agregações por mês/categoria e totais
- **Insights**: regras simples (estouro de orçamento, recorrências, relação despesa/renda)

## Stack
- Java 21, Spring Boot 3 (web, validation, data-jpa)
- Flyway, MySQL 8
- Lombok (opcional)

## Requisitos
- Java 21+
- Maven 3.9+
- MySQL 8 (ou compatível)

## Configuração
Edite `src/main/resources/application.yml` se necessário. Padrão:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fincoach?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate.format_sql: true
      hibernate.jdbc.time_zone: America/Sao_Paulo
  flyway:
    locations: classpath:db/migration
    baseline-on-migrate: true
server:
  port: 8080
````

Crie o banco (se ainda não existir):

```sql
CREATE DATABASE fincoach CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

> **Timezone**: o app fixa `America/Sao_Paulo` (ver `FincoachApplication`)

## Como rodar (Local)

```bash
# 1) instale dependências e rode
mvn spring-boot:run
# ou
mvn clean package && java -jar target/fincoach-backend-0.1.0.jar
```

O Flyway executa a `V1__init.sql` automaticamente na primeira execução.

### Smoke test

```bash
# Criar categoria
curl -s -X POST http://localhost:8080/categories \
  -H 'Content-Type: application/json' \
  -d '{"name":"Mercado","kind":"VARIAVEL","color":"#4CAF50"}' | jq

# Configurar orçamento (YYYY-MM)
curl -s -X POST http://localhost:8080/budgets \
  -H 'Content-Type: application/json' \
  -d '{"categoryId":1,"month":"2025-08","amount":1200.00}' | jq

# Lançar transação
curl -s -X POST http://localhost:8080/transactions \
  -H 'Content-Type: application/json' \
  -d '{"categoryId":1,"occurredAt":"2025-08-15","amount":250.00,"type":"EXPENSE","description":"Compras semanais","recurring":false,"merchant":"Assaí"}' | jq

# Relatório e insights
curl -s 'http://localhost:8080/reports/monthly-summary?month=2025-08' | jq
curl -s 'http://localhost:8080/insights?month=2025-08' | jq
```

## Como rodar (Docker)

### Dockerfile (backend)

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/fincoach-backend-0.1.0.jar app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
```

### docker-compose.yml (API + MySQL)

```yaml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: fincoach
      TZ: America/Sao_Paulo
    ports: ["3306:3306"]
    volumes:
      - mysql_data:/var/lib/mysql

  api:
    build: .
    depends_on: [mysql]
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/fincoach?useSSL=false&serverTimezone=America/Sao_Paulo
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    ports: ["8080:8080"]

volumes:
  mysql_data:
```

**Passos**:

```bash
mvn clean package -DskipTests
docker compose up --build
```

## Estrutura de pastas

```
src/main/java/com/fincoach/
  ├─ controller/     # REST controllers
  ├─ domain/entity/  # JPA entities + enums
  ├─ domain/repo/    # Repositories (Spring Data JPA)
  ├─ dto/            # DTOs de request/response
  ├─ exception/      # Handler + exceções
  ├─ service/        # Regras de negócio
  ├─ util/           # Utils (YearMonth)
  └─ FincoachApplication.java
src/main/resources/
  ├─ application.yml
  └─ db/migration/V1__init.sql
```

## Banco de Dados & Migrações

* **Flyway** com `V1__init.sql` cria `categories`, `budgets`, `transactions` e índices
* Recomenda-se criar uma `V2__seed.sql` (opcional) para dados demo
* Tipos monetários em `DECIMAL(12,2)`

## Convenções de Modelagem

* `month` de orçamento usa **dia 1** do mês (ex.: `2025-08-01`)
* Valores monetários **positivos**; sinal é dado por `type` (`INCOME` | `EXPENSE`)
* `recurring` marca transações recorrentes (assinaturas)

## API

Base URL: `http://localhost:8080`

### Autorização simulada

* Em produção será JWT. No MVP, o **ID de usuário** é lido do header `X-User-Id`. Se omitido, usa `1`.

### Categorias

**GET** `/categories` → lista categorias do usuário

**POST** `/categories`

```json
{
  "name": "Mercado",
  "kind": "VARIAVEL",
  "color": "#4CAF50"
}
```

**200/201**

```json
{ "id": 1, "name": "Mercado", "kind": "VARIAVEL", "color": "#4CAF50" }
```

### Orçamentos

**GET** `/budgets?month=YYYY-MM`

**POST** `/budgets`

```json
{ "categoryId": 1, "month": "2025-08", "amount": 1200.00 }
```

**200**

```json
{ "id": 3, "categoryId": 1, "categoryName": "Mercado", "month": "2025-08-01", "amount": 1200.00 }
```

### Transações

**GET** `/transactions?month=YYYY-MM`

**POST** `/transactions`

```json
{
  "categoryId": 1,
  "occurredAt": "2025-08-15",
  "amount": 250.00,
  "type": "EXPENSE",
  "description": "Compras semanais",
  "recurring": false,
  "merchant": "Assaí"
}
```

**201**

```json
{
  "id": 10,
  "categoryId": 1,
  "categoryName": "Mercado",
  "occurredAt": "2025-08-15",
  "description": "Compras semanais",
  "amount": 250.00,
  "type": "EXPENSE",
  "recurring": false,
  "merchant": "Assaí"
}
```

**DELETE** `/transactions/{id}` → `204`

### Relatórios

**GET** `/reports/monthly-summary?month=YYYY-MM`
**200**

```json
{
  "month": "2025-08",
  "totalIncome": 5000.00,
  "totalExpense": 3200.00,
  "net": 1800.00,
  "byCategory": [
    { "categoryId": 1, "name": "Mercado", "total": 1250.00 },
    { "categoryId": 2, "name": "Restaurantes", "total": 600.00 }
  ]
}
```

### Insights

**GET** `/insights?month=YYYY-MM`
**200**

```json
[
  { "title": "Estouro em Mercado", "message": "Você excedeu o orçamento da categoria em R$ 50.00...", "severity": "warning" },
  { "title": "Assinaturas recorrentes", "message": "Você tem 3 lançamentos recorrentes este mês...", "severity": "info" }
]
```

### Formato de erro

**HTTP 400/404**

```json
{ "message": "Descrição do erro" }
```

**Validação**

```json
{
  "message": "Validation failed",
  "errors": { "amount": "must be greater than or equal to 0.00" }
}
```

---

## Boas práticas & Próximos passos

* **Autenticação/JWT** (substituir `X-User-Id` por `@AuthenticationPrincipal`)
* **OpenAPI/Swagger** (springdoc-openapi para documentação viva)
* **Testes**: unitários (services) e JPA (repositories)
* **Seeds**: criar `V2__seed.sql` com categorias + transações fake
* **Observabilidade**: logs estruturados, métricas/health (`spring-boot-starter-actuator`)
* **Produção**: profiles, variáveis sensíveis via env/secret, conexão pool (Hikari), índices adicionais conforme uso

```

## Observações finais
- Tudo está pronto para acoplar autenticação depois (JWT / Spring Security).
- O front-end pode consumir diretamente esses endpoints.
- Para seeds de dados, você pode criar uma `V2__seed.sql` com inserts.
- Ao dockerizar, aponte `SPRING_DATASOURCE_URL` para `mysql:3306` no `docker-compose`.

Se quiser, posso adicionar **V2__seed.sql** com categorias/transações fake e um **Dockerfile** do backend na próxima interação.

```
