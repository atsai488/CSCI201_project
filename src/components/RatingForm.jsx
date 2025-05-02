import React, { useState } from 'react'

export default function RatingForm({ sellerId, onRatingSubmitted }) {
  const [stars, setStars]     = useState(0)
  const [comment, setComment] = useState('')

  const handleStarClick = (n) => setStars(n)

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (stars < 1) return
    try {
      const res = await fetch('/api/ratings', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify({ sellerId, stars, comment })
      })
      if (!res.ok) throw new Error('Network response was not ok')
      setStars(0)
      setComment('')
      onRatingSubmitted()
    } catch (err) {
      console.error('Failed to submit rating:', err)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="rating-form">
      <div className="star-rating">
          {[1,2,3,4,5].map(n => (
      <span
        key={n}
        onClick={() => handleStarClick(n)}
        className={n <= stars ? 'star selected' : 'star'}
      >
        ★
      </span>
    ))}
      </div>
      <textarea
        value={comment}
        onChange={e => setComment(e.target.value)}
        placeholder="Optional comment"
      />
      <button type="submit" disabled={stars===0}>
        Submit Review
      </button>
    </form>
  )
}
