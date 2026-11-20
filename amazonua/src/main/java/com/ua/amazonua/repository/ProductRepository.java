package com.ua.amazonua.repository;

import com.ua.amazonua.domain.Product;

import java.util.List;
import java.util.Optional;

/**
 * Контракт сховища товарів (ПОРТ).
 *
 * ШАР: repository (абстракція). Сервіс залежить САМЕ від цього інтерфейсу,
 * а не від конкретної реалізації — це DIP (Dependency Inversion).
 *
 * Заняття 1/1: реалізація — {@link InMemoryProductRepository}.
 * Крок 2: з'явиться JPA-реалізація; сервіс і контролер не зміняться.
 */
public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(Long id);

    Product save(Product product);
}
