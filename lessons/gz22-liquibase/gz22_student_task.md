# Самостійна робота до заняття 2.2 — міграції схеми з Liquibase (лише backend)

## Мета

Перевести ведення схеми бази даних у **власному проєкті** з генерації Hibernate (`ddl-auto`)
на **міграції Liquibase** (схему та початкові дані створюють версіоновані файли змін, а
Hibernate лише перевіряє відповідність — `validate`) і додати **пагінацію та сортування**
переліку. Лише backend, у **власному репозиторії**. Наскрізний приклад тут — книгарня
(сутність `Book`).

## Бали

Заняття **2/2 (ГЗ)** — максимум **4 бали**.

Бали виставляються за результатами **усного опитування викладача** — **4 питання по 1 балу**
за кожне. Питання стосуються **пройденого на занятті матеріалу та цієї самостійної роботи**,
наприклад: чому міграції кращі за `ddl-auto` у проді; що таке changelog, changeset і таблиці
`DATABASECHANGELOG` / `DATABASECHANGELOGLOCK`; що робить режим `ddl-auto: validate`; що таке
`Pageable`/`Page` і як параметри `?page=&size=&sort=` керують сторінкою; що станеться, якщо
змінити вже застосований changeSet (контрольна сума).

## Передумови

Виконана самостійна робота до 2.1: ваша сутність уже зберігається в PostgreSQL через JPA,
БД піднімається з Docker. Ця робота — приріст до того проєкту.

## Що має вийти

![Набір змін Liquibase для вашого проєкту](./media-student/changelog-structure.svg)

---

## Крок 1. Додати залежність Liquibase

У `pom.xml` додайте (версію не вказуйте — нею керує Spring Boot):
```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```
Для Spring Boot 3.4 це саме `liquibase-core` (окремий `spring-boot-starter-liquibase` —
лише з 3.5).

## Крок 2. Переключити Hibernate на `validate`

У `application.yaml`:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate      # було update; тепер схему веде Liquibase
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
```

## Крок 3. Створити головний файл набору змін

Файл `src/main/resources/db/changelog/db.changelog-master.yaml`, що лише підключає зміни:
```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/001-create-books.yaml
  - include:
      file: db/changelog/changes/002-seed-books.yaml
```

## Крок 4. Міграція створення таблиці (`createTable`)

Файл `.../changes/001-create-books.yaml`. ПІДКАЗКА: типи й імена колонок мають збігатися з
тим, що очікує Hibernate для вашої сутності (інакше `validate` не пройде й застосунок не
стартує). Скелет (доповніть своїми полями):
```yaml
databaseChangeLog:
  - changeSet:
      id: 001-create-books
      author: <ваш-логін>
      changes:
        - createTable:
            tableName: books
            columns:
              - column:
                  name: id
                  type: bigint
                  autoIncrement: true
                  constraints: { primaryKey: true, nullable: false }
              - column:
                  name: title
                  type: varchar(255)
                  constraints: { nullable: false }
              # price (numeric), author, year ... — ваші поля
```
Якщо є колекції (`@ElementCollection`) — додайте їх таблиці та `addForeignKeyConstraint`
(як на занятті для `product_images` / `product_characteristics`).

## Крок 5. Міграція початкових даних (`insert`) — ваш тестовий датасет

Файл `.../changes/002-seed-books.yaml`. Тут ви створюєте **власний набір тестових даних**.
Один запис = один блок `insert` із усіма полями вашої сутності. Повний приклад одного рядка:
```yaml
      changes:
        - insert:
            tableName: books
            columns:
              - column: {name: id, valueNumeric: 1}
              - column: {name: title, value: "Кобзар"}
              - column: {name: author, value: "Тарас Шевченко"}
              - column: {name: price, valueNumeric: 250.00}
              - column: {name: year, valueNumeric: 1840}
        # ... наступні книги — такі самі блоки insert з id 2, 3, 4 ...
```

**ПІДКАЗКА — як легко зробити БАГАТО записів** (щоб пагінація реально працювала, потрібно
більше за розмір сторінки, напр. 15–30):
- скопіюйте блок `insert` потрібну кількість разів, міняючи лише `id` і значення полів; **або**
- згенеруйте YAML одним рядком у терміналі (підставте свої поля):
  ```bash
  for i in $(seq 1 20); do printf '        - insert:\n            tableName: books\n            columns:\n              - column: {name: id, valueNumeric: %s}\n              - column: {name: title, value: "Книга %s"}\n              - column: {name: author, value: "Автор %s"}\n              - column: {name: price, valueNumeric: %s.00}\n' "$i" "$i" "$i" "$((100 + i * 20))"; done
  ```
  Вивід вставте у `changes:` вашого changeSet.

**ПІДКАЗКА — зсув лічильника id.** Якщо задаєте `id` явно, наприкінці changeSet зсуньте
лічильник автоінкремента на `(останній id + 1)`, щоб перший запис через `POST` не отримав
уже зайнятий `id` (напр. після 20 записів):
```yaml
        - sql:
            sql: "ALTER TABLE books ALTER COLUMN id RESTART WITH 21"
```

**ПІДКАЗКА — колекції.** Якщо у вашій сутності є `@ElementCollection` (напр. список тегів або
зображень), для кожного запису додайте й рядки у відповідну таблицю колекції (з тим самим
`product_id`/`book_id` та `position`) — як на занятті для `product_images`.

## Крок 6. Перевірити на чистій базі

```bash
docker compose down -v        # видалити том
docker compose up -d
./mvnw spring-boot:run
```
У логах Liquibase — `ChangeSet ... ran successfully`. Переконайтеся:
```bash
docker exec -it <ваш-postgres> psql -U <user> -d <db> \
  -c "SELECT id, author, filename FROM databasechangelog ORDER BY orderexecuted;"
```

## Крок 7. Додати другий changeSet і дослідити контрольну суму

1. Додайте окремий changeSet (новий `id` changeSet'а), що вставляє ще 2–3 записи
   (з наступними вільними `id` рядків і новим `RESTART WITH`); перезапустіть — застосується
   лише він, старі пропустяться.
2. Спробуйте **змінити вже застосований** changeSet (напр. правку в seed) і перезапустити.
   Опишіть у README, що станеться (помилка невідповідності контрольної суми checksum) і
   чому так: застосовані зміни редагувати не можна — лише додавати нові.

## Крок 8. Додати пагінацію і сортування

ПІДКАЗКА (факти звірено з документацією Spring Data [1][2]): метод репозиторію приймає
`Pageable` і повертає `Page<T>`; контролер приймає `Pageable` параметром методу, а Spring
збирає його з параметрів запиту — `page` (з 0, типово 0), `size` (типово 20),
`sort=property,asc|desc`. Скелет (без тіла — реалізуєте самі):
```java
// репозиторій
Page<Book> findAll(Pageable pageable);          // або власний @Query + countQuery

// контролер
@GetMapping("/page")
PageDto<BookCardDto> page(Pageable pageable) { /* делегувати в сервіс, змапити вміст */ }
```
Назовні краще віддавати власний вузький `PageDto` (content + page/size/totalElements/totalPages),
а не Spring-тип `Page` цілком. Перевірте: `GET /api/books/page?size=2&sort=price,desc`.

Щоб пагінація була помітна, потрібно більше записів, ніж розмір сторінки. Якщо на Кроці 5 ви
вже зробили 15–30 записів — цього досить. Якщо ні — додайте їх **окремим** seed-changeSet'ом
(не правкою вже застосованого; див. підказку про генерацію YAML у Кроці 5).

---

## Що здати

1. У **вашому** репозиторії — підключений Liquibase, `ddl-auto: validate`.
2. Міграція `createTable` вашої таблиці (+ таблиці колекцій, якщо є) і міграція `insert`.
3. Другий changeSet, що додає ще кілька записів.
4. Пагінований ендпоінт переліку (`Pageable`/`Page`) із сортуванням `?sort=`.
5. У README — опис поведінки при зміні вже застосованого changeSet (контрольна сума).

## Критерії

- На чистій базі (`down -v` → `up`) схема й дані створюються міграціями самі.
- `ddl-auto: validate`; застосунок стартує (схема збігається із сутностями).
- Є щонайменше два окремі changeSet'и; повторний старт не застосовує їх удруге.
- Перелік віддає сторінку (`Page`/`PageDto`) із метаданими; сортування `?sort=` працює.
- Предметна область власна (поля відрізняються від еталона).

## Джерела

1. Spring Data JPA Documentation. *Paging and Sorting (Pageable, Page).* https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html
2. Spring Data JPA Documentation. *Web support — Pageable request parameters.* https://docs.spring.io/spring-data/jpa/reference/repositories/core-extensions.html
