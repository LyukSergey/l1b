# План SVG — заняття 2/4 (FTS + MongoDB)

| Файл | Розділ README | Що зображує | Пріоритет |
|---|---|---|---|
| `fts-pipeline.svg` | «Повнотекстовий пошук» | Текст → to_tsvector (tsvector у колонці + GIN-індекс); запит → plainto_tsquery; оператор @@ + ts_rank → відсортовані результати | 1 |
| `polyglot-persistence.svg` | «MongoDB — коли і як» | Застосунок → Postgres (products: таблиця зі схемою) + MongoDB (reviews: гнучкі документи). Чому різні дані в різних сховищах | 1 |
| `reviews-flow.svg` | «Відгуки через шари» | POST /{id}/reviews → Controller → ReviewService (перевірка товару в Postgres) → ReviewRepository (Mongo) → документ | 2 |

Палітра — svg-rules. Postgres/Mongo компоненти — сині блоки; наш код — білі з обведенням.
Стрілки вертикальні/горизонтальні, влучають у центр блоків.
