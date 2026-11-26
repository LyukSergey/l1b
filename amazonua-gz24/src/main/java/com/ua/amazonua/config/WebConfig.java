package com.ua.amazonua.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Веб-конфігурація.
 *
 * CORS дозволяє React dev-серверу (Vite) ходити в наш API. Vite за замовчуванням на :5173,
 * але якщо порт зайнятий — бере наступний вільний (5174, 5175…). Тому дозволяємо БУДЬ-ЯКИЙ
 * порт localhost через allowedOriginPatterns, а не фіксований origin: інакше при запуску UI
 * на іншому порту браузер отримує 403 на preflight-запит (OPTIONS) перед POST/PUT/DELETE.
 * OPTIONS теж у списку методів — це той самий preflight браузера. У проді (крок 6) запити
 * підуть через nginx з того ж хоста, і CORS не потрібен.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
