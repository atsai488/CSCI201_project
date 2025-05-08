import React, { useState, useEffect } from 'react'
import RatingForm from './RatingForm'
import RatingsList from './RatingList'
import StarDisplay from './StarDisplay'

export default function RatingSection({ sellerId }) {
  const [ratings, setRatings] = useState([])
  const [average, setAverage] = useState(0)

  const fetchRatings = async () => {
    try {
      const res = await fetch(`/api/ratings/seller/${sellerId}`);
      console.log('fetch status', res.status);
  
      const text = await res.text();
      console.log('raw response text:', text);
  
      // now try to parse it
      const data = JSON.parse(text);
      console.log('parsed data:', data);
  
      setAverage(data.average);
      setRatings(data.ratings);
    } catch (err) {
      console.error('Failed to fetch ratings:', err);
    }
  }
  
  

  useEffect(() => {
    fetchRatings()
  }, [sellerId])

  return (
    <section className="ratings-section">
      <div className="ratings-summary">
        <StarDisplay stars={average} />
        <span>{average.toFixed(1)} ★ ({ratings.length} reviews)</span>
      </div>
      <RatingForm sellerId={sellerId} onRatingSubmitted={fetchRatings} />
      <RatingsList ratings={ratings} />
    </section>
  )
}
