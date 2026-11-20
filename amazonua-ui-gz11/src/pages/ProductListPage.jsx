import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchProducts } from '../api.js'

// Сторінка переліку товарів. Кожен елемент — посилання на сторінку товару (/products/:id).
export default function ProductListPage() {
  const [products, setProducts] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchProducts()
      .then(setProducts)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="container"><p>Завантаження…</p></div>
  if (error) return <div className="container"><p className="error">{error}</p></div>

  return (
    <div className="container">
      <header className="header">
        <h1>AmazonUA</h1>
        <span className="muted">{products.length} товарів</span>
      </header>

      <div className="grid">
        {products.map((p) => (
          <Link key={p.id} to={`/products/${p.id}`} className="card">
            <div className="card-image">
              <img src={p.imageUrl} alt={p.name} loading="lazy" />
            </div>
            <div className="card-body">
              <div className="card-name">{p.name}</div>
              <div className="card-price">{formatPrice(p.price)} грн</div>
            </div>
          </Link>
        ))}
      </div>
    </div>
  )
}

function formatPrice(price) {
  return Number(price).toLocaleString('uk-UA', { minimumFractionDigits: 2 })
}
