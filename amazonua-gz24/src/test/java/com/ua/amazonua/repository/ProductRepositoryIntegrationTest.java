package com.ua.amazonua.repository;

import com.ua.amazonua.AbstractIntegrationTest;
import com.ua.amazonua.domain.Characteristic;
import com.ua.amazonua.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Інтеграційний тест РЕПОЗИТОРНОГО шару (заняття 2/2) проти реального PostgreSQL
 * (singleton-контейнер із {@link AbstractIntegrationTest}).
 *
 * Перевіряє доменний {@link ProductRepository} напряму (бін — реальний JPA-адаптер,
 * нічого не мокано): пагінацію/сортування ({@code searchPage}) і оглядові методи поверх
 * SQL-view та збереженої функції. АВТОНОМІЯ: {@code @BeforeEach} чистить сховище, кожен
 * тест сам готує потрібні записи через {@link #givenProduct}.
 */
@SpringBootTest
class ProductRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void clear() {
        repository.deleteAll();
    }

    private void givenProduct(String name, String category, String price) {
        Product p = new Product(null, name, new BigDecimal(price), "/images/x.svg",
                List.of("/images/x.svg"), "опис", category,
                List.of(new Characteristic("k", "v")));
        repository.save(p);
    }

    @Test
    void searchPage_returnsSliceWithTotals() {
        for (int i = 1; i <= 5; i++) {
            givenProduct("Товар " + i, "Гаджети", String.valueOf(i * 100));
        }

        // друга сторінка по 2 (page=1, size=2)
        Page<Product> page = repository.searchPage(null, null, null, null, PageRequest.of(1, 2));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(1);
    }

    @Test
    void searchPage_sortsByPriceDescending() {
        givenProduct("Дешевий", "Гаджети", "100.00");
        givenProduct("Дорогий", "Гаджети", "900.00");
        givenProduct("Середній", "Гаджети", "500.00");

        Page<Product> page = repository.searchPage(null, null, null, null,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "price")));

        assertThat(page.getContent().get(0).getName()).isEqualTo("Дорогий");
        assertThat(page.getContent().get(2).getName()).isEqualTo("Дешевий");
    }

    @Test
    void searchPage_appliesFilterBeforePaging() {
        givenProduct("Навушники", "Аудіо", "1200.00");
        givenProduct("Клавіатура", "Периферія", "1800.00");
        givenProduct("Миша", "Периферія", "700.00");

        Page<Product> page = repository.searchPage("Периферія", null, null, null,
                PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findDiscounted_readsViewBelowThreshold() {
        // view products_discounted: price < 1500
        givenProduct("Дешева миша", "Периферія", "749.00");
        givenProduct("Дорогий монітор", "Монітори", "8499.00");

        List<ProductRepository.DiscountedRow> rows = repository.findDiscounted();

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).name()).isEqualTo("Дешева миша");
        assertThat(rows.get(0).imageUrl()).isEqualTo("/images/x.svg");  // view тепер віддає й фото
    }

    @Test
    void findDiscountedPage_returnsSliceOfView() {
        // 5 дешевих (< 1500) + 1 дорогий → view має 5 рядків
        for (int i = 1; i <= 5; i++) givenProduct("Знижка " + i, "Періферія", String.valueOf(i * 100));
        givenProduct("Дорогий", "Монітори", "9999.00");

        Page<ProductRepository.DiscountedRow> page =
                repository.findDiscountedPage(PageRequest.of(0, 2, Sort.by("price").ascending()));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
    }

    @Test
    void countByCategoryFunction_countsInDatabase() {
        givenProduct("A", "Периферія", "100.00");
        givenProduct("B", "Периферія", "200.00");
        givenProduct("C", "Аудіо", "300.00");

        assertThat(repository.countByCategoryFunction("Периферія")).isEqualTo(2);
    }
}
