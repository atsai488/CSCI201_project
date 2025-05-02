import React from 'react'
import ReactDOM from 'react-dom/client'
import RatingsSection from './components/RatingSection'
import '../css/style.css';

const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(
  <React.StrictMode>
    {/* for testing */}
    <RatingsSection sellerId={2} />
  </React.StrictMode>
)
