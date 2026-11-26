import { useEffect, useState } from 'react'
import { fetchReviews } from '../api.js'
import ReviewFormModal from './ReviewFormModal.jsx'

// Блок відгуків на сторінці товару (заняття 2/4): середня оцінка, список відгуків із MongoDB
// і кнопка «+ Додати відгук», що відкриває модалку з формою.
export default function Reviews({ productId }) {
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [modal, setModal] = useState(false)

  const load = () =>
    fetchReviews(productId)
      .then(setReviews)
      .catch((e) => setError(e.message))

  useEffect(() => {
    load().finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [productId])

  const avg = reviews.length
    ? (reviews.reduce((s, r) => s + r.rating, 0) / reviews.length).toFixed(1)
    : null

  return (
    <section className="reviews">
      <div className="reviews-bar">
        <h2 className="reviews-title">
          Відгуки {reviews.length > 0 && (
            <span className="reviews-summary">{Stars(Math.round(avg))} {avg} · {reviews.length}</span>
          )}
        </h2>
        <button className="add-button" onClick={() => setModal(true)} title="Додати відгук">+</button>
      </div>

      {error && <p className="error">{error}</p>}

      {loading ? (
        <p className="muted">Завантаження відгуків…</p>
      ) : reviews.length === 0 ? (
        <p className="muted">Відгуків ще немає — будьте першим: натисніть «+».</p>
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

      {modal && (
        <ReviewFormModal
          productId={productId}
          onClose={() => setModal(false)}
          onSaved={() => { setModal(false); load() }}
        />
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
