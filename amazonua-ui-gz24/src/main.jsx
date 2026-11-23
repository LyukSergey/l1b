import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import HomePage from './pages/HomePage.jsx'
import CategoryPage from './pages/CategoryPage.jsx'
import DiscountsPage from './pages/DiscountsPage.jsx'
import ProductDetailPage from './pages/ProductDetailPage.jsx'
import './styles.css'

// Маршрути магазину:
//   /                 → вітрина: банер знижок + плашки категорій (пошук → картки товарів)
//   /discounts        → товари зі знижкою (SQL-подання products_discounted)
//   /category/:name   → товари однієї категорії (картками, з пагінацією)
//   /products/:id     → сторінка одного товару
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/discounts" element={<DiscountsPage />} />
        <Route path="/category/:name" element={<CategoryPage />} />
        <Route path="/products/:id" element={<ProductDetailPage />} />
      </Routes>
    </BrowserRouter>
  </React.StrictMode>,
)
