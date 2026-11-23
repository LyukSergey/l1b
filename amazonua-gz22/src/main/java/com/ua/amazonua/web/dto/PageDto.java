package com.ua.amazonua.web.dto;

import java.util.List;

/**
 * Сторінка результатів для відповіді API (заняття 2/2, пагінація).
 *
 * ШАР: web/dto. Власний, вузький контракт сторінки — навмисно НЕ віддаємо назовні
 * Spring-тип {@code Page<T>} цілком (у нього багато службових полів). Клієнту потрібні
 * лише вміст сторінки та базові метадані для навігації:
 *
 *  - content       — елементи поточної сторінки;
 *  - page          — номер поточної сторінки (з 0);
 *  - size          — розмір сторінки;
 *  - totalElements — усього записів за критеріями (усі сторінки разом);
 *  - totalPages    — усього сторінок.
 *
 * Параметр типу T дозволяє переносити будь-який DTO (у нас — {@link ProductCardDto}).
 */
public record PageDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
