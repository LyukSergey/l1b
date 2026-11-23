# Приклади запитів і команди БД — Заняття 2/1

Спершу підняти БД і застосунок:
```bash
cd amazonua-gz21
docker compose up -d
./mvnw spring-boot:run      # під Java 21
```

Контракти API не змінилися з Модуля 1 — змінилось лише сховище (тепер PostgreSQL).

---

## 1. Перелік товарів (тепер із БД)

```bash
curl http://localhost:8080/api/products
```

## 1a. Фільтри та пошук

```bash
# фільтр за категорією (можна частину — wildcard %пери%)
curl "http://localhost:8080/api/products?category=Периферія"

# діапазон ціни
curl "http://localhost:8080/api/products?minPrice=1000&maxPrice=2000"

# текстовий пошук за назвою (примітивний LIKE)
curl "http://localhost:8080/api/products?q=миша"

# комбінація критеріїв
curl "http://localhost:8080/api/products?category=Периферія&q=клав"
```

## 1b. Унікальні категорії (для випадаючого списку фільтра)

```bash
curl http://localhost:8080/api/products/categories
# → ["Аудіо","Периферія", ...]  (унікальні, відсортовані)
```

## 2. Один товар із деталями

```bash
curl http://localhost:8080/api/products/1
# відповідь містить поле "category" — потрібне формі редагування на UI
```

## 3. Створити товар

```bash
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Веб-камера StreamCam",
    "price": 2100.00,
    "category": "Периферія",
    "imageUrl": "/images/monitor.svg",
    "images": ["/images/monitor.svg"],
    "description": "Full HD веб-камера з автофокусом.",
    "characteristics": [ { "name": "Роздільність", "value": "1080p" } ]
  }'
```
Очікувано: `HTTP/1.1 201`.

## 4. Перевірка персистентності (головне на цьому занятті)

1. Створіть товар (п. 3).
2. Зупиніть застосунок (Ctrl+C) і запустіть знову (`./mvnw spring-boot:run`).
3. Виконайте `curl http://localhost:8080/api/products` — створений товар **лишився**
   (на відміну від in-memory, де він зник би).

---

## 5. Подивитись дані напряму в БД через `psql`

Під'єднатись до Postgres у контейнері:
```bash
docker exec -it amazonua-postgres psql -U amazonua -d amazonua
```

Усередині `psql`:
```sql
-- які таблиці створив Hibernate
\dt

-- усі товари
SELECT id, name, price, category FROM products;

-- характеристики конкретного товару (окрема таблиця)
SELECT * FROM product_characteristics WHERE product_id = 1;

-- зображення (окрема таблиця)
SELECT * FROM product_images WHERE product_id = 1;

-- вийти
\q
```

---

## 6. Скинути дані (почати з чистої БД)

```bash
docker compose down -v      # видалити том разом із даними
docker compose up -d        # підняти заново — БД порожня, сидер наповнить стартовими товарами
```

---

## 7. Переключитись назад на in-memory (демонстрація DIP)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=inmemory
```
Застосунок працюватиме без БД (дані знову в пам'яті). Жоден клас, крім вибору реалізації
репозиторію, не змінюється — у цьому сенс інверсії залежностей.
