import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { fetchProduct } from '../api.js'
import Lightbox from '../components/Lightbox.jsx'
import ProductFormModal from '../components/ProductFormModal.jsx'
import Reviews from '../components/Reviews.jsx'

// Сторінка одного товару: галерея фото + деталі + характеристики.
// Клік на велике фото → збільшення (лайтбокс). Редагування (олівець) — тут,
// на сторінці деталей (а не з картки у списку).
export default function ProductDetailPage() {
  const { id } = useParams()
  const [product, setProduct] = useState(null)
  const [error, setError] = useState(null)
  const [activeImage, setActiveImage] = useState(null) // яке фото показано великим
  const [zoomed, setZoomed] = useState(false)          // чи відкрито лайтбокс
  const [editing, setEditing] = useState(false)        // чи відкрито модалку редагування

  // завантажити (або перезавантажити) товар із сервера
  const load = () =>
    fetchProduct(id)
      .then((p) => {
        setProduct(p)
        setActiveImage(p.imageUrl)
      })
      .catch((e) => setError(e.message))

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id])

  if (error) {
    return (
      <div className="container">
        <p className="error">{error}</p>
        <Link to="/" className="back">← До списку</Link>
      </div>
    )
  }
  if (!product) return <div className="container"><p>Завантаження…</p></div>

  const images = product.images?.length ? product.images : [product.imageUrl]

  return (
    <div className="container">
      <Link to="/" className="back">← До списку</Link>

      <div className="detail">
        <div className="gallery">
          <div className="gallery-main" onClick={() => setZoomed(true)} title="Натисніть, щоб збільшити">
            <img src={activeImage} alt={product.name} />
          </div>
          {images.length > 1 && (
            <div className="thumbs">
              {images.map((img) => (
                <button
                  key={img}
                  className={'thumb' + (img === activeImage ? ' thumb-active' : '')}
                  onClick={() => setActiveImage(img)}
                >
                  <img src={img} alt="" />
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="detail-info">
          <div className="detail-title">
            <h1>{product.name}</h1>
            <button className="detail-edit" title="Редагувати"
                    onClick={() => setEditing(true)}>✎</button>
          </div>
          <div className="detail-price">{formatPrice(product.price)} грн</div>
          <p className="detail-description">{product.description}</p>

          {product.characteristics?.length > 0 && (
            <table className="chars">
              <tbody>
                {product.characteristics.map((c) => (
                  <tr key={c.name}>
                    <td className="chars-name">{c.name}</td>
                    <td className="chars-value">{c.value}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* Заняття 2/4: відгуки на товар (MongoDB) */}
      <Reviews productId={product.id} />

      {zoomed && <Lightbox src={activeImage} alt={product.name} onClose={() => setZoomed(false)} />}

      {editing && (
        <ProductFormModal
          productId={product.id}
          onClose={() => setEditing(false)}
          onSaved={() => { setEditing(false); load() }}
        />
      )}
    </div>
  )
}

function formatPrice(price) {
  return Number(price).toLocaleString('uk-UA', { minimumFractionDigits: 2 })
}
