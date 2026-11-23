package com.ua.amazonua.repository.mongo;

import com.ua.amazonua.domain.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Репозиторій відгуків у MongoDB (заняття 2/4).
 *
 * ШАР: repository/mongo. {@code MongoRepository<Review, String>} дає готові CRUD-методи для
 * документів колекції {@code reviews} (реалізацію генерує Spring Data MongoDB). Тип ключа —
 * {@code String}, бо {@code _id} документа — рядок.
 *
 * {@code findByProductIdOrderByCreatedAtDesc} — похідний метод (derived query): Spring Data
 * сам будує запит за іменем методу (відгуки одного товару, найновіші спершу).
 */
public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
}
