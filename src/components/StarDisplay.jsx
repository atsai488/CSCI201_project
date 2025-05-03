import React from 'react'

export default function StarDisplay({ stars }) {
  const fullStars = Math.round(stars)
  const emptyStars = 5 - fullStars
  return (
    <span className="star-display">
      {'★'.repeat(fullStars)}{'☆'.repeat(emptyStars)}
    </span>
  )
}
