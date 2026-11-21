package com.ua.amazonua;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Testcontainers-конфігурація для integration-тестів (заняття 2/1).
 *
 * Піднімає РЕАЛЬНИЙ PostgreSQL у Docker-контейнері на час тестів. Анотація
 * {@code @ServiceConnection} сама прокидає url/username/password контейнера у
 * datasource застосунку — ручна конфігурація не потрібна. Контейнер стартує/
 * зупиняється автоматично.
 *
 * Потрібен запущений Docker (Rancher/Docker Desktop). Образ той самий, що й у
 * docker-compose (postgres:16-alpine), щоб тести йшли проти тієї ж версії, що й прод.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16-alpine");
    }
}
