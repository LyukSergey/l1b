package com.ua.amazonua.repository;

import com.ua.amazonua.domain.Product;

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
