package com.ua.amazonua.web;

import com.ua.amazonua.exception.ProductNotFoundException;
import com.ua.amazonua.service.ProductService;
import com.ua.amazonua.web.dto.CharacteristicDto;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockBean
    private ProductService productService;

    @Test
    void list_returnsProductCards() throws Exception {
        when(productService.findAll()).thenReturn(List.of(
                new ProductCardDto(1L, "Навушники", new BigDecimal("1299.00"), "/images/h.svg")
        ));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Навушники"))
                .andExpect(jsonPath("$[0].imageUrl").value("/images/h.svg"));
    }

    @Test
    void getOne_returnsProductDetail() throws Exception {
        when(productService.findById(1L)).thenReturn(new ProductDetailDto(
                1L, "Навушники", new BigDecimal("1299.00"), "/images/h.svg",
                List.of("/images/h.svg", "/images/h2.svg"),
                "Опис товару",
                List.of(new CharacteristicDto("Колір", "Чорний"))
        ));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.images.length()").value(2))
                .andExpect(jsonPath("$.description").value("Опис товару"))
                .andExpect(jsonPath("$.characteristics[0].name").value("Колір"));
    }

    @Test
    void getOne_returns404WhenMissing() throws Exception {
        when(productService.findById(999L)).thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}
