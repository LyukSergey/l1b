package com.ua.amazonua.service;

import com.ua.amazonua.domain.Characteristic;
import com.ua.amazonua.domain.Product;
import com.ua.amazonua.exception.ProductNotFoundException;
import com.ua.amazonua.repository.ProductRepository;
import com.ua.amazonua.service.mapper.ProductMapper;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * UNIT-тест сервісу: репозиторій замокано, маппер справжній.
 * Перевіряємо лише логіку ProductServiceImpl. (Повніше про тести — на 1/4.)
 */
class ProductServiceImplTest {

    private final ProductRepository repository = mock(ProductRepository.class);
    private final ProductMapper mapper = new ProductMapper();
    private final ProductService service = new ProductServiceImpl(repository, mapper);

    private Product sampleProduct() {
        return new Product(
                1L, "Навушники", new BigDecimal("1299.00"),
                "/images/h.svg",
                List.of("/images/h.svg", "/images/h2.svg"),
                "Опис",
                List.of(new Characteristic("Колір", "Чорний"))
        );
    }

    @Test
    void findAll_mapsProductsToCards() {
        when(repository.findAll()).thenReturn(List.of(sampleProduct()));

        List<ProductCardDto> cards = service.findAll();

        assertThat(cards).hasSize(1);
        ProductCardDto card = cards.get(0);
        assertThat(card.id()).isEqualTo(1L);
        assertThat(card.name()).isEqualTo("Навушники");
        assertThat(card.imageUrl()).isEqualTo("/images/h.svg");
    }

    @Test
    void findById_returnsDetailWithImagesAndCharacteristics() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleProduct()));

        ProductDetailDto detail = service.findById(1L);

        assertThat(detail.id()).isEqualTo(1L);
        assertThat(detail.images()).hasSize(2);
        assertThat(detail.description()).isEqualTo("Опис");
        assertThat(detail.characteristics()).hasSize(1);
        assertThat(detail.characteristics().get(0).name()).isEqualTo("Колір");
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
