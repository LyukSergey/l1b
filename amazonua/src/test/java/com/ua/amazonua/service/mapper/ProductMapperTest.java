package com.ua.amazonua.service.mapper;

import com.ua.amazonua.domain.Characteristic;
import com.ua.amazonua.domain.Product;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** UNIT-тест мапера domain → DTO. */
class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    private Product sample() {
        return new Product(1L, "Миша", new BigDecimal("749.00"), "/images/m.svg",
                List.of("/images/m.svg"), "Опис",
                List.of(new Characteristic("Вага", "59 г")));
    }

    @Test
    void toCard_takesOnlyCardFields() {
        ProductCardDto card = mapper.toCard(sample());

        assertThat(card.id()).isEqualTo(1L);
        assertThat(card.name()).isEqualTo("Миша");
        assertThat(card.price()).isEqualByComparingTo("749.00");
        assertThat(card.imageUrl()).isEqualTo("/images/m.svg");
    }

    @Test
    void toDetail_copiesImagesAndCharacteristics() {
        ProductDetailDto detail = mapper.toDetail(sample());

        assertThat(detail.images()).containsExactly("/images/m.svg");
        assertThat(detail.description()).isEqualTo("Опис");
        assertThat(detail.characteristics()).hasSize(1);
        assertThat(detail.characteristics().get(0).value()).isEqualTo("59 г");
    }
}
