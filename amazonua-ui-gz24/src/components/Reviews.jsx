import { useEffect, useState } from 'react'
import { fetchReviews, addReview } from '../api.js'

// Блок відгуків на сторінці товару (заняття 2/4): список відгуків із MongoDB
// + форма додавання відгуку з оцінкою зірками (як у реальних магазинах).
export default function Reviews({ productId }) {
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // поля форми
  const [author, setAuthor] = useState('')
  const [rating, setRating] = useState(0)
  const [hover, setHover] = useState(0)
  const [text, setText] = useState('')
  const [saving, setSaving] = useState(false)

  const load = () =>
    fetchReviews(productId)
      .then(setReviews)
      .catch((e) => setError(e.message))

  useEffect(() => {
    load().finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [productId])

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
      setAuthor(''); setRating(0); setText('')
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  const avg = reviews.length
    ? (reviews.reduce((s, r) => s + r.rating, 0) / reviews.length).toFixed(1)
    : null

  return (
    <section className="reviews">
      <h2 className="reviews-title">
        Відгуки {reviews.length > 0 && (
          <span className="reviews-summary">{Stars(Math.round(avg))} {avg} · {reviews.length}</span>
        )}
      </h2>

      {/* форма додавання */}
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
               value={author} onChange={(e) => setAuthor(e.target.value)} />
        <textarea className="review-text" rows="3" placeholder="Поділіться враженнями про товар…"
                  value={text} onChange={(e) => setText(e.target.value)} />
        {error && <p className="error">{error}</p>}
        <button type="submit" className="btn-primary" disabled={saving}>
          {saving ? 'Надсилання…' : 'Залишити відгук'}
        </button>
      </form>

      {/* список */}
      {loading ? (
        <p className="muted">Завантаження відгуків…</p>
      ) : reviews.length === 0 ? (
        <p className="muted">Відгуків ще немає — будьте першим.</p>
      ) : (
        <ul className="review-list">
          {reviews.map((r) => (
            <li key={r.id} className="review-item">
              <div className="review-head">
                <span className="review-author-name">{r.author}</span>
                <span className="review-stars">{Stars(r.rating)}</span>
              </div>
              <p className="review-body">{r.text}</p>
              <time className="review-date">{formatDate(r.createdAt)}</time>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}

function Stars(n) {
  return '★★★★★'.slice(0, n) + '☆☆☆☆☆'.slice(0, 5 - n)
}

function formatDate(iso) {
  if (!iso) return ''
  try {
    return new Date(iso).toLocaleDateString('uk-UA', { year: 'numeric', month: 'long', day: 'numeric' })
  } catch {
    return ''
  }
}
