package com.ua.amazonua;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Базовий клас для інтеграційних тестів (@SpringBootTest) із реальними PostgreSQL і MongoDB.
 *
 * SINGLETON-контейнери: по ОДНОМУ статичному контейнеру на ВЕСЬ тестовий прогін (не на кожен
 * тест-клас). Чому саме так:
 *  - кожен @SpringBootTest-клас інакше піднімав би СВОЇ контейнери на нових портах;
 *  - з вимкненим Ryuk (Rancher) і reuse це спричиняло конфлікти портів між контекстами
 *    ("Connection to localhost:NNNNN refused");
 *  - спільні контейнери прибирають ці конфлікти й пришвидшують тести (БД стартують раз).
 *
 * Заняття 2/4: окрім Postgres (товари) додано MongoDB (відгуки) — polyglot persistence.
 * Контейнери статичні, стартуються вручну, а @DynamicPropertySource прокидає їх адреси в
 * конфіг. JVM зупиняє контейнери сама при завершенні (між класами не зупиняємо — спільні).
 */
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static final MongoDBContainer MONGO =
            new MongoDBContainer("mongo:7");

    static {
        POSTGRES.start();   // один раз на весь прогін; спільні для всіх підкласів
        MONGO.start();
    }

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }
}
