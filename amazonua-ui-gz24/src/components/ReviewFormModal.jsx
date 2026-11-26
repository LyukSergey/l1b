import { useEffect, useState } from 'react'
import { addReview } from '../api.js'

// Модалка додавання відгуку (заняття 2/4). Відкривається кнопкою «+ Додати відгук».
// Форма: оцінка зірками (1–5), ім'я, текст. onSaved() — після успішного збереження.
export default function ReviewFormModal({ productId, onClose, onSaved }) {
  const [author, setAuthor] = useState('')
  const [rating, setRating] = useState(0)
  const [hover, setHover] = useState(0)
  const [text, setText] = useState('')
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)

  // закриття по Esc
  useEffect(() => {
    const onKey = (e) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', onKey)
    return () => document.removeEventListener('keydown', onKey)
  }, [onClose])

  const submit = async (e) => {
    e.preventDefault()
    setError(null)
    if (!author.trim() || !text.trim() || rating < 1) {
      setError('Вкажіть імʼя, оцінку (1–5 зірок) і текст відгуку.')
      return
    }
    setSaving(true)
    try {
      await addReview(productId, { author: author.trim(), rating, text: text.trim() })
      onSaved()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Новий відгук</h2>
          <button className="modal-close" onClick={onClose} aria-label="Закрити">×</button>
        </div>

        <form className="review-form" onSubmit={submit}>
          <div className="review-stars-input" role="radiogroup" aria-label="Оцінка">
            {[1, 2, 3, 4, 5].map((n) => (
              <button type="button" key={n}
                      className={'star-btn' + ((hover || rating) >= n ? ' star-on' : '')}
                      onMouseEnter={() => setHover(n)}
                      onMouseLeave={() => setHover(0)}
                      onClick={() => setRating(n)}
                      aria-label={`${n} з 5`}>★</button>
            ))}
            <span className="review-stars-hint">{rating ? `${rating} з 5` : 'оцініть товар'}</span>
          </div>
          <input className="review-author" placeholder="Ваше імʼя"
                 value={author} onChange={(e) => setAuthor(e.target.value)} autoFocus />
          <textarea className="review-text" rows="4" placeholder="Поділіться враженнями про товар…"
                    value={text} onChange={(e) => setText(e.target.value)} />
          {error && <p className="error">{error}</p>}
          <div className="modal-actions">
            <button type="button" className="btn-secondary" onClick={onClose}>Скасувати</button>
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? 'Надсилання…' : 'Залишити відгук'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
