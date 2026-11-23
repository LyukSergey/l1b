package com.ua.amazonua;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Базовий клас для інтеграційних тестів (@SpringBootTest) із реальним PostgreSQL.
 *
 * SINGLETON-контейнер: ОДИН статичний контейнер на ВЕСЬ тестовий прогін (не на кожен
 * тест-клас). Чому саме так:
 *  - кожен @SpringBootTest-клас інакше піднімав би СВІЙ контейнер на новому порту;
 *  - з вимкненим Ryuk (Rancher) і reuse це спричиняло конфлікти портів між контекстами
 *    ("Connection to localhost:NNNNN refused");
 *  - один спільний контейнер прибирає ці конфлікти й пришвидшує тести (БД стартує раз).
 *
 * Контейнер статичний і стартується вручну (НЕ через @Container на кожен клас), а
 * @DynamicPropertySource прокидає його url/user/pass у datasource. JVM зупиняє контейнер
 * сама при завершенні (контейнер не зупиняємо між класами навмисно — він спільний).
 */
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        POSTGRES.start();   // один раз на весь прогін; спільний для всіх підкласів
    }

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
