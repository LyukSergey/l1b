package com.ua.amazonua.web.dto;

import java.util.List;

/**
 * Структурована відповідь про помилку (єдиний формат для всіх помилок API).
 *
 * ШАР: web/dto. Повертається глобальним обробником помилок. Замість того щоб
 * клієнт отримував стандартну сторінку помилки Spring, він дістає передбачуваний JSON:
 * статус, тип помилки, повідомлення і (за потреби) перелік проблем валідації.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        List<String> details
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, List.of());
    }
}
