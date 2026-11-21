package com.ua.amazonua;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Перевіряє, що контекст застосунку піднімається. Заняття 2/1: контекст потребує БД,
 * тому клас успадковує {@link AbstractIntegrationTest} — спільний singleton-контейнер
 * PostgreSQL (Testcontainers) на весь тестовий прогін.
 */
@SpringBootTest
class AmazonuaApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
    }

}
