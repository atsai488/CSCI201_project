import React from 'react'
import StarDisplay from './StarDisplay'

export default function RatingsList({ ratings }) {
  if (ratings.length === 0) {
    return <p>No reviews yet.</p>
  }
  return (
    <div className="ratings-list">
      {ratings.map(r => (
        <div key={r.id} className="rating-item">
          <StarDisplay stars={r.stars} />
          {r.comment && <p>{r.comment}</p>}
          <small>
            By {r.buyerUsername} on {new Date(r.createdAt).toLocaleDateString()}
          </small>
        </div>
      ))}
    </div>
  )
}
