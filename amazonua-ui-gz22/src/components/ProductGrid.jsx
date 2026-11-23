import { Link } from 'react-router-dom'

// Сітка карток товарів (перевикористовується на сторінці категорії, знижок і в результатах пошуку).
// products — масив карток { id, name, price, imageUrl }; onDelete — опційний обробник видалення.
export default function ProductGrid({ products, onDelete }) {
  if (!products || products.length === 0) {
    return <p className="muted">Нічого не знайдено.</p>
  }
  return (
    <div className="grid">
      {products.map((p) => (
        <div key={p.id} className="card">
          <Link to={`/products/${p.id}`} className="card-link">
            <div className="card-image">
              {p.imageUrl
                ? <img src={p.imageUrl} alt={p.name} loading="lazy" />
                : <span className="card-image-ph">🛍</span>}
            </div>
            <div className="card-body">
              <div className="card-name">{p.name}</div>
              <div className="card-price">{formatPrice(p.price)} грн</div>
            </div>
          </Link>
          {onDelete && (
            <button className="card-delete" onClick={() => onDelete(p.id)}>Видалити</button>
          )}
        </div>
      ))}
    </div>
  )
}

export function formatPrice(price) {
  return Number(price).toLocaleString('uk-UA', { minimumFractionDigits: 2 })
}
