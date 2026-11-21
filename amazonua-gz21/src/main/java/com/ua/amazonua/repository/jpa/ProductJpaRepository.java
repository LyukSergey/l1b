package com.ua.amazonua.repository.jpa;

import com.ua.amazonua.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Spring Data JPA репозиторій для {@link Product}.
 *
 * ШАР: repository/jpa. Успадкування від {@link JpaRepository} дає готові методи
 * (findAll, findById, save, deleteById, deleteAll...) — реалізацію генерує Spring Data.
 *
 * Це НЕ наш доменний інтерфейс {@code ProductRepository}. Його поєднує з доменним
 * контрактом адаптер {@code JpaProductRepositoryAdapter} — так зберігається DIP.
 */
public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIgnoreCase(String category);

    /**
     * Комбінований пошук/фільтр для панелі фільтрів UI.
     * Кожен критерій ОПЦІЙНИЙ: якщо параметр null — умова не застосовується
     * (патерн "(:param IS NULL OR ...)").
     *
     * ВАЖЛИВО (навчальний акцент заняття): пошук за текстом тут зроблено через SQL LIKE
     * по полю name — це примітив. LIKE шукає ПІДРЯДОК буква-в-букву: не розуміє словоформ,
     * синонімів, помилок, не ранжує за релевантністю і на великих обсягах повільний
     * (повний скан без індексу). Тобто повноцінний повнотекстовий пошук у реляційній БД
     * через LIKE зробити по-людськи НЕМОЖЛИВО. Нормальне рішення — повнотекстовий пошук
     * PostgreSQL (tsvector, заняття 2/4) або пошуковий рушій OpenSearch (заняття 3/1).
     */
    @Query("""
            SELECT p FROM Product p
            WHERE (:categoryPattern IS NULL OR LOWER(p.category) LIKE CAST(:categoryPattern AS string))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:qPattern IS NULL OR LOWER(p.name) LIKE CAST(:qPattern AS string))
            ORDER BY p.id
            """)
    List<Product> search(@Param("categoryPattern") String categoryPattern,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         @Param("qPattern") String qPattern);
}
