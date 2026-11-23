# Приклади запитів — заняття 2.2 (Liquibase, пагінація, VIEW/функція)

Схему тепер веде Liquibase (замість `ddl-auto`); додано пагінований перелік і оглядові
view/функцію. Нижче — перевірка міграцій, звичайні запити та нові ендпоінти.

Передумови: піднята БД і застосунок.
```bash
cd amazonua-gz22
docker compose up -d
./mvnw spring-boot:run
```

---

## 1. Перелік товарів (створені міграцією seed)

```bash
curl http://localhost:8080/api/products
# → ~40 стартових товарів (002 + 004 + 006; усі з окремих seed-changeSet'ів)
```

## 1a. Пагінований перелік (сторінки + сортування)

```bash
# перша сторінка по 2 елементи
curl "http://localhost:8080/api/products/page?page=0&size=2"
# → { "content": [...2 товари...], "page":0, "size":2, "totalElements":40, "totalPages":20 }

# сортування за ціною спаданням
curl "http://localhost:8080/api/products/page?sort=price,desc"

# пагінація разом із фільтром за категорією
curl "http://localhost:8080/api/products/page?category=Периферія&size=10"
```

## 1b. VIEW і збережена функція (оглядово)

```bash
# товари зі знижкою — читання SQL-view products_discounted (price < 1500)
curl http://localhost:8080/api/products/discounted

# знижки СТОРІНКАМИ (серверна пагінація над view)
curl "http://localhost:8080/api/products/discounted/page?page=0&size=8"
# → PageDto { content, page, size, totalElements, totalPages }

# кількість товарів у категорії — порахована збереженою функцією в БД
curl "http://localhost:8080/api/products/count?category=Периферія"
# → число
```

## 2. Один товар із деталями

```bash
curl http://localhost:8080/api/products/1
# відповідь містить images та characteristics із таблиць-колекцій
```

## 3. Створити товар (перевірка зсуву лічильника id)

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
# → 201 Created; новий id = 41 (а не 1), бо остання seed-міграція зсунула IDENTITY (RESTART WITH 41)
```

---

## 4. Переконатися, що схему й дані створив Liquibase

Журнал застосованих змін (таблиця `DATABASECHANGELOG`):
```bash
docker exec -it amazonua-postgres psql -U amazonua -d amazonua \
  -c "SELECT id, author, filename, orderexecuted FROM databasechangelog ORDER BY orderexecuted;"
# → рядки 001-create-products, 002-seed-products, 003-view-and-function, 004-seed-more-products,
#   005-discounted-view-with-image, 006-seed-even-more-products, 007-sync-images-with-category
```

Перелік створених таблиць:
```bash
docker exec -it amazonua-postgres psql -U amazonua -d amazonua -c "\dt"
# → products, product_images, product_characteristics,
#   а також службові databasechangelog і databasechangeloglock
```

Дані seed напряму в таблиці:
```bash
docker exec -it amazonua-postgres psql -U amazonua -d amazonua \
  -c "SELECT id, name, category FROM products ORDER BY id;"
```

---

## 5. Повторний старт не застосовує міграції вдруге

```bash
# зупинити й знову запустити застосунок (БД і том лишаються)
# у логах Liquibase буде: "Database is up to date, no changesets to execute"
```

## 6. Чиста база: міграції відпрацьовують «з нуля»

```bash
docker compose down -v        # видалити том разом із даними
docker compose up -d          # нова порожня БД
./mvnw spring-boot:run        # Liquibase створює схему й вставляє seed автоматично
curl http://localhost:8080/api/products   # знову ~40 стартових товарів
```
