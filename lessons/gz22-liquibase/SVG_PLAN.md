# План SVG-діаграм — заняття 2/2 (Liquibase)

| Файл | Розділ README | Що зображує | Пріоритет |
|---|---|---|---|
| `ddl-auto-vs-liquibase.svg` | «Навіщо міграції» | Хто володіє схемою: БУЛО (Hibernate ddl-auto генерує схему) vs СТАЛО (Liquibase-міграції створюють, Hibernate лише validate) | 1 |
| `liquibase-startup-flow.svg` | «Як Liquibase працює при старті» | Послідовність: старт → читає master changelog → звіряє з DATABASECHANGELOG → застосовує лише нові changeSet → фіксує в журналі → Hibernate validate | 1 |
| `changelog-structure.svg` | «Структура набору змін» | master changelog → include 001-create (createTable products/images/characteristics) + 002-seed (insert) → реальні таблиці БД | 2 |

Палітра й правила — `reference/svg-rules.md`. Компоненти Liquibase/Hibernate — сині блоки,
наш код/файли — білі з обведенням. Стрілки вертикальні, влучають у центр блоків.
