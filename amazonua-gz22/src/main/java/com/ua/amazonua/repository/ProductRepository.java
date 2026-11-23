package com.ua.amazonua.repository;

import com.ua.amazonua.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Контракт сховища товарів (ПОРТ).
 *
 * ШАР: repository (абстракція). Сервіс залежить САМЕ від цього інтерфейсу,
 * а не від конкретної реалізації — це DIP (Dependency Inversion).
 *
 * Заняття 1/2: реалізація — {@link InMemoryProductRepository}; додано методи
 * видалення та пошуку за категорією (для повного CRUD і фільтра).
 * Крок 2: з'явиться JPA-реалізація; сервіс і контролер не зміняться.
 */
public interface ProductRepository {

    List<Product> findAll();

    List<Product> findByCategory(String category);

    /**
     * Комбінований фільтр/пошук для панелі фільтрів UI: категорія + діапазон ціни +
     * текстовий пошук (усі критерії опційні, null = не застосовувати).
     * Текстовий пошук примітивний (LIKE по name) — його обмеження є навчальним акцентом 2/1.
     */
    List<Product> search(String category, BigDecimal minPrice, BigDecimal maxPrice, String q);

    /**
     * Заняття 2/2: ПАГІНОВАНИЙ варіант {@link #search}. Ті самі опційні критерії, але
     * повертає ОДНУ сторінку результатів ({@link Page}) згідно з {@link Pageable}
     * (номер і розмір сторінки + сортування). Потрібен для великих переліків, щоб не
     * вантажити всі записи одразу.
     */
    Page<Product> searchPage(String category, BigDecimal minPrice, BigDecimal maxPrice, String q, Pageable pageable);

    /** Унікальні категорії (для випадаючого списку фільтра на UI). */
    List<String> findCategories();

    /**
     * Заняття 2/2 (ОГЛЯДОВО): рядки SQL-подання products_discounted (id, name, price).
     * Подання — збережений іменований запит у БД; читаємо його як таблицю.
     */
    List<DiscountedRow> findDiscounted();

    /**
     * Заняття 2/2 (ОГЛЯДОВО): результат збереженої функції count_products_by_category.
     * Повертає кількість товарів у вказаній категорії, пораховану кодом у самій БД.
     */
    long countByCategoryFunction(String category);

    /** Рядок подання «товари зі знижкою» (мінімальний зріз для демонстрації VIEW). */
    record DiscountedRow(Long id, String name, BigDecimal price) {
    }

    Optional<Product> findById(Long id);

    Product save(Product product);

    boolean deleteById(Long id);

    /**
     * Видалити ВСІ записи зі сховища.
     * Потрібно для ізоляції інтеграційних тестів: кожен тест має стартувати з
     * відомого стану, щоб тести не впливали один на одного. У Модулі 2 (реальна БД)
     * цей метод надасть {@code JpaRepository.deleteAll()}.
     */
    void deleteAll();
}
