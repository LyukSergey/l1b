package com.ua.amazonua;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Інтеграційний тест, що ПЕРЕВІРЯЄ РОБОТУ LIQUIBASE (заняття 2/2).
 *
 * Схему БД тепер створює не Hibernate, а міграції Liquibase. Під час старту контексту
 * (проти реального PostgreSQL із {@link AbstractIntegrationTest}) Liquibase застосовує
 * набір змін і фіксує кожен changeSet у службовій таблиці DATABASECHANGELOG. Нижче
 * перевіряємо саме це: таблиця-журнал існує, обидва наші changeSet'и в ній записані,
 * а створені міграцією таблиці реально доступні застосунку.
 *
 * Запитів до застосунку тут не робимо — звертаємось напряму до БД через {@link JdbcTemplate},
 * бо предмет перевірки — факт і результат міграції, а не HTTP-контракт.
 */
@SpringBootTest
class LiquibaseMigrationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void changelogTable_recordsOurChangeSets() {
        // DATABASECHANGELOG — журнал Liquibase: рядок на кожен застосований changeSet.
        List<String> appliedIds = jdbc.queryForList(
                "SELECT id FROM databasechangelog ORDER BY orderexecuted", String.class);

        assertThat(appliedIds).contains("001-create-products", "002-seed-products");
    }

    @Test
    void schemaCreatedByMigration_tablesExist() {
        // Таблиці, створені міграцією 001, мають існувати й читатися.
        assertThat(tableExists("products")).isTrue();
        assertThat(tableExists("product_images")).isTrue();
        assertThat(tableExists("product_characteristics")).isTrue();
    }

    @Test
    void seedMigration_wasApplied() {
        // Факт застосування seed-міграції 002 фіксує журнал Liquibase. Саме кількість рядків
        // у products тут НЕ перевіряємо: інші автономні тести в тому самому прогоні викликають
        // deleteAll() на спільній БД (singleton-контейнер), тож рядки можуть бути вже очищені.
        // Журнал DATABASECHANGELOG deleteAll() не чіпає — тому перевіряємо саме його.
        Integer applied = jdbc.queryForObject(
                "SELECT count(*) FROM databasechangelog WHERE id = '002-seed-products'",
                Integer.class);
        assertThat(applied).isEqualTo(1);
    }

    private boolean tableExists(String table) {
        Integer n = jdbc.queryForObject(
                "SELECT count(*) FROM information_schema.tables "
                        + "WHERE table_schema = 'public' AND table_name = ?",
                Integer.class, table);
        return n != null && n > 0;
    }
}
