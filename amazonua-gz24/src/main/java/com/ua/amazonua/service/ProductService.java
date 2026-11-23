package com.ua.amazonua.service;

import com.ua.amazonua.web.dto.PageDto;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import com.ua.amazonua.web.dto.ProductRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Контракт сервісу товарів (бізнес-логіка).
 *
 * ШАР: service (абстракція). Контролер залежить від цього інтерфейсу, а не
 * від реалізації — DIP. Це ж полегшує тести (можна підставити мок/стаб).
 *
 * Заняття 1/2: до читання (findAll/findById з 1/1) додано повний CRUD —
 * створення, оновлення, видалення — та фільтр за категорією.
 */
public interface ProductService {

    /** Список товарів для карток; якщо category != null — лише цієї категорії. */
    List<ProductCardDto> findAll(String category);

    /**
     * Фільтр/пошук для панелі фільтрів: категорія + діапазон ціни + текстовий пошук.
     * Усі критерії опційні (null = не застосовувати). Текстовий пошук — примітивний (LIKE).
     */
    List<ProductCardDto> search(String category, BigDecimal minPrice, BigDecimal maxPrice, String q);

    /**
     * Заняття 2/2: ПАГІНОВАНИЙ фільтр/пошук. Ті самі опційні критерії, але повертає ОДНУ
     * сторінку карток разом із метаданими ({@link PageDto}): номер/розмір сторінки,
     * загальна кількість записів і сторінок. Параметри сторінки й сортування несе Pageable.
     */
    PageDto<ProductCardDto> searchPage(String category, BigDecimal minPrice, BigDecimal maxPrice,
                                       String q, Pageable pageable);

    /**
     * Заняття 2/4: ПОВНОТЕКСТОВИЙ пошук за назвою/описом із ранжуванням за релевантністю
     * (засобами PostgreSQL). На відміну від LIKE, знаходить за нормалізованим текстом.
     */
    List<ProductCardDto> searchFullText(String q);

    /** Список унікальних категорій (для випадаючого списку фільтра). */
    List<String> findCategories();

    /** Заняття 2/2 (оглядово): рядки SQL-view «товари зі знижкою». */
    List<ProductCardDto> findDiscounted();

    /** Заняття 2/2: ПАГІНОВАНИЙ перелік товарів зі знижкою (сторінка карток + метадані). */
    PageDto<ProductCardDto> findDiscountedPage(Pageable pageable);

    /** Заняття 2/2 (оглядово): кількість товарів у категорії, порахована збереженою функцією. */
    long countByCategory(String category);

    /** Один товар з повними деталями; кидає ProductNotFoundException, якщо нема. */
    ProductDetailDto findById(Long id);

    /** Створити товар; повертає створений із призначеним id. */
    ProductDetailDto create(ProductRequest request);

    /** Оновити товар за id; кидає ProductNotFoundException, якщо нема. */
    ProductDetailDto update(Long id, ProductRequest request);

    /** Видалити товар за id; кидає ProductNotFoundException, якщо нема. */
    void delete(Long id);
}
