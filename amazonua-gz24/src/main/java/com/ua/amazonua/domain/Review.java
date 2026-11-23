package com.ua.amazonua.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Відгук на товар — документ MongoDB (заняття 2/4).
 *
 * ШАР: domain. На відміну від реляційного {@link Product} (таблиця з фіксованою схемою),
 * відгук зберігається у MongoDB як ГНУЧКИЙ ДОКУМЕНТ у колекції {@code reviews}. Схему
 * наперед оголошувати не треба — поля документа задає сам клас.
 *
 * {@code @Document} позначає клас як кандидата на відображення в колекцію MongoDB [джерело].
 * {@code @Id} позначає поле ідентифікатора (мапиться на {@code _id} документа).
 *
 * productId зв'язує відгук із товаром у PostgreSQL (це НЕ зовнішній ключ БД — просто поле;
 * узгодженість між двома сховищами забезпечує застосунок — приклад polyglot persistence).
 */
@Document("reviews")
public class Review {

    @Id
    private String id;

    /** id товару (з PostgreSQL), якого стосується відгук. */
    private Long productId;

    /** Автор відгуку (ім'я, яке ввів користувач). */
    private String author;

    /** Оцінка 1..5 зірок. */
    private int rating;

    /** Текст відгуку. */
    private String text;

    /** Коли створено (для сортування «найновіші спершу»). */
    private Instant createdAt;

    public Review() {
    }

    public Review(String id, Long productId, String author, int rating, String text, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.author = author;
        this.rating = rating;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
