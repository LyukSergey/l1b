package com.ua.amazonua.repository.jpa;

import com.ua.amazonua.domain.Product;
import com.ua.amazonua.repository.ProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Реалізація доменного {@link ProductRepository} поверх Spring Data JPA.
 *
 * ШАР: repository/jpa. Це адаптер: він реалізує НАШ інтерфейс сховища, делегуючи
 * виклики до {@link ProductJpaRepository}. Завдяки цьому сервіс і контролер
 * залежать від того самого {@code ProductRepository}, що й раніше, і НЕ зміняться
 * при переході з пам'яті на БД — наочна інверсія залежностей (DIP).
 *
 * {@code @Primary} робить цю реалізацію основною (замість in-memory, яка лишилась
 * у коді лише для демонстрації/тестів під профілем).
 */
@Repository
@Primary
public class JpaProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpa;

    public JpaProductRepositoryAdapter(ProductJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Product> findAll() {
        return jpa.findAll();
    }

    @Override
    public List<Product> findByCategory(String category) {
        return jpa.findByCategoryIgnoreCase(category);
    }

    @Override
    public List<Product> search(String category, BigDecimal minPrice, BigDecimal maxPrice, String q) {
        // усі текстові критерії — через LIKE-патерни у нижньому регістрі (wildcards %…%);
        // null лишається null, тож відповідна умова не застосовується
        String categoryPattern = likePattern(category);
        String qPattern = likePattern(q);
        return jpa.search(categoryPattern, minPrice, maxPrice, qPattern);
    }

    @Override
    public Page<Product> searchPage(String category, BigDecimal minPrice, BigDecimal maxPrice,
                                    String q, Pageable pageable) {
        // ті самі LIKE-патерни, що й у search(); сторінку й сортування несе Pageable
        return jpa.searchPage(likePattern(category), minPrice, maxPrice, likePattern(q), pageable);
    }

    @Override
    public List<String> findCategories() {
        return jpa.findDistinctCategories();
    }

    @Override
    public List<DiscountedRow> findDiscounted() {
        // кожен рядок подання приходить як Object[] {id, name, price}; мапимо у вузький record
        return jpa.findDiscountedView().stream()
                .map(r -> new DiscountedRow(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (BigDecimal) r[2]))
                .toList();
    }

    @Override
    public long countByCategoryFunction(String category) {
        return jpa.countByCategoryFunction(category);
    }

    private static String likePattern(String value) {
        return value == null ? null : "%" + value.toLowerCase() + "%";
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Product save(Product product) {
        return jpa.save(product);
    }

    @Override
    public boolean deleteById(Long id) {
        if (jpa.existsById(id)) {
            jpa.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
