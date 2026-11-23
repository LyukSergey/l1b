import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { fetchCategories, fetchProductsPage } from '../api.js'
import ProductGrid from '../components/ProductGrid.jsx'
import ProductFormModal from '../components/ProductFormModal.jsx'

const SEARCH_SIZE = 24

// Іконка, що ВІДПОВІДАЄ категорії (статичні SVG у /images/categories/).
// Назва категорії → файл іконки; для невідомих категорій — default.
const CATEGORY_ICONS = {
  'аудіо': 'audio',
  'монітори': 'monitors',
  'периферія': 'peripherals',
  'аксесуари': 'accessories',
  'кабелі': 'cables',
  'накопичувачі': 'storage',
}
function categoryIcon(name) {
  const key = (name || '').trim().toLowerCase()
  const file = CATEGORY_ICONS[key] || 'default'
  return `/images/categories/${file}.svg`
}

// Головна сторінка (вітрина):
//  - банер «Сьогодні знижки» → сторінка знижок;
//  - плашки категорій (груп товарів) → сторінка категорії;
//  - коли в пошуку є текст, замість плашок одразу показуються КАРТКИ знайдених товарів.
export default function HomePage() {
  const navigate = useNavigate()
  const [categories, setCategories] = useState([])
  const [searchInput, setSearchInput] = useState('')
  const [query, setQuery] = useState('')          // застосований текст пошуку ('' = показуємо плашки)
  const [results, setResults] = useState([])
  const [error, setError] = useState(null)
  const [modal, setModal] = useState(false)

  const loadCategories = () => fetchCategories().then(setCategories).catch((e) => setError(e.message))

  useEffect(() => {
    loadCategories()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // пошук: по тексту одразу шукаємо товари (усі категорії) і показуємо картки
  const runSearch = (q) => {
    setQuery(q)
    if (!q) { setResults([]); return }
    fetchProductsPage({ q }, 0, SEARCH_SIZE, '')
      .then((page) => setResults(page.content))
      .catch((e) => setError(e.message))
  }

  const onSearchSubmit = (e) => {
    e.preventDefault()
    runSearch(searchInput.trim())
  }

  const onSearchChange = (value) => {
    setSearchInput(value)
    runSearch(value.trim())   // живий пошук під час набору
  }

  const searching = query.length > 0

  return (
    <div className="container">
      <header className="header">
        <h1>AmazonUA</h1>
        <button className="add-button" onClick={() => setModal(true)} title="Додати товар">+</button>
      </header>

      <form className="search-form home-search" onSubmit={onSearchSubmit}>
        <input className="search-input" placeholder="Пошук за назвою…"
               value={searchInput} onChange={(e) => onSearchChange(e.target.value)} />
        <button type="submit" className="btn-primary">Знайти</button>
      </form>

      {error && <p className="error">{error}</p>}

      {searching ? (
        // режим пошуку: одразу картки знайдених товарів
        <>
          <h2 className="section-title">Результати пошуку: «{query}»</h2>
          <ProductGrid products={results} />
        </>
      ) : (
        // звичайна вітрина: плашки груп, де перша картка — «Сьогодні знижки»
        <>
          <h2 className="section-title">Групи товарів</h2>
          <div className="group-grid">
            <button className="group-card group-card-discount" onClick={() => navigate('/discounts')}>
              <div className="group-image">
                <span className="group-discount-badge">%</span>
              </div>
              <div className="group-name">Сьогодні знижки</div>
            </button>

            {categories.map((c) => (
              <button key={c} className="group-card"
                      onClick={() => navigate(`/category/${encodeURIComponent(c)}`)}>
                <div className="group-image">
                  <img src={categoryIcon(c)} alt={c} loading="lazy" />
                </div>
                <div className="group-name">{c}</div>
              </button>
            ))}
          </div>
        </>
      )}

      {modal && (
        <ProductFormModal
          onClose={() => setModal(false)}
          onSaved={() => { setModal(false); loadCategories() }}
        />
      )}
    </div>
  )
}
