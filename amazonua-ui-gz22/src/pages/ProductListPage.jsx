import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchProductsPage, fetchCategories, deleteProduct } from '../api.js'
import ProductFormModal from '../components/ProductFormModal.jsx'
import FilterPanel from '../components/FilterPanel.jsx'

const emptyFilters = { category: '', minPrice: '', maxPrice: '', q: '' }
const PAGE_SIZE = 8

// Сторінка переліку товарів (заняття 2/2).
// Пошук + панель фільтрів + ПАГІНАЦІЯ і СОРТУВАННЯ — усе через пагінований ендпоінт /api/products/page.
export default function ProductListPage() {
  const [products, setProducts] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(null)          // null | { productId?: number }
  const [filters, setFilters] = useState(emptyFilters)
  const [showFilters, setShowFilters] = useState(false)
  const [searchInput, setSearchInput] = useState('')
  const [categories, setCategories] = useState([])  // для випадаючого списку у фільтрах
  const [page, setPage] = useState(0)               // поточна сторінка (з 0)
  const [sort, setSort] = useState('')              // напр. 'price,asc'
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  // завантаження сторінки з поточними фільтрами/сортуванням
  const load = (f, p, s) =>
    fetchProductsPage(f, p, PAGE_SIZE, s)
      .then((pageData) => {
        setProducts(pageData.content)
        setTotalPages(pageData.totalPages)
        setTotalElements(pageData.totalElements)
      })
      .catch((e) => setError(e.message))

  useEffect(() => {
    load(emptyFilters, 0, '').finally(() => setLoading(false))
    fetchCategories().then(setCategories).catch(() => {})
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // будь-яка зміна фільтрів/пошуку/сортування скидає на першу сторінку
  const applyFilters = (patch) => {
    const next = { ...filters, ...patch }
    setFilters(next)
    setShowFilters(false)
    setPage(0)
    load(next, 0, sort)
  }

  const onSearch = (e) => {
    e.preventDefault()
    applyFilters({ q: searchInput.trim() })
  }

  const onSortChange = (value) => {
    setSort(value)
    setPage(0)
    load(filters, 0, value)
  }

  const goToPage = (p) => {
    if (p < 0 || p >= totalPages) return
    setPage(p)
    load(filters, p, sort)
  }

  const resetAll = () => {
    setFilters(emptyFilters)
    setSearchInput('')
    setSort('')
    setShowFilters(false)
    setPage(0)
    load(emptyFilters, 0, '')
  }

  const reloadCategories = () => fetchCategories().then(setCategories).catch(() => {})

  // після зміни даних перезавантажуємо поточну сторінку й категорії
  const reloadCurrent = () => { load(filters, page, sort); reloadCategories() }

  const onDelete = async (id) => {
    setError(null)
    try {
      await deleteProduct(id)
      reloadCurrent()
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
        <span className="muted">{totalElements} товарів</span>
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

        <label className="sort-select">
          Сортування:
          <select value={sort} onChange={(e) => onSortChange(e.target.value)}>
            <option value="">за замовчуванням</option>
            <option value="price,asc">ціна ↑</option>
            <option value="price,desc">ціна ↓</option>
            <option value="name,asc">назва А-Я</option>
            <option value="name,desc">назва Я-А</option>
          </select>
        </label>

        {(activeFilterCount > 0 || filters.q || sort) && (
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
        <>
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

          <div className="pager">
            <button className="btn-secondary" disabled={page <= 0}
                    onClick={() => goToPage(page - 1)}>← Попередня</button>
            <span className="pager-info">Сторінка {page + 1} з {totalPages}</span>
            <button className="btn-secondary" disabled={page >= totalPages - 1}
                    onClick={() => goToPage(page + 1)}>Наступна →</button>
          </div>
        </>
      )}

      {modal && (
        <ProductFormModal
          productId={modal.productId}
          onClose={() => setModal(null)}
          onSaved={reloadCurrent}
        />
      )}
    </div>
  )
}

function formatPrice(price) {
  return Number(price).toLocaleString('uk-UA', { minimumFractionDigits: 2 })
}
