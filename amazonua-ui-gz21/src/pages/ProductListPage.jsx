import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchProducts, fetchCategories, deleteProduct } from '../api.js'
import ProductFormModal from '../components/ProductFormModal.jsx'
import FilterPanel from '../components/FilterPanel.jsx'

const emptyFilters = { category: '', minPrice: '', maxPrice: '', q: '' }

// Сторінка переліку товарів (заняття 2/1).
// Пошуковий рядок + панель фільтрів (категорія, діапазон ціни) — усе через API/БД.
export default function ProductListPage() {
  const [products, setProducts] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(null)        // null | { productId?: number }
  const [filters, setFilters] = useState(emptyFilters)
  const [showFilters, setShowFilters] = useState(false)
  const [searchInput, setSearchInput] = useState('')
  const [categories, setCategories] = useState([])   // для випадаючого списку у фільтрах

  // перезавантаження списку з поточними фільтрами
  const reload = (f = filters) =>
    fetchProducts(f)
      .then(setProducts)
      .catch((e) => setError(e.message))

  useEffect(() => {
    reload(emptyFilters).finally(() => setLoading(false))
    fetchCategories().then(setCategories).catch(() => {})   // необов'язкове — не блокує сторінку
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const applyFilters = (patch) => {
    const next = { ...filters, ...patch }
    setFilters(next)
    setShowFilters(false)
    reload(next)
  }

  const onSearch = (e) => {
    e.preventDefault()
    applyFilters({ q: searchInput.trim() })
  }

  const resetAll = () => {
    setFilters(emptyFilters)
    setSearchInput('')
    setShowFilters(false)
    reload(emptyFilters)
  }

  // список категорій міг змінитися (додали/прибрали товар) — перечитуємо
  const reloadCategories = () => fetchCategories().then(setCategories).catch(() => {})

  const onDelete = async (id) => {
    setError(null)
    try {
      await deleteProduct(id)
      await reload()
      await reloadCategories()
    } catch (err) {
      setError(err.message)
    }
  }

  const activeFilterCount =
    (filters.category ? 1 : 0) + (filters.minPrice ? 1 : 0) + (filters.maxPrice ? 1 : 0)

  if (loading) return <div className="container"><p>Завантаження…</p></div>

  return (
    <div className="container">
      <header className="header">
        <h1>AmazonUA</h1>
        <span className="muted">{products.length} товарів</span>
        <button className="add-button" onClick={() => setModal({})}
                title="Додати товар">+</button>
      </header>

      <div className="toolbar">
        <form className="search-form" onSubmit={onSearch}>
          <input className="search-input" placeholder="Пошук за назвою…"
                 value={searchInput} onChange={(e) => setSearchInput(e.target.value)} />
          <button type="submit" className="btn-primary">Знайти</button>
        </form>

        <button className="filter-toggle" onClick={() => setShowFilters((v) => !v)}>
          Фільтри{activeFilterCount ? ` (${activeFilterCount})` : ''}
        </button>

        {(activeFilterCount > 0 || filters.q) && (
          <button className="btn-secondary" onClick={resetAll}>Скинути все</button>
        )}
      </div>

      {showFilters && (
        <FilterPanel
          initial={filters}
          categories={categories}
          onApply={applyFilters}
          onReset={resetAll}
          onClose={() => setShowFilters(false)}
        />
      )}

      {error && <p className="error">{error}</p>}

      {products.length === 0 ? (
        <p className="muted">Нічого не знайдено за заданими критеріями.</p>
      ) : (
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
      )}

      {modal && (
        <ProductFormModal
          productId={modal.productId}
          onClose={() => setModal(null)}
          onSaved={() => { reload(); reloadCategories() }}
        />
      )}
    </div>
  )
}

function formatPrice(price) {
  return Number(price).toLocaleString('uk-UA', { minimumFractionDigits: 2 })
}
