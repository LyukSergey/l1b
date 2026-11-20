# Приклади запитів — Заняття 1/4 (обробка помилок)

Бек має бути запущений: `cd amazonua-gz14 && ./mvnw spring-boot:run` (порт 8080, JDK 21).

Заняття про тестування; у коді з'явилась глобальна обробка помилок. Нижче — запити, що
демонструють єдиний формат відповіді для `404` та `400`.

---

## 1. Товар не знайдено → 404 зі структурованим тілом

```bash
curl -i http://localhost:8080/api/products/999
```

Очікувано: `HTTP/1.1 404` і тіло:
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Товар з id=999 не знайдено",
  "details": []
}
```

## 2. Невалідне тіло запиту → 400 з переліком проблем

Порожня назва й від'ємна ціна порушують правила валідації:

```bash
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{ "name": "", "price": -5, "category": "" }'
```

Очікувано: `HTTP/1.1 400` і тіло:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Помилка валідації вхідних даних",
  "details": [
    "name: назва обов'язкова",
    "price: ціна має бути додатною",
    "category: категорія обов'язкова"
  ]
}
```

## 3. Валідне створення → 201 (для порівняння)

```bash
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{ "name": "Коврик", "price": 299.00, "category": "Аксесуари",
        "imageUrl": "/images/mouse.svg", "images": ["/images/mouse.svg"],
        "description": "Ігровий килимок", "characteristics": [] }'
```

Очікувано: `HTTP/1.1 201` і JSON створеного товару.
