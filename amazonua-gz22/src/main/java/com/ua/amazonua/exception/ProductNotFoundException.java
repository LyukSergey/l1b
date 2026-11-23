package com.ua.amazonua.exception;

/**
 * Кидаємо, коли товар за id не знайдено.
 *
 * ШАР: exception. Заняття 1/4: прибрано {@code @ResponseStatus} — тепер цей виняток
 * обробляє глобальний {@code GlobalExceptionHandler} і повертає 404 ЗІ структурованим
 * тілом {@code ErrorResponse} (єдиний формат помилок для всього API).
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Товар з id=" + id + " не знайдено");
    }
}
