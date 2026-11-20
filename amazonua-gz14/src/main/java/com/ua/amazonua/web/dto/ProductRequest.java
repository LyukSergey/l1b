package com.ua.amazonua.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * Вхідні дані для СТВОРЕННЯ або ОНОВЛЕННЯ товару (тіло запиту POST/PUT).
 *
 * ШАР: web/dto. Окремий від вихідних DTO контракт: клієнт надсилає поля товару,
 * але БЕЗ id — ідентифікатор призначає сервер (при створенні) або передається
 * у шляху (при оновленні). Так вхідний і вихідний контракти не змішуються.
 *
 * Заняття 1/4: додано правила валідації (Bean Validation). Якщо запит їх порушує,
 * Spring кидає MethodArgumentNotValidException, а GlobalExceptionHandler повертає 400.
 */
public record ProductRequest(
        @NotBlank(message = "назва обов'язкова")
        String name,

        @Positive(message = "ціна має бути додатною")
        BigDecimal price,

        String imageUrl,
        List<String> images,
        String description,

        @NotBlank(message = "категорія обов'язкова")
        String category,

        List<CharacteristicDto> characteristics
) {
}
