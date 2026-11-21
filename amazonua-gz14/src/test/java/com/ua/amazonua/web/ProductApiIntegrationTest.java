package com.ua.amazonua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.amazonua.repository.ProductRepository;
import com.ua.amazonua.web.dto.CharacteristicDto;
import com.ua.amazonua.web.dto.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * END-TO-END інтеграційний тест: піднімає ВЕСЬ застосунок (@SpringBootTest) з РЕАЛЬНИМИ
 * бінами — контролер, сервіс, маппер, репозиторій. Нічого не мокаємо.
 *
 * АВТОНОМІЯ ТЕСТІВ: перед кожним тестом сховище ПОВНІСТЮ очищається (deleteAll), тому
 * кожен тест стартує з ПОРОЖНЬОГО стану і САМ готує рівно ті дані, які йому потрібні
 * (через приватний хелпер {@link #givenProduct}). Тест не покладається на глобальний
 * ProductSeeder і не "знає" про чужі дані — звідси повна незалежність від інших тестів
 * і від вмісту сіду застосунку.
 *
 * (ProductSeeder лишається для ЗАПУСКУ застосунку — щоб магазин не був порожнім, — але
 * у тестах НЕ використовується.)
 *
 * У Модулі 2, коли in-memory репозиторій заміниться на Postgres, цей тест природно
 * почне перевіряти роботу з реальною БД (через Testcontainers) без змін у контролері/
 * сервісі — демонстрація DIP. Slice-тест (@WebMvcTest) базу не покриває, тому лишається окремо.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository repository;

    /** Порожній старт: кожен тест далі сам створює потрібні йому дані. */
    @BeforeEach
    void clearState() {
        repository.deleteAll();
    }

    // --- Фабрика тестових даних: тест створює товар через API й отримує його реальний id. ---

    private long givenProduct(String name, String category) throws Exception {
        ProductRequest request = new ProductRequest(
                name, new BigDecimal("100.00"), "/images/mouse.svg",
                List.of("/images/mouse.svg"), "опис", category,
                List.of(new CharacteristicDto("Ключ", "Значення")));
        String body = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    @Test
    void getAll_returnsExactlyWhatTestCreated() throws Exception {
        // тест САМ створює рівно 2 товари — і перевіряє рівно 2 (не прив'язано до сіду)
        givenProduct("Товар A", "Категорія1");
        givenProduct("Товар B", "Категорія2");

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAll_whenEmpty_returnsEmptyList() throws Exception {
        // нічого не створюємо — старт порожній, тож перелік порожній
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getById_returnsProductWithDetails() throws Exception {
        long id = givenProduct("Навушники", "Аудіо"); // тест сам створив свій товар

        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) id))
                .andExpect(jsonPath("$.name").value("Навушники"))
                .andExpect(jsonPath("$.characteristics").isArray())
                .andExpect(jsonPath("$.images").isArray());
    }

    @Test
    void getById_missing_returns404WithErrorBody() throws Exception {
        // нічого не створюємо — будь-який id відсутній; проходить крізь реальний GlobalExceptionHandler
        mockMvc.perform(get("/api/products/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void createThenReadThenDelete_fullCycleThroughAllLayers() throws Exception {
        long id = givenProduct("Коврик для миші", "Аксесуари");

        // READ створеного — справді збережений
        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Коврик для миші"));

        // DELETE → 204, і потім 404 (реально видалено)
        mockMvc.perform(delete("/api/products/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_invalid_returns400ThroughRealValidation() throws Exception {
        String invalidJson = """
                { "name": "", "price": -5, "category": "" }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray());
    }
}
