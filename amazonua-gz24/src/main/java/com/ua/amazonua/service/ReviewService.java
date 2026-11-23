package com.ua.amazonua.service;

import com.ua.amazonua.web.dto.ReviewDto;
import com.ua.amazonua.web.dto.ReviewRequest;

import java.util.List;

/**
 * Контракт сервісу відгуків (заняття 2/4).
 *
 * ШАР: service (абстракція). Контролер залежить від цього інтерфейсу — DIP.
 */
public interface ReviewService {

    /** Відгуки товару, найновіші спершу. */
    List<ReviewDto> findForProduct(Long productId);

    /** Додати відгук до товару; кидає ProductNotFoundException, якщо товару немає. */
    ReviewDto add(Long productId, ReviewRequest request);
}
