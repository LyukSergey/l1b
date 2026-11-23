package com.ua.amazonua.repository.jpa;

import com.ua.amazonua.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /** Унікальні непорожні категорії (для випадаючого списку фільтра на UI). */
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctCategories();

    /**
     * Заняття 2/2: ПАГІНОВАНИЙ пошук/фільтр. Той самий набір опційних критеріїв, що й у
     * {@link #search}, але результат — {@link Page} зі сторінкою записів і метаданими
     * (загальна кількість елементів/сторінок). Сортування НЕ зашито в запит (немає
     * ORDER BY) — його задає {@link Pageable} (параметр ?sort=...).
     *
     * Для {@code Page} Spring Data потрібен ще й запит підрахунку загальної кількості —
     * його задаємо явно через {@code countQuery}, щоб рахувати за тими самими критеріями.
     */
    @Query(value = """
            SELECT p FROM Product p
            WHERE (:categoryPattern IS NULL OR LOWER(p.category) LIKE CAST(:categoryPattern AS string))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:qPattern IS NULL OR LOWER(p.name) LIKE CAST(:qPattern AS string))
            """,
            countQuery = """
            SELECT count(p) FROM Product p
            WHERE (:categoryPattern IS NULL OR LOWER(p.category) LIKE CAST(:categoryPattern AS string))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:qPattern IS NULL OR LOWER(p.name) LIKE CAST(:qPattern AS string))
            """)
    Page<Product> searchPage(@Param("categoryPattern") String categoryPattern,
                             @Param("minPrice") BigDecimal minPrice,
                             @Param("maxPrice") BigDecimal maxPrice,
                             @Param("qPattern") String qPattern,
                             Pageable pageable);

    /**
     * Заняття 2/2 (ОГЛЯДОВО): читання SQL-view (VIEW) products_discounted через
     * НАТИВНИЙ запит. View створює міграція Liquibase; тут ми лише читаємо його як
     * звичайну таблицю. nativeQuery=true означає «це справжній SQL, не JPQL».
     */
    @Query(value = "SELECT id, name, price, image_url FROM products_discounted ORDER BY price", nativeQuery = true)
    List<Object[]> findDiscountedView();

    /**
     * Заняття 2/2: ПАГІНОВАНЕ читання того самого view. Для нативного запиту з {@link Page}
     * обов'язковий окремий {@code countQuery}. Кожен рядок — Object[] {id, name, price, image_url}.
     */
    @Query(value = "SELECT id, name, price, image_url FROM products_discounted",
            countQuery = "SELECT count(*) FROM products_discounted",
            nativeQuery = true)
    Page<Object[]> findDiscountedViewPage(Pageable pageable);

    /**
     * Заняття 2/2 (ОГЛЯДОВО): виклик збереженої ФУНКЦІЇ count_products_by_category(text),
     * яку створює міграція Liquibase. Нативний запит повертає одне число.
     */
    @Query(value = "SELECT count_products_by_category(:category)", nativeQuery = true)
    long countByCategoryFunction(@Param("category") String category);

    /**
     * Заняття 2/4: ПОВНОТЕКСТОВИЙ пошук (FTS) по products.search_vector.
     *
     * - {@code plainto_tsquery('simple', :q)} перетворює сирий текст користувача на tsquery
     *   (нормалізує слова, сам ставить AND між ними);
     * - оператор {@code @@} — збіг між tsvector (документ) і tsquery (запит);
     * - {@code ts_rank(...)} дає оцінку релевантності — сортуємо за спаданням.
     * Результати повертаємо повними сутностями Product (SELECT *).
     */
    @Query(value = """
            SELECT * FROM products
            WHERE search_vector @@ plainto_tsquery('simple', :q)
            ORDER BY ts_rank(search_vector, plainto_tsquery('simple', :q)) DESC, id
            """, nativeQuery = true)
    List<Product> searchFullText(@Param("q") String q);
}
