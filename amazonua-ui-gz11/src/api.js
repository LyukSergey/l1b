// Тонкий клієнт до нашого Spring Boot API.
// Шляхи відносні ('/api/...') — Vite-проксі перенаправить їх на :8080 (див. vite.config.js).

export async function fetchProducts() {
  const res = await fetch('/api/products')
  if (!res.ok) throw new Error('Не вдалося завантажити список товарів')
  return res.json()
}

export async function fetchProduct(id) {
  const res = await fetch(`/api/products/${id}`)
  if (res.status === 404) throw new Error('Товар не знайдено')
  if (!res.ok) throw new Error('Не вдалося завантажити товар')
  return res.json()
}
