package com.ua.amazonua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ua.amazonua.exception.ProductNotFoundException;
import com.ua.amazonua.service.ProductService;
import com.ua.amazonua.web.dto.CharacteristicDto;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import com.ua.amazonua.web.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * INTEGRATION-тест веб-шару: піднімаємо лише контролер (@WebMvcTest),
 * сервіс (інтерфейс) замокано. Перевіряємо HTTP-статуси й JSON.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductDetailDto sampleDetail() {
        return new ProductDetailDto(1L, "Навушники", new BigDecimal("1299.00"), "/images/h.svg",
                List.of("/images/h.svg", "/images/h2.svg"), "Опис товару", "Аудіо",
                List.of(new CharacteristicDto("Колір", "Чорний")));
    }

    private ProductRequest sampleRequest() {
        return new ProductRequest("Навушники", new BigDecimal("1299.00"), "/images/h.svg",
                List.of("/images/h.svg"), "Опис", "Аудіо", List.of());
    }

    @Test
    void list_returnsProductCards() throws Exception {
        when(productService.search(null, null, null, null)).thenReturn(List.of(
                new ProductCardDto(1L, "Навушники", new BigDecimal("1299.00"), "/images/h.svg")
        ));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Навушники"));
    }

    @Test
    void list_withCategory_passesFilter() throws Exception {
        when(productService.search("Аудіо", null, null, null)).thenReturn(List.of(
                new ProductCardDto(1L, "Навушники", new BigDecimal("1299.00"), "/images/h.svg")
        ));

        mockMvc.perform(get("/api/products").param("category", "Аудіо"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Навушники"));
        verify(productService).search("Аудіо", null, null, null);
    }

    @Test
    void list_withPriceAndQuery_passesAllFilters() throws Exception {
        when(productService.search(null, new BigDecimal("500"), new BigDecimal("2000"), "нав"))
                .thenReturn(List.of(
                        new ProductCardDto(1L, "Навушники", new BigDecimal("1299.00"), "/images/h.svg")));

        mockMvc.perform(get("/api/products")
                        .param("minPrice", "500")
                        .param("maxPrice", "2000")
                        .param("q", "нав"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Навушники"));
        verify(productService).search(null, new BigDecimal("500"), new BigDecimal("2000"), "нав");
    }

    @Test
    void getOne_returnsProductDetail() throws Exception {
        when(productService.findById(1L)).thenReturn(sampleDetail());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.images.length()").value(2))
                .andExpect(jsonPath("$.characteristics[0].name").value("Колір"));
    }

    @Test
    void getOne_returns404WithErrorBody() throws Exception {
        when(productService.findById(999L)).thenThrow(new ProductNotFoundException(999L));

        // Заняття 1/4: 404 тепер формує GlobalExceptionHandler — зі структурованим тілом ErrorResponse.
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Товар з id=999 не знайдено"));
    }

    @Test
    void create_returns400WhenInvalid() throws Exception {
        // порожня назва + від'ємна ціна → @Valid не проходить → 400 з переліком проблем
        String invalidJson = """
                { "name": "", "price": -5, "category": "" }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void create_returns201() throws Exception {
        when(productService.create(any(ProductRequest.class))).thenReturn(sampleDetail());

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_returns200() throws Exception {
        when(productService.update(eq(1L), any(ProductRequest.class))).thenReturn(sampleDetail());

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_returns404WhenMissing() throws Exception {
        when(productService.update(eq(999L), any(ProductRequest.class)))
                .thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
        verify(productService).delete(1L);
    }

    @Test
    void delete_returns404WhenMissing() throws Exception {
        doThrow(new ProductNotFoundException(999L)).when(productService).delete(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}
