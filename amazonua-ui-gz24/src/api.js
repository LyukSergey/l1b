// Тонкий клієнт до нашого Spring Boot API.
// Шляхи відносні ('/api/...') — Vite-проксі перенаправить їх на :8080 (див. vite.config.js).

// Заняття 2/2: ПАГІНОВАНИЙ перелік. Повертає PageDto { content, page, size, totalElements, totalPages }.
// filters = { category, minPrice, maxPrice, q }; page з 0; sort — напр. 'price,asc'.
export async function fetchProductsPage(filters = {}, page = 0, size = 8, sort = '') {
  const params = new URLSearchParams()
  if (filters.category) params.set('category', filters.category)
  if (filters.minPrice) params.set('minPrice', filters.minPrice)
  if (filters.maxPrice) params.set('maxPrice', filters.maxPrice)
  if (filters.q) params.set('q', filters.q)
  params.set('page', page)
  params.set('size', size)
  if (sort) params.set('sort', sort)

  const res = await fetch('/api/products/page?' + params.toString())
  if (!res.ok) throw new Error('Не вдалося завантажити список товарів')
  return res.json()
}

// Унікальні категорії з бекенду — для випадаючого списку у фільтрах.
export async function fetchCategories() {
  const res = await fetch('/api/products/categories')
  if (!res.ok) throw new Error('Не вдалося завантажити категорії')
  return res.json()
}

// Товари зі знижкою — читання SQL-подання products_discounted (банер «Сьогодні знижки»).
export async function fetchDiscounted() {
  const res = await fetch('/api/products/discounted')
  if (!res.ok) throw new Error('Не вдалося завантажити знижки')
  return res.json()
}

// Заняття 2/2: ПАГІНОВАНІ товари зі знижкою. Повертає PageDto.
export async function fetchDiscountedPage(page = 0, size = 8) {
  const params = new URLSearchParams({ page, size })
  const res = await fetch('/api/products/discounted/page?' + params.toString())
  if (!res.ok) throw new Error('Не вдалося завантажити знижки')
  return res.json()
}

export async function fetchProduct(id) {
  const res = await fetch(`/api/products/${id}`)
  if (res.status === 404) throw new Error('Товар не знайдено')
  if (!res.ok) throw new Error('Не вдалося завантажити товар')
  return res.json()
}

// Заняття 2/4: повнотекстовий пошук (FTS) — повертає масив карток, ранжованих за релевантністю.
export async function searchProducts(q) {
  const res = await fetch('/api/products/search?q=' + encodeURIComponent(q))
  if (!res.ok) throw new Error('Не вдалося виконати пошук')
  return res.json()
}

// Заняття 2/4: відгуки на товар (MongoDB).
export async function fetchReviews(productId) {
  const res = await fetch(`/api/products/${productId}/reviews`)
  if (!res.ok) throw new Error('Не вдалося завантажити відгуки')
  return res.json()
}

export async function addReview(productId, review) {
  const res = await fetch(`/api/products/${productId}/reviews`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(review),
  })
  if (res.status === 404) throw new Error('Товар не знайдено')
  if (!res.ok) throw new Error('Не вдалося додати відгук')
  return res.json()
}

// Заняття 1/2: створення / оновлення / видалення товару.

export async function createProduct(product) {
  const res = await fetch('/api/products', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(product),
  })
  if (!res.ok) throw new Error('Не вдалося створити товар')
  return res.json()
}

export async function updateProduct(id, product) {
  const res = await fetch(`/api/products/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(product),
  })
  if (res.status === 404) throw new Error('Товар не знайдено')
  if (!res.ok) throw new Error('Не вдалося оновити товар')
  return res.json()
}

export async function deleteProduct(id) {
  const res = await fetch(`/api/products/${id}`, { method: 'DELETE' })
  if (res.status === 404) throw new Error('Товар не знайдено')
  if (!res.ok) throw new Error('Не вдалося видалити товар')
}
