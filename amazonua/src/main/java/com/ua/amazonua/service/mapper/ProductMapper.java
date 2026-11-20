package com.ua.amazonua.service.mapper;

import com.ua.amazonua.domain.Product;
import com.ua.amazonua.web.dto.CharacteristicDto;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Перетворення доменного {@link Product} у DTO.
 *
 * ШАР: service/mapper. Винесено ОКРЕМО від сервісу (SRP): сервіс відповідає
 * за бізнес-логіку, маппер — лише за перетворення форматів.
 * Коли мапінгів побільшає, тут буде одне місце для всіх.
 */
@Component
public class ProductMapper {

    public ProductCardDto toCard(Product product) {
        return new ProductCardDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
        );
    }

    public ProductDetailDto toDetail(Product product) {
        List<CharacteristicDto> characteristics = product.getCharacteristics().stream()
                .map(c -> new CharacteristicDto(c.getName(), c.getValue()))
                .toList();
        return new ProductDetailDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getImages(),
                product.getDescription(),
                characteristics
        );
    }
}
