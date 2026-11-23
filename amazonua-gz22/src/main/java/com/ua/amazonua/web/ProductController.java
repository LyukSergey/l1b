package com.ua.amazonua.web;

import com.ua.amazonua.service.ProductService;
import com.ua.amazonua.web.dto.PageDto;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import com.ua.amazonua.web.dto.ProductRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API товарів.
 *
 * ШАР: web. Залежить лише від {@link ProductService} (інтерфейсу) — DIP.
 * Відповідає тільки за HTTP: прийняти запит, повернути DTO. Жодної логіки.
 *
 * Заняття 1/2 — повний CRUD у пам'яті:
 *   GET    /api/products              → список карток (опц. ?category=...)
 *   GET    /api/products/{id}         → один товар з деталями
 *   POST   /api/products              → створити товар        (201 Created)
 *   PUT    /api/products/{id}         → оновити товар         (200 OK)
 *   DELETE /api/products/{id}         → видалити товар        (204 No Content)
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Перелік товарів із панеллю фільтрів: усі параметри опційні.
     *   ?category=  — фільтр за категорією
     *   ?minPrice= &maxPrice=  — діапазон ціни
     *   ?q=         — текстовий пошук за назвою (примітивний LIKE — див. README заняття)
     */
    @GetMapping
    public List<ProductCardDto> list(@RequestParam(required = false) String category,
                                     @RequestParam(required = false) BigDecimal minPrice,
                                     @RequestParam(required = false) BigDecimal maxPrice,
                                     @RequestParam(required = false) String q) {
        return productService.search(category, minPrice, maxPrice, q);
    }

    /**
     * Заняття 2/2: ПАГІНОВАНИЙ перелік із тими самими фільтрами + пагінація/сортування.
     * Параметри сторінки Spring збирає у {@link Pageable} автоматично:
     *   ?page=  — номер сторінки (з 0, типово 0)
     *   ?size=  — розмір сторінки (типово 20)
     *   ?sort=  — поле сортування, напр. ?sort=price,asc або ?sort=name,desc
     * Повертає {@link PageDto}: вміст сторінки + метадані (totalElements, totalPages).
     */
    @GetMapping("/page")
    public PageDto<ProductCardDto> page(@RequestParam(required = false) String category,
                                        @RequestParam(required = false) BigDecimal minPrice,
                                        @RequestParam(required = false) BigDecimal maxPrice,
                                        @RequestParam(required = false) String q,
                                        Pageable pageable) {
        return productService.searchPage(category, minPrice, maxPrice, q, pageable);
    }

    /** Унікальні категорії — для випадаючого списку фільтра на UI. */
    @GetMapping("/categories")
    public List<String> categories() {
        return productService.findCategories();
    }

    /**
     * Заняття 2/2 (оглядово): перелік товарів зі знижкою — читання SQL-view (VIEW).
     * View створює міграція Liquibase; застосунок лише читає його.
     */
    @GetMapping("/discounted")
    public List<ProductCardDto> discounted() {
        return productService.findDiscounted();
    }

    /** Заняття 2/2: ПАГІНОВАНИЙ перелік товарів зі знижкою (?page=&size=&sort=). */
    @GetMapping("/discounted/page")
    public PageDto<ProductCardDto> discountedPage(Pageable pageable) {
        return productService.findDiscountedPage(pageable);
    }

    /**
     * Заняття 2/2 (оглядово): кількість товарів у категорії, порахована збереженою
     * ФУНКЦІЄЮ в самій БД (count_products_by_category).
     */
    @GetMapping("/count")
    public long countByCategory(@RequestParam String category) {
        return productService.countByCategory(category);
    }

    @GetMapping("/{id}")
    public ProductDetailDto getOne(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDetailDto create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    public ProductDetailDto update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
