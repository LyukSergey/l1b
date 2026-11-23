package com.ua.amazonua.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Вхідні дані нового відгуку (заняття 2/4).
 *
 * ШАР: web/dto. productId не приймаємо в тілі — він береться зі шляху
 * ({@code POST /api/products/{id}/reviews}). Bean Validation перевіряє рейтинг 1..5 і текст.
 */
public record ReviewRequest(
        @NotBlank String author,
        @Min(1) @Max(5) int rating,
        @NotBlank String text
) {
}
