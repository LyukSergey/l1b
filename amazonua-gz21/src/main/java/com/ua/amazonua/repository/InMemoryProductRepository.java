package com.ua.amazonua.repository;

import com.ua.amazonua.domain.Product;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory реалізація {@link ProductRepository} (використовувалась на заняттях 1/1–1/5).
 *
 * ШАР: repository. Заняття 2/1: основною стала JPA-реалізація
 * ({@code JpaProductRepositoryAdapter}). Цю лишено для демонстрації інверсії залежностей —
 * активується ЛИШЕ під профілем {@code inmemory} (щоб не конфліктувати з JPA-реалізацією).
 * Увімкнути: {@code --spring.profiles.active=inmemory}. Наочно показує: щоб замінити
 * сховище, достатньо підставити іншу реалізацію ТОГО САМОГО інтерфейсу — сервіс/контролер
 * не зміняться.
 */
@Repository
@Profile("inmemory")
public class InMemoryProductRepository implements ProductRepository {

    private final Map<Long, Product> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return storage.values().stream()
                .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                .toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(sequence.incrementAndGet());
        }
        storage.put(product.getId(), product);
        return product;
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public void deleteAll() {
        storage.clear();
        sequence.set(0); // скидаємо лічильник, щоб id після повторного seed були передбачувані (1, 2, ...)
    }
}
