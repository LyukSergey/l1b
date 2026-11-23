package com.ua.amazonua.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.List;

/**
 * Товар — JPA-сутність (з заняття 2/1 зберігається в PostgreSQL).
 *
 * ШАР: domain. На 1/1–1/5 це був звичайний клас у пам'яті; тепер він позначений
 * JPA-анотаціями й мапиться на таблицю {@code products}. Контракт домену (поля,
 * гетери/сетери) НЕ змінився — тому сервіс/контролер/DTO лишаються такими ж.
 *
 * @Entity вимагає конструктор без аргументів (є нижче).
 * images та characteristics — колекції значень (@ElementCollection), тобто окремі
 * таблиці, зв'язані з products зовнішнім ключем.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    /** Основне фото (на картці у списку). */
    private String imageUrl;

    /** Усі фото (галерея на сторінці товару) — окрема таблиця product_images. */
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @OrderColumn(name = "position")
    private List<String> images;

    @Column(length = 2000)
    private String description;

    /** Категорія товару (для фільтра у переліку). */
    private String category;

    /** Характеристики — окрема таблиця product_characteristics (Characteristic = @Embeddable). */
    @ElementCollection
    @CollectionTable(name = "product_characteristics", joinColumns = @JoinColumn(name = "product_id"))
    @OrderColumn(name = "position")
    private List<Characteristic> characteristics;

    public Product() {
    }

    public Product(Long id, String name, BigDecimal price, String imageUrl,
                   List<String> images, String description, String category,
                   List<Characteristic> characteristics) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.images = images;
        this.description = description;
        this.category = category;
        this.characteristics = characteristics;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<Characteristic> characteristics) {
        this.characteristics = characteristics;
    }
}
