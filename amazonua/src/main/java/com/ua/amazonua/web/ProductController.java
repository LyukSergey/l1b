package com.ua.amazonua.web;

import com.ua.amazonua.service.ProductService;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API товарів.
 *
 * ШАР: web. Залежить лише від {@link ProductService} (інтерфейсу) — DIP.
 * Відповідає тільки за HTTP: прийняти запит, повернути DTO. Жодної логіки.
 *
 * Заняття 1/1 — два ендпоінти (перегляд):
 *   GET /api/products       → список карток
 *   GET /api/products/{id}  → один товар з фото та деталями
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductCardDto> list() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ProductDetailDto getOne(@PathVariable Long id) {
        return productService.findById(id);
    }
}
