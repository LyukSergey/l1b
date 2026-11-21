package com.ua.amazonua.service;

import com.ua.amazonua.domain.Product;
import com.ua.amazonua.exception.ProductNotFoundException;
import com.ua.amazonua.repository.ProductRepository;
import com.ua.amazonua.service.mapper.ProductMapper;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import com.ua.amazonua.web.dto.ProductRequest;
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
