package com.ua.amazonua.repository;

import com.ua.amazonua.domain.Product;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public List<Product> search(String category, java.math.BigDecimal minPrice,
                                java.math.BigDecimal maxPrice, String q) {
        // той самий примітив, що й SQL LIKE %…%: підрядок без урахування регістру
        String catLower = category == null ? null : category.toLowerCase();
        String qLower = q == null ? null : q.toLowerCase();
        return storage.values().stream()
                .filter(p -> catLower == null ||
                        (p.getCategory() != null && p.getCategory().toLowerCase().contains(catLower)))
                .filter(p -> minPrice == null || p.getPrice().compareTo(minPrice) >= 0)
                .filter(p -> maxPrice == null || p.getPrice().compareTo(maxPrice) <= 0)
                .filter(p -> qLower == null || p.getName().toLowerCase().contains(qLower))
                .sorted(java.util.Comparator.comparing(Product::getId))
                .toList();
    }

    @Override
    public Page<Product> searchPage(String category, java.math.BigDecimal minPrice,
                                    java.math.BigDecimal maxPrice, String q, Pageable pageable) {
        // застосовуємо ті самі фільтри, тоді вручну «вирізаємо» потрібну сторінку
        List<Product> all = search(category, minPrice, maxPrice, q);
        int from = (int) Math.min(pageable.getOffset(), all.size());
        int to = Math.min(from + pageable.getPageSize(), all.size());
        List<Product> pageContent = all.subList(from, to);
        // загальна кількість (all.size()) потрібна для totalElements/totalPages у Page
        return new PageImpl<>(pageContent, pageable, all.size());
    }

    @Override
    public List<String> findCategories() {
        return storage.values().stream()
                .map(Product::getCategory)
                .filter(c -> c != null)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public List<DiscountedRow> findDiscounted() {
        // VIEW існує лише в PostgreSQL; у пам'яті подань немає
        throw new UnsupportedOperationException(
                "SQL-view products_discounted доступний лише на PostgreSQL, не в in-memory сховищі");
    }

    @Override
    public Page<DiscountedRow> findDiscountedPage(Pageable pageable) {
        throw new UnsupportedOperationException(
                "SQL-view products_discounted доступний лише на PostgreSQL, не в in-memory сховищі");
    }

    @Override
    public long countByCategoryFunction(String category) {
        // збережена функція існує лише в PostgreSQL
        throw new UnsupportedOperationException(
                "Збережена функція count_products_by_category доступна лише на PostgreSQL");
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
