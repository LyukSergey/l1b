import { useEffect, useState } from 'react'
import { createProduct, updateProduct, fetchProduct } from '../api.js'

const emptyForm = {
  name: '', price: '', category: '', imageUrl: '', images: '', description: '',
  characteristics: [{ name: '', value: '' }], // список пар; кожна — окремі поля
}

// Універсальна модалка товару: створення (productId не задано) або
// редагування (productId задано — підтягує повні дані й зберігає через PUT).
// onClose() — закрити; onSaved() — після успішного збереження (щоб оновити список).
export default function ProductFormModal({ productId, onClose, onSaved }) {
  const isEdit = productId != null
  const [form, setForm] = useState(emptyForm)
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)
  const [loading, setLoading] = useState(isEdit) // у режимі редагування спершу вантажимо товар

  // закриття по Esc
  useEffect(() => {
    const onKey = (e) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', onKey)
    return () => document.removeEventListener('keydown', onKey)
  }, [onClose])

  // режим редагування: підтягнути ПОВНІ дані товару (деталі з /{id}, бо в картці їх немає)
  useEffect(() => {
    if (!isEdit) return
    fetchProduct(productId)
      .then((p) => {
        setForm({
          name: p.name ?? '',
          price: p.price ?? '',
          category: p.category ?? '',
          imageUrl: p.imageUrl ?? '',
          images: (p.images ?? []).join(', '),
          description: p.description ?? '',
          characteristics: p.characteristics?.length
            ? p.characteristics.map((c) => ({ name: c.name, value: c.value }))
            : [{ name: '', value: '' }],
        })
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [isEdit, productId])

  // --- динамічні поля характеристик ---
  const updateCharacteristic = (index, field, value) =>
    setForm({
      ...form,
      characteristics: form.characteristics.map((c, i) =>
        i === index ? { ...c, [field]: value } : c,
      ),
    })
  const addCharacteristic = () =>
    setForm({ ...form, characteristics: [...form.characteristics, { name: '', value: '' }] })
  const removeCharacteristic = (index) =>
    setForm({ ...form, characteristics: form.characteristics.filter((_, i) => i !== index) })

  const onSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setSaving(true)
    try {
      const mainImage = form.imageUrl || '/images/headphones.svg'
      const images = form.images
        ? form.images.split(',').map((s) => s.trim()).filter(Boolean)
        : [mainImage]
      const characteristics = form.characteristics.filter((c) => c.name.trim())

      const payload = {
        name: form.name,
        price: Number(form.price),
        category: form.category,
        imageUrl: mainImage,
        images,
        description: form.description,
        characteristics,
      }

      if (isEdit) {
        await updateProduct(productId, payload)
      } else {
        await createProduct(payload)
      }
      onSaved()
      onClose()
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
          <h2>{isEdit ? 'Редагувати товар' : 'Новий товар'}</h2>
          <button className="modal-close" onClick={onClose} aria-label="Закрити">×</button>
        </div>

        {error && <p className="error">{error}</p>}

        {loading ? (
          <p>Завантаження…</p>
        ) : (
          <form className="create-form" onSubmit={onSubmit}>
            <div className="create-form-row">
              <input placeholder="Назва" value={form.name} required
                     onChange={(e) => setForm({ ...form, name: e.target.value })} />
              <input placeholder="Ціна" type="number" step="0.01" value={form.price} required
                     onChange={(e) => setForm({ ...form, price: e.target.value })} />
              <input placeholder="Категорія" value={form.category}
                     onChange={(e) => setForm({ ...form, category: e.target.value })} />
            </div>

            <div className="create-form-row">
              <input placeholder="URL основного фото" value={form.imageUrl}
                     onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} />
              <input placeholder="Усі фото через кому (необов'язково)" value={form.images}
                     onChange={(e) => setForm({ ...form, images: e.target.value })} />
            </div>

            <textarea placeholder="Опис товару" value={form.description} rows={2}
                      onChange={(e) => setForm({ ...form, description: e.target.value })} />

            <fieldset className="chars-fieldset">
              <legend>Характеристики</legend>
              {form.characteristics.map((c, i) => (
                <div className="char-row" key={i}>
                  <input placeholder="Назва (напр. Колір)" value={c.name}
                         onChange={(e) => updateCharacteristic(i, 'name', e.target.value)} />
                  <input placeholder="Значення (напр. Чорний)" value={c.value}
                         onChange={(e) => updateCharacteristic(i, 'value', e.target.value)} />
                  <button type="button" className="char-remove"
                          onClick={() => removeCharacteristic(i)}
                          disabled={form.characteristics.length === 1}>×</button>
                </div>
              ))}
              <button type="button" className="char-add" onClick={addCharacteristic}>
                + Додати характеристику
              </button>
            </fieldset>

            <div className="modal-actions">
              <button type="button" className="btn-secondary" onClick={onClose}>Скасувати</button>
              <button type="submit" className="create-submit" disabled={saving}>
                {saving ? 'Збереження…' : (isEdit ? 'Зберегти' : 'Додати товар')}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}
