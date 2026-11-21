// Панель фільтрів (заняття 2/1). Відкривається кнопкою «Фільтри».
// Локальний стан полів; «Застосувати» передає значення нагору, «Скинути» очищає.
import { useState } from 'react'

export default function FilterPanel({ initial, onApply, onReset, onClose }) {
  const [category, setCategory] = useState(initial.category ?? '')
  const [minPrice, setMinPrice] = useState(initial.minPrice ?? '')
  const [maxPrice, setMaxPrice] = useState(initial.maxPrice ?? '')

  const apply = (e) => {
    e.preventDefault()
    onApply({ category: category.trim(), minPrice, maxPrice })
  }

  const reset = () => {
    setCategory('')
    setMinPrice('')
    setMaxPrice('')
    onReset()
  }

  return (
    <div className="filter-panel">
      <div className="filter-panel-header">
        <h3>Фільтри</h3>
        <button className="filter-close" onClick={onClose} aria-label="Закрити">×</button>
      </div>

      <form onSubmit={apply}>
        <label className="filter-field">
          <span>Категорія</span>
          <input placeholder="напр. Периферія (можна частину)" value={category}
                 onChange={(e) => setCategory(e.target.value)} />
        </label>

        <label className="filter-field">
          <span>Ціна від</span>
          <input type="number" step="0.01" placeholder="мін." value={minPrice}
                 onChange={(e) => setMinPrice(e.target.value)} />
        </label>

        <label className="filter-field">
          <span>Ціна до</span>
          <input type="number" step="0.01" placeholder="макс." value={maxPrice}
                 onChange={(e) => setMaxPrice(e.target.value)} />
        </label>

        <div className="filter-actions">
          <button type="button" className="btn-secondary" onClick={reset}>Скинути</button>
          <button type="submit" className="btn-primary">Застосувати</button>
        </div>
      </form>
    </div>
  )
}
