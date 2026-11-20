package com.ua.amazonua.service;

import com.ua.amazonua.domain.Product;
import com.ua.amazonua.exception.ProductNotFoundException;
import com.ua.amazonua.repository.ProductRepository;
import com.ua.amazonua.service.mapper.ProductMapper;
import com.ua.amazonua.web.dto.ProductCardDto;
import com.ua.amazonua.web.dto.ProductDetailDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реалізація {@link ProductService}.
 *
 * ШАР: service (реалізація). Залежить від АБСТРАКЦІЙ:
 *  - {@link ProductRepository} (інтерфейс сховища) — DIP,
 *  - {@link ProductMapper} (мапінг у DTO) — SRP.
 * Залежності приходять через конструктор (IoC/DI).
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ProductCardDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toCard)
                .toList();
    }

    @Override
    public ProductDetailDto findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return mapper.toDetail(product);
    }
}
