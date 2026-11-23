package com.ua.amazonua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.amazonua.AbstractIntegrationTest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
 * Заняття 2/1: тест іде проти РЕАЛЬНОГО PostgreSQL у контейнері. Контейнер — спільний
 * singleton із {@link AbstractIntegrationTest} (один на весь прогін). Контролер і сервіс
 * НЕ змінилися порівняно з in-memory — наочна інверсія залежностей (DIP): змінилась лише
 * реалізація репозиторію. Slice-тест (@WebMvcTest) базу не покриває, тому лишається окремо.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductApiIntegrationTest extends AbstractIntegrationTest {

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
        return givenProduct(name, category, new BigDecimal("100.00"));
    }

    private long givenProduct(String name, String category, BigDecimal price) throws Exception {
        ProductRequest request = new ProductRequest(
                name, price, "/images/mouse.svg",
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
    void filter_byCategoryPriceAndSearch_throughRealDb() throws Exception {
        // тест сам готує набір даних із різними категоріями/цінами/назвами
        givenProduct("Навушники AudioPro", "Аудіо", new BigDecimal("1299.00"));
        givenProduct("Клавіатура MechKey", "Периферія", new BigDecimal("1890.00"));
        givenProduct("Миша GlideMouse", "Периферія", new BigDecimal("749.00"));

        // фільтр за категорією (точна назва)
        mockMvc.perform(get("/api/products").param("category", "Периферія"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // фільтр за категорією ЧАСТКОВО (wildcard %пери%) — теж знаходить "Периферія"
        mockMvc.perform(get("/api/products").param("category", "пери"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // діапазон ціни (1000..2000) → лишаються навушники і клавіатура
        mockMvc.perform(get("/api/products").param("minPrice", "1000").param("maxPrice", "2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // текстовий пошук LIKE за назвою
        mockMvc.perform(get("/api/products").param("q", "миша"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Миша GlideMouse"));

        // комбінація: категорія + пошук
        mockMvc.perform(get("/api/products").param("category", "Периферія").param("q", "клав"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Клавіатура MechKey"));
    }

    @Test
    void search_like_isSubstringOnly_notSmart() throws Exception {
        // ДЕМОНСТРАЦІЯ межі LIKE: він шукає ЛИШЕ підрядок буква-в-букву.
        givenProduct("Навушники бездротові", "Аудіо");

        // точний підрядок — знайдено
        mockMvc.perform(get("/api/products").param("q", "бездрот"))
                .andExpect(jsonPath("$.length()").value(1));

        // друкарська помилка ("наушники") — LIKE НЕ знаходить (немає розуміння словоформ/помилок)
        mockMvc.perform(get("/api/products").param("q", "наушники"))
                .andExpect(jsonPath("$.length()").value(0));
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
                .andExpect(jsonPath("$.category").value("Аудіо"))   // категорія тепер у деталях (для редагування в UI)
                .andExpect(jsonPath("$.characteristics").isArray())
                .andExpect(jsonPath("$.images").isArray());
    }

    @Test
    void categories_returnsDistinctSorted() throws Exception {
        givenProduct("A", "Периферія");
        givenProduct("B", "Аудіо");
        givenProduct("C", "Периферія");   // дубль категорії

        mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))          // унікальні
                .andExpect(jsonPath("$[0]").value("Аудіо"))          // відсортовано
                .andExpect(jsonPath("$[1]").value("Периферія"));
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

    // --- Заняття 2/2: пагінація і сортування (крізь контролер → сервіс → реальний репозиторій) ---

    @Test
    void page_returnsRequestedSliceAndTotals() throws Exception {
        // тест сам створює рівно 5 товарів однієї категорії
        for (int i = 1; i <= 5; i++) {
            givenProduct("Товар " + i, "Гаджети", new BigDecimal(i * 100));
        }

        // друга сторінка по 2 елементи (page=1, size=2) → лишається 2 елементи, усього 5/3 сторінки
        mockMvc.perform(get("/api/products/page")
                        .param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void page_sortByPriceDesc_ordersAcrossRealDb() throws Exception {
        givenProduct("Дешевий", "Гаджети", new BigDecimal("100.00"));
        givenProduct("Дорогий", "Гаджети", new BigDecimal("900.00"));
        givenProduct("Середній", "Гаджети", new BigDecimal("500.00"));

        // сортування задає Pageable через ?sort=price,desc — найдорожчий має бути першим
        mockMvc.perform(get("/api/products/page").param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Дорогий"))
                .andExpect(jsonPath("$.content[2].name").value("Дешевий"));
    }

    @Test
    void page_appliesFilterTogetherWithPaging() throws Exception {
        givenProduct("Навушники", "Аудіо", new BigDecimal("1200.00"));
        givenProduct("Клавіатура", "Периферія", new BigDecimal("1800.00"));
        givenProduct("Миша", "Периферія", new BigDecimal("700.00"));

        // фільтр за категорією + пагінація разом
        mockMvc.perform(get("/api/products/page")
                        .param("category", "Периферія").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    // --- Заняття 2/2 (оглядово): VIEW і збережена функція на реальному PostgreSQL ---

    @Test
    void discountedView_returnsProductsBelowThreshold() throws Exception {
        // подання products_discounted: price < 1500
        givenProduct("Дешева миша", "Периферія", new BigDecimal("749.00"));
        givenProduct("Дорогий монітор", "Монітори", new BigDecimal("8499.00"));

        mockMvc.perform(get("/api/products/discounted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Дешева миша"));
    }

    @Test
    void countFunction_returnsCountForCategory() throws Exception {
        // збережена функція count_products_by_category рахує в самій БД
        givenProduct("A", "Периферія");
        givenProduct("B", "Периферія");
        givenProduct("C", "Аудіо");

        mockMvc.perform(get("/api/products/count").param("category", "Периферія"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }
}
