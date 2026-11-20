# Приклади запитів — Заняття 1/2 (повний CRUD)

Бек має бути запущений: `cd amazonua-gz12 && ./mvnw spring-boot:run` (порт 8080, JDK 21).

На 1/2 — усі шість операцій: перелік, фільтр за категорією, один товар, створення,
оновлення, видалення.

---

## 1. Перелік товарів

```bash
curl http://localhost:8080/api/products
```

## 2. Перелік із фільтром за категорією

```bash
curl "http://localhost:8080/api/products?category=Периферія"
```
Поверне лише товари цієї категорії.

## 3. Один товар з деталями

```bash
curl http://localhost:8080/api/products/1
```

## 4. Створити товар (POST → 201 Created)

```bash
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Веб-камера StreamCam",
    "price": 2100.00,
    "imageUrl": "/images/monitor.svg",
    "images": ["/images/monitor.svg"],
    "description": "Full HD веб-камера з автофокусом.",
    "category": "Периферія",
    "characteristics": [
      { "name": "Роздільність", "value": "1080p" },
      { "name": "Частота", "value": "60 к/с" }
    ]
  }'
```
Очікувано: `HTTP/1.1 201` і JSON створеного товару з призначеним `id`.

## 5. Оновити товар (PUT → 200 OK)

```bash
curl -i -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Навушники AudioPro X2",
    "price": 1499.00,
    "imageUrl": "/images/headphones.svg",
    "images": ["/images/headphones.svg"],
    "description": "Оновлена модель навушників.",
    "category": "Аудіо",
    "characteristics": [ { "name": "Колір", "value": "Білий" } ]
  }'
```
Очікувано: `HTTP/1.1 200` і JSON оновленого товару.

## 6. Оновити неіснуючий товар → 404

```bash
curl -i -X PUT http://localhost:8080/api/products/999 \
  -H "Content-Type: application/json" \
  -d '{ "name": "X", "price": 1, "category": "Y" }'
```
Очікувано: `HTTP/1.1 404`.

## 7. Видалити товар (DELETE → 204 No Content)

```bash
curl -i -X DELETE http://localhost:8080/api/products/2
```
Очікувано: `HTTP/1.1 204` (без тіла).

## 8. Видалити неіснуючий товар → 404

```bash
curl -i -X DELETE http://localhost:8080/api/products/999
```
Очікувано: `HTTP/1.1 404`.
