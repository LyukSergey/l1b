import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchDiscountedPage } from '../api.js'
import ProductGrid from '../components/ProductGrid.jsx'

const PAGE_SIZE = 8

// Сторінка знижок: товари з SQL-подання products_discounted (ендпоінт /api/products/discounted/page),
// з пагінацією (серверною — Pageable над поданням).
export default function DiscountsPage() {
  const [products, setProducts] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  const load = (p) =>
    fetchDiscountedPage(p, PAGE_SIZE)
      .then((data) => {
        setProducts(data.content)
        setTotalPages(data.totalPages)
        setTotalElements(data.totalElements)
      })
      .catch((e) => setError(e.message))

  useEffect(() => {
    load(0).finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const goToPage = (p) => {
    if (p < 0 || p >= totalPages) return
    setPage(p)
    load(p)
  }

  return (
    <div className="container">
      <Link to="/" className="back">← На головну</Link>
      <header className="header">
        <h1 className="section-title">Сьогодні знижки</h1>
        <span className="muted">{totalElements} товарів</span>
      </header>

      {error && <p className="error">{error}</p>}

      {loading ? (
        <p>Завантаження…</p>
      ) : (
        <>
          <ProductGrid products={products} />

          {products.length > 0 && (
            <div className="pager">
              <button className="btn-secondary" disabled={page <= 0}
                      onClick={() => goToPage(page - 1)}>← Попередня</button>
              <span className="pager-info">Сторінка {page + 1} з {totalPages}</span>
              <button className="btn-secondary" disabled={page >= totalPages - 1}
                      onClick={() => goToPage(page + 1)}>Наступна →</button>
            </div>
          )}
        </>
      )}
    </div>
  )
}
