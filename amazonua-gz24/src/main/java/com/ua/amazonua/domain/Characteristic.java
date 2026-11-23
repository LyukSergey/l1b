package com.ua.amazonua.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Одна характеристика товару: назва + значення (напр. "Колір" → "Чорний").
 *
 * ШАР: domain. @Embeddable — вбудовуваний тип: не має власної таблиці/id, а зберігається
 * у складі колекції товару (@ElementCollection у {@link Product}) як рядки таблиці
 * product_characteristics. Колонку value названо char_value, бо VALUE — зарезервоване слово SQL.
 */
@Embeddable
public class Characteristic {

    @Column(name = "char_name")
    private String name;

    @Column(name = "char_value")
    private String value;

    public Characteristic() {
    }

    public Characteristic(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
