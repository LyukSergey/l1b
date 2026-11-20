package com.ua.amazonua.service;

import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;

import java.util.List;

/**
 * Контракт сервісу товарів (бізнес-логіка).
 *
 * ШАР: service (абстракція). Контролер залежить від цього інтерфейсу, а не
 * від реалізації — DIP. Це ж полегшує тести (можна підставити мок/стаб).
 */
public interface ProductService {

    /** Список товарів для карток у списку. */
    List<ProductCardDto> findAll();

    /** Один товар з повними деталями; кидає ProductNotFoundException, якщо нема. */
    ProductDetailDto findById(Long id);
}
