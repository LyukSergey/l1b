import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchProducts, deleteProduct } from '../api.js'
import ProductFormModal from '../components/ProductFormModal.jsx'

// Сторінка переліку товарів.
// Додавання — кнопка «+»; редагування — олівець на картці; обидва відкривають модалку.
export default function ProductListPage() {
  const [products, setProducts] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(null) // null | { productId?: number }

  const reload = () =>
    fetchProducts()
      .then(setProducts)
      .catch((e) => setError(e.message))

  useEffect(() => {
    reload().finally(() => setLoading(false))
  }, [])

  const onDelete = async (id) => {
    setError(null)
    try {
      await deleteProduct(id)
      await reload()
    } catch (err) {
      setError(err.message)
    }
  }

  if (loading) return <div className="container"><p>Завантаження…</p></div>

  return (
    <div className="container">
      <header className="header">
        <h1>AmazonUA</h1>
        <span className="muted">{products.length} товарів</span>
        <button className="add-button" onClick={() => setModal({})}
                title="Додати товар">+</button>
      </header>

      {error && <p className="error">{error}</p>}

      <div className="grid">
        {products.map((p) => (
          <div key={p.id} className="card">
            <button className="card-edit" title="Редагувати"
                    onClick={() => setModal({ productId: p.id })}>✎</button>
            <Link to={`/products/${p.id}`} className="card-link">
              <div className="card-image">
                <img src={p.imageUrl} alt={p.name} loading="lazy" />
              </div>
              <div className="card-body">
                <div className="card-name">{p.name}</div>
                <div className="card-price">{formatPrice(p.price)} грн</div>
              </div>
            </Link>
            <button className="card-delete" onClick={() => onDelete(p.id)}>Видалити</button>
          </div>
        ))}
      </div>

      {modal && (
        <ProductFormModal
          productId={modal.productId}
          onClose={() => setModal(null)}
          onSaved={reload}
        />
      )}
    </div>
  )
}

function formatPrice(price) {
  return Number(price).toLocaleString('uk-UA', { minimumFractionDigits: 2 })
}
