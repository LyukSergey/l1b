import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { fetchProduct } from '../api.js'
import Lightbox from '../components/Lightbox.jsx'

// Сторінка одного товару: галерея фото + деталі + характеристики.
// Клік на велике фото → збільшення (лайтбокс).
export default function ProductDetailPage() {
  const { id } = useParams()
  const [product, setProduct] = useState(null)
  const [error, setError] = useState(null)
  const [activeImage, setActiveImage] = useState(null) // яке фото показано великим
  const [zoomed, setZoomed] = useState(false)          // чи відкрито лайтбокс

  useEffect(() => {
    fetchProduct(id)
      .then((p) => {
        setProduct(p)
        setActiveImage(p.imageUrl)
      })
      .catch((e) => setError(e.message))
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
          <h1>{product.name}</h1>
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

      {zoomed && <Lightbox src={activeImage} alt={product.name} onClose={() => setZoomed(false)} />}
    </div>
  )
}

function formatPrice(price) {
  return Number(price).toLocaleString('uk-UA', { minimumFractionDigits: 2 })
}
