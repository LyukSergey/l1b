package com.ua.amazonua.service;

import com.ua.amazonua.domain.Review;
import com.ua.amazonua.exception.ProductNotFoundException;
import com.ua.amazonua.repository.ProductRepository;
import com.ua.amazonua.repository.mongo.ReviewRepository;
import com.ua.amazonua.web.dto.ReviewDto;
import com.ua.amazonua.web.dto.ReviewRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Реалізація {@link ReviewService} (заняття 2/4).
 *
 * ШАР: service. Працює з ДВОМА сховищами (polyglot persistence):
 *  - {@link ReviewRepository} (MongoDB) — зберігає й читає відгуки-документи;
 *  - {@link ProductRepository} (PostgreSQL) — щоб переконатися, що товар існує, перш ніж
 *    додавати до нього відгук. Узгодженість між сховищами тут забезпечує код (а не БД):
 *    у Mongo немає зовнішнього ключа на products.
 *
 * Для MongoDB транзакцій (як у JPA) тут не використовуємо — кожна операція атомарна на рівні
 * одного документа; це типово для документної БД.
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviews;
    private final ProductRepository products;

    public ReviewServiceImpl(ReviewRepository reviews, ProductRepository products) {
        this.reviews = reviews;
        this.products = products;
    }

    @Override
    public List<ReviewDto> findForProduct(Long productId) {
        return reviews.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(ReviewServiceImpl::toDto)
                .toList();
    }

    @Override
    public ReviewDto add(Long productId, ReviewRequest request) {
        // перевіряємо наявність товару в PostgreSQL — щоб не з'явився відгук на неіснуючий товар
        products.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Review review = new Review(null, productId, request.author(), request.rating(),
                request.text(), Instant.now());
        Review saved = reviews.save(review);   // id генерує MongoDB
        return toDto(saved);
    }

    private static ReviewDto toDto(Review r) {
        return new ReviewDto(r.getId(), r.getProductId(), r.getAuthor(),
                r.getRating(), r.getText(), r.getCreatedAt());
    }
}
