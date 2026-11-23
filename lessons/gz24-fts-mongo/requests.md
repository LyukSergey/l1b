# Приклади запитів — заняття 2.4 (FTS + MongoDB)

Передумови: піднята БД і застосунок.
```bash
cd amazonua-gz24
docker compose up -d          # PostgreSQL + MongoDB
./mvnw spring-boot:run
```

---

## 1. Повнотекстовий пошук (FTS)

```bash
# пошук за словом (не підрядком) — із ранжуванням за релевантністю
curl "http://localhost:8080/api/products/search?q=навушники"

# кілька слів — plainto_tsquery ставить між ними AND (мають бути обидва)
curl "http://localhost:8080/api/products/search?q=навушники%20бездротові"
```

Порівняння зі старим LIKE-фільтром (заняття 2.1) — той самий текст, різний механізм:
```bash
curl "http://localhost:8080/api/products?q=навушники"        # LIKE: підрядок
curl "http://localhost:8080/api/products/search?q=навушники" # FTS: за словами + релевантність
```

Перевірити search_vector напряму в БД:
```bash
docker exec -it amazonua-postgres psql -U amazonua -d amazonua \
  -c "SELECT id, name, search_vector FROM products LIMIT 3;"
```

---

## 2. Відгуки на товар (MongoDB)

```bash
# відгуки товару (найновіші спершу) — порожньо, якщо ще немає
curl http://localhost:8080/api/products/1/reviews

# додати відгук (201 Created)
curl -i -X POST http://localhost:8080/api/products/1/reviews \
  -H "Content-Type: application/json" \
  -d '{ "author": "Олена", "rating": 5, "text": "Чудові навушники!" }'

# ще один
curl -s -X POST http://localhost:8080/api/products/1/reviews \
  -H "Content-Type: application/json" \
  -d '{ "author": "Ігор", "rating": 4, "text": "Добрий звук." }'

# тепер список містить обидва (новіший — першим)
curl http://localhost:8080/api/products/1/reviews
```

Межі:
```bash
# відгук до неіснуючого товару → 404
curl -i -X POST http://localhost:8080/api/products/999999/reviews \
  -H "Content-Type: application/json" \
  -d '{ "author": "Хтось", "rating": 5, "text": "..." }'

# некоректний рейтинг / порожні поля → 400
curl -i -X POST http://localhost:8080/api/products/1/reviews \
  -H "Content-Type: application/json" \
  -d '{ "author": "", "rating": 9, "text": "" }'
```

Подивитися відгуки напряму в MongoDB:
```bash
docker exec -it amazonua-mongo mongosh amazonua \
  --eval 'db.reviews.find().sort({createdAt:-1}).pretty()'
```

---

## 3. Чиста база (з нуля)

```bash
docker compose down -v        # видалити томи Postgres і Mongo
docker compose up -d
./mvnw spring-boot:run        # Liquibase накотить схему + FTS; Mongo-колекція — на льоту
```
