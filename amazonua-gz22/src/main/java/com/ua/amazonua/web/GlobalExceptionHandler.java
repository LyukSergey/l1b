package com.ua.amazonua.web;

import com.ua.amazonua.exception.ProductNotFoundException;
import com.ua.amazonua.web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Глобальна обробка помилок (заняття 1/4).
 *
 * ШАР: web. {@code @RestControllerAdvice} застосовує {@code @ExceptionHandler}-методи
 * до УСІХ контролерів в одному місці — замість того щоб обробляти помилки в кожному
 * контролері окремо. Клієнт завжди отримує передбачуваний JSON {@link ErrorResponse},
 * а не стандартну сторінку помилки Spring.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Товар не знайдено → 404 з єдиним форматом помилки. */
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ProductNotFoundException ex) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );
    }

    /**
     * Невалідне тіло запиту (@Valid не пройшов) → 400 з переліком проблем.
     * Spring MVC кидає MethodArgumentNotValidException, коли валідація @RequestBody не вдалась.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Помилка валідації вхідних даних",
                details
        );
    }
}
