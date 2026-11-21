package com.ua.amazonua;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Перевіряє, що контекст застосунку піднімається. Заняття 2/1: контекст тепер
 * потребує БД, тому підключаємо Testcontainers (реальний PostgreSQL у контейнері).
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AmazonuaApplicationTests {

    @Test
    void contextLoads() {
    }

}
