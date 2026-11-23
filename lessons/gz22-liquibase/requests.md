# Приклади запитів — заняття 2.2 (Liquibase)

Контракти API не змінилися з 2.1 — змінився лише спосіб ведення схеми (тепер міграції
Liquibase замість `ddl-auto`). Нижче — перевірка роботи міграцій і звичайні запити до API.

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
# → 4 стартові товари, вставлені changeset'ом 002-seed-products
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
# → 201 Created; новий id = 5 (а не 1), бо міграція зсунула IDENTITY після seed
```

---

## 4. Переконатися, що схему й дані створив Liquibase

Журнал застосованих змін (таблиця `DATABASECHANGELOG`):
```bash
docker exec -it amazonua-postgres psql -U amazonua -d amazonua \
  -c "SELECT id, author, filename, orderexecuted FROM databasechangelog ORDER BY orderexecuted;"
# → рядки 001-create-products і 002-seed-products
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
curl http://localhost:8080/api/products   # знову 4 стартові товари
```
