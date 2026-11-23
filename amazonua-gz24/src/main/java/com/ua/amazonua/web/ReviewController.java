package com.ua.amazonua.web;

import com.ua.amazonua.service.ReviewService;
import com.ua.amazonua.web.dto.ReviewDto;
import com.ua.amazonua.web.dto.ReviewRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API відгуків на товар (заняття 2/4).
 *
 * ШАР: web. Відгуки — під-ресурс товару:
 *   GET  /api/products/{id}/reviews  → список відгуків товару (найновіші спершу)
 *   POST /api/products/{id}/reviews  → додати відгук                 (201 Created)
 *
 * Залежить лише від {@link ReviewService} (інтерфейсу) — DIP. Самі відгуки лежать у MongoDB,
 * але контролер про це не знає.
 */
@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewDto> list(@PathVariable Long productId) {
        return reviewService.findForProduct(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto add(@PathVariable Long productId, @Valid @RequestBody ReviewRequest request) {
        return reviewService.add(productId, request);
    }
}
