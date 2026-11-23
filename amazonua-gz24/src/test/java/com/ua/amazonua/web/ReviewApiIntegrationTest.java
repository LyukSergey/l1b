package com.ua.amazonua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.amazonua.AbstractIntegrationTest;
import com.ua.amazonua.repository.ProductRepository;
import com.ua.amazonua.repository.mongo.ReviewRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * END-TO-END тест відгуків (заняття 2/4): весь застосунок із РЕАЛЬНИМИ PostgreSQL і MongoDB
 * (singleton-контейнери з {@link AbstractIntegrationTest}). Нічого не мокано.
 *
 * АВТОНОМІЯ: перед кожним тестом чистимо ОБИДВА сховища (товари в Postgres і відгуки в Mongo),
 * кожен тест сам створює потрібні дані.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReviewApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository products;

    @Autowired
    private ReviewRepository reviews;

    @BeforeEach
    void clear() {
        products.deleteAll();   // PostgreSQL
        reviews.deleteAll();    // MongoDB
    }

    private long givenProduct() throws Exception {
        ProductRequest request = new ProductRequest(
                "Навушники", new BigDecimal("1299.00"), "/images/headphones.svg",
                List.of("/images/headphones.svg"), "опис", "Аудіо",
                List.of(new CharacteristicDto("Колір", "Чорний")));
        String body = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    private void addReview(long productId, String author, int rating, String text) throws Exception {
        String json = """
                { "author": "%s", "rating": %d, "text": "%s" }
                """.formatted(author, rating, text);
        mockMvc.perform(post("/api/products/" + productId + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void addAndListReviews_throughMongo() throws Exception {
        long id = givenProduct();

        addReview(id, "Олена", 5, "Чудові навушники!");
        addReview(id, "Ігор", 4, "Добрий звук, але довго заряджаються.");

        mockMvc.perform(get("/api/products/" + id + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                // найновіші спершу → останній доданий ("Ігор") на першому місці
                .andExpect(jsonPath("$[0].author").value("Ігор"))
                .andExpect(jsonPath("$[0].rating").value(4))
                .andExpect(jsonPath("$[1].author").value("Олена"));
    }

    @Test
    void addReview_toMissingProduct_returns404() throws Exception {
        String json = """
                { "author": "Хтось", "rating": 5, "text": "..." }
                """;
        mockMvc.perform(post("/api/products/999999/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void addReview_invalidRating_returns400() throws Exception {
        long id = givenProduct();
        String json = """
                { "author": "", "rating": 9, "text": "" }
                """;
        mockMvc.perform(post("/api/products/" + id + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reviews_areIsolatedPerProduct() throws Exception {
        long a = givenProduct();
        long b = givenProduct();
        addReview(a, "Автор A", 5, "відгук A");

        mockMvc.perform(get("/api/products/" + b + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
