import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { fetchProductsPage, deleteProduct } from '../api.js'
import ProductGrid from '../components/ProductGrid.jsx'
import ProductFormModal from '../components/ProductFormModal.jsx'

const PAGE_SIZE = 8

// Сторінка однієї групи (категорії): товари цієї категорії картками,
// з пагінацією і сортуванням (пагінований ендпоінт /api/products/page з фільтром category).
export default function CategoryPage() {
  const { name } = useParams()
  const category = decodeURIComponent(name)

  const [products, setProducts] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(null)        // null | { productId?: number }
  const [page, setPage] = useState(0)
  const [sort, setSort] = useState('')
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  const load = (p, s) =>
    fetchProductsPage({ category }, p, PAGE_SIZE, s)
      .then((data) => {
        setProducts(data.content)
        setTotalPages(data.totalPages)
        setTotalElements(data.totalElements)
      })
      .catch((e) => setError(e.message))

  useEffect(() => {
    setLoading(true)
    setPage(0)
    load(0, sort).finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [category])

  const onSortChange = (value) => {
    setSort(value)
    setPage(0)
    load(0, value)
  }

  const goToPage = (p) => {
    if (p < 0 || p >= totalPages) return
    setPage(p)
    load(p, sort)
  }

  const reloadCurrent = () => load(page, sort)

  const onDelete = async (id) => {
    setError(null)
    try {
      await deleteProduct(id)
      reloadCurrent()
    } catch (err) {
      setError(err.message)
    }
  }

  if (loading) return <div className="container"><p>Завантаження…</p></div>

  return (
    <div className="container">
      <Link to="/" className="back">← На головну</Link>

      <header className="header">
        <h1 className="section-title">{category}</h1>
        <span className="muted">{totalElements} товарів</span>
        <button className="add-button" onClick={() => setModal({})} title="Додати товар">+</button>
      </header>

      <div className="toolbar">
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
      </div>

      {error && <p className="error">{error}</p>}

      <ProductGrid products={products} onDelete={onDelete} />

      {products.length > 0 && (
        <div className="pager">
          <button className="btn-secondary" disabled={page <= 0}
                  onClick={() => goToPage(page - 1)}>← Попередня</button>
          <span className="pager-info">Сторінка {page + 1} з {totalPages}</span>
          <button className="btn-secondary" disabled={page >= totalPages - 1}
                  onClick={() => goToPage(page + 1)}>Наступна →</button>
        </div>
      )}

      {modal && (
        <ProductFormModal
          productId={modal.productId}
          onClose={() => setModal(null)}
          onSaved={() => { setModal(null); reloadCurrent() }}
        />
      )}
    </div>
  )
}
