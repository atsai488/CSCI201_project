import React, { useState, useEffect } from 'react';
import '@fortawesome/fontawesome-free/css/all.min.css';
import '../css/style.css';

export default function BuyerAccount() {
  const [userData, setUserData] = useState({
    name: '',
    email: '',
    joinDate: '',
    purchaseHistory: [],
    ratings: []
  });

  useEffect(() => {
    const fetchUserData = async () => {
      const email = localStorage.getItem('email');
      if (email) {
        try {
          const response = await fetch(`/get-user-details?email=${encodeURIComponent(email)}`);
          if (response.ok) {
            const data = await response.json();
            setUserData(data);
          }
        } catch (error) {
          console.error('Error fetching user data:', error);
        }
      }
    };

    fetchUserData();
  }, []);

  return (
    <div className="buyer-account">
      <header>
        <div className="header" onClick={() => window.location.href = '/'} style={{ cursor: "pointer" }}>
          USC Marketplace
        </div>
        <div className="nav-icons">
          <i
            className="fa fa-comment"
            onClick={() => window.location.href = '/messages'}
          />
          <div className="profile-container">
            <i
              className="fa fa-user"
              onClick={() => window.location.href = '/buyeraccount'}
            />
          </div>
        </div>
      </header>

      <main className="account-content">
        <div className="account-header">
          <div className="profile-section">
            <div className="profile-picture">
              <i className="fa fa-user-circle fa-5x"></i>
            </div>
            <div className="profile-info">
              <h1>{userData.name || 'Loading...'}</h1>
              <p className="email">{userData.email}</p>
              <p className="join-date">Member since {userData.joinDate || 'Loading...'}</p>
            </div>
          </div>
        </div>

        <div className="account-sections">
          <section className="purchase-history">
            <h2><i className="fa fa-shopping-bag"></i> Purchase History</h2>
            <div className="history-list">
              {userData.purchaseHistory && userData.purchaseHistory.length > 0 ? (
                userData.purchaseHistory.map((purchase, index) => (
                  <div key={index} className="purchase-item">
                    <img src={purchase.image} alt={purchase.productName} />
                    <div className="purchase-details">
                      <h3>{purchase.productName}</h3>
                      <p>Purchased on: {purchase.date}</p>
                      <p>Price: ${purchase.price}</p>
                    </div>
                  </div>
                ))
              ) : (
                <p>No purchase history available</p>
              )}
            </div>
          </section>

          <section className="ratings-section">
            <h2><i className="fa fa-star"></i> Ratings & Reviews</h2>
            <div className="ratings-list">
              {userData.ratings && userData.ratings.length > 0 ? (
                userData.ratings.map((rating, index) => (
                  <div key={index} className="rating-item">
                    <div className="rating-header">
                      <span className="rating-stars">
                        {'★'.repeat(rating.stars)}{'☆'.repeat(5 - rating.stars)}
                      </span>
                      <span className="rating-date">{rating.date}</span>
                    </div>
                    <p className="rating-comment">{rating.comment}</p>
                  </div>
                ))
              ) : (
                <p>No ratings available</p>
              )}
            </div>
          </section>
        </div>
      </main>
    </div>
  );
} 