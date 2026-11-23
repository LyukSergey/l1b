package com.ua.amazonua.web.dto;

import java.time.Instant;

/**
 * Відгук на товар для відповіді API (заняття 2/4).
 *
 * ШАР: web/dto. Назовні віддаємо DTO, а не доменний документ {@code Review}.
 */
public record ReviewDto(
        String id,
        Long productId,
        String author,
        int rating,
        String text,
        Instant createdAt
) {
}
