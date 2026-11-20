# Приклади запитів — Заняття 1/1

Бек має бути запущений: `cd amazonua && ./mvnw spring-boot:run` (порт 8080).

На 1/1 лише **два GET-ендпоінти** (перегляд). Створення товарів — на 1/2.

---

## 1. Список товарів (картки)

```bash
curl http://localhost:8080/api/products
```

Очікувана відповідь (скорочено):
```json
[
  { "id": 1, "name": "Навушники AudioPro X1", "price": 1299.00, "imageUrl": "/images/headphones.svg" },
  { "id": 2, "name": "Клавіатура MechKey TKL", "price": 1890.50, "imageUrl": "/images/keyboard.svg" }
]
```

Красивий вивід (якщо встановлено `jq`):
```bash
curl -s http://localhost:8080/api/products | jq
```

---

## 2. Один товар з фото та деталями

```bash
curl http://localhost:8080/api/products/1
```

Очікувана відповідь:
```json
{
  "id": 1,
  "name": "Навушники AudioPro X1",
  "price": 1299.00,
  "imageUrl": "/images/headphones.svg",
  "images": ["/images/headphones.svg", "/images/headphones-2.svg"],
  "description": "Бездротові навушники з активним шумозаглушенням і автономністю до 30 годин.",
  "characteristics": [
    { "name": "Тип", "value": "Накладні" },
    { "name": "Підключення", "value": "Bluetooth 5.3" }
  ]
}
```

---

## 3. Товар, якого немає → 404

```bash
curl -i http://localhost:8080/api/products/999
```

Очікувано: перший рядок `HTTP/1.1 404`.

---

## 4. Фото віддаються як статика

```bash
curl -i http://localhost:8080/images/headphones.svg
```

Очікувано: `HTTP/1.1 200` і `Content-Type: image/svg+xml`.
