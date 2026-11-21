// Тонкий клієнт до нашого Spring Boot API.
// Шляхи відносні ('/api/...') — Vite-проксі перенаправить їх на :8080 (див. vite.config.js).

// Заняття 2/1: перелік із фільтрами. filters = { category, minPrice, maxPrice, q }.
// Порожні значення не додаємо в query string.
export async function fetchProducts(filters = {}) {
  const params = new URLSearchParams()
  if (filters.category) params.set('category', filters.category)
  if (filters.minPrice) params.set('minPrice', filters.minPrice)
  if (filters.maxPrice) params.set('maxPrice', filters.maxPrice)
  if (filters.q) params.set('q', filters.q)

  const query = params.toString()
  const res = await fetch('/api/products' + (query ? '?' + query : ''))
  if (!res.ok) throw new Error('Не вдалося завантажити список товарів')
  return res.json()
}

// Унікальні категорії з бекенду — для випадаючого списку у фільтрах.
export async function fetchCategories() {
  const res = await fetch('/api/products/categories')
  if (!res.ok) throw new Error('Не вдалося завантажити категорії')
  return res.json()
}

export async function fetchProduct(id) {
  const res = await fetch(`/api/products/${id}`)
  if (res.status === 404) throw new Error('Товар не знайдено')
  if (!res.ok) throw new Error('Не вдалося завантажити товар')
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
