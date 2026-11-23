package com.ua.amazonua.service;

import com.ua.amazonua.domain.Product;
import com.ua.amazonua.exception.ProductNotFoundException;
import com.ua.amazonua.repository.ProductRepository;
import com.ua.amazonua.service.mapper.ProductMapper;
import com.ua.amazonua.web.dto.PageDto;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import com.ua.amazonua.web.dto.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Реалізація {@link ProductService}.
 *
 * ШАР: service (реалізація). Залежить від АБСТРАКЦІЙ:
 *  - {@link ProductRepository} (інтерфейс сховища) — DIP,
 *  - {@link ProductMapper} (мапінг у DTO) — SRP.
 * Залежності приходять через конструктор (IoC/DI).
 *
 * {@code @Transactional(readOnly = true)} на рівні класу тримає Hibernate-сесію
 * відкритою на час методу — тому мапінг у DTO встигає завантажити ліниві (LAZY)
 * колекції (images, characteristics) ДО закриття сесії. Без цього (і з open-in-view=false)
 * виникала б LazyInitializationException. Методи, що ЗМІНЮЮТЬ дані, перекривають це
 * на звичайну (read-write) транзакцію. Детально транзакції — на занятті 2/6.
 */
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ProductCardDto> findAll(String category) {
        // лишаємо для сумісності з попередніми заняттями; делегуємо в загальний пошук
        return search(category, null, null, null);
    }

    @Override
    public List<ProductCardDto> search(String category, BigDecimal minPrice, BigDecimal maxPrice, String q) {
        // порожні значення нормалізуємо в null, щоб відповідна умова не застосовувалась
        String cat = (category == null || category.isBlank()) ? null : category;
        String query = (q == null || q.isBlank()) ? null : q;
        return repository.search(cat, minPrice, maxPrice, query).stream()
                .map(mapper::toCard)
                .toList();
    }

    @Override
    public List<ProductCardDto> searchFullText(String q) {
        if (q == null || q.isBlank()) {
            return List.of();   // порожній запит — порожній результат
        }
        return repository.searchFullText(q).stream()
                .map(mapper::toCard)
                .toList();
    }

    @Override
    public PageDto<ProductCardDto> searchPage(String category, BigDecimal minPrice,
                                              BigDecimal maxPrice, String q, Pageable pageable) {
        String cat = (category == null || category.isBlank()) ? null : category;
        String query = (q == null || q.isBlank()) ? null : q;
        Page<Product> page = repository.searchPage(cat, minPrice, maxPrice, query, pageable);
        // Spring-тип Page мапимо у власний вузький PageDto: вміст карток + метадані сторінки
        List<ProductCardDto> content = page.getContent().stream().map(mapper::toCard).toList();
        return new PageDto<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public List<String> findCategories() {
        return repository.findCategories();
    }

    @Override
    public List<ProductCardDto> findDiscounted() {
        // рядок view (id, name, price, image_url) → картка
        return repository.findDiscounted().stream()
                .map(r -> new ProductCardDto(r.id(), r.name(), r.price(), r.imageUrl()))
                .toList();
    }

    @Override
    public PageDto<ProductCardDto> findDiscountedPage(Pageable pageable) {
        // без сортування пагінація по поданню недетермінована — за замовчуванням упорядковуємо за ціною
        Pageable effective = pageable.getSort().isSorted()
                ? pageable
                : org.springframework.data.domain.PageRequest.of(
                        pageable.getPageNumber(), pageable.getPageSize(),
                        org.springframework.data.domain.Sort.by("price").ascending());
        var page = repository.findDiscountedPage(effective);
        List<ProductCardDto> content = page.getContent().stream()
                .map(r -> new ProductCardDto(r.id(), r.name(), r.price(), r.imageUrl()))
                .toList();
        return new PageDto<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public long countByCategory(String category) {
        return repository.countByCategoryFunction(category);
    }

    @Override
    public ProductDetailDto findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return mapper.toDetail(product);
    }

    @Override
    @Transactional   // read-write: метод змінює дані
    public ProductDetailDto create(ProductRequest request) {
        Product product = mapper.toDomain(request);
        product.setId(null);                 // id призначить репозиторій
        Product saved = repository.save(product);
        return mapper.toDetail(saved);
    }

    @Override
    @Transactional
    public ProductDetailDto update(Long id, ProductRequest request) {
        // переконуємось, що товар існує (інакше 404)
        repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        Product product = mapper.toDomain(request);
        product.setId(id);                   // зберігаємо той самий id
        Product saved = repository.save(product);
        return mapper.toDetail(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        boolean removed = repository.deleteById(id);
        if (!removed) {
            throw new ProductNotFoundException(id);
        }
    }
}
