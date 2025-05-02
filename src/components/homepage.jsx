import React, { useState } from 'react';
import '@fortawesome/fontawesome-free/css/all.min.css';
import '../css/style.css'; 

export default function Homepage() {
  const userRole = 'seller';
  const [profileOpen, setProfileOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  return (
    <>
      <header>
        <div className="header">USC Marketplace</div>
        <div className="nav-icons">
          <i
            id="addIcon"
            className="fa fa-plus"
            style={{ display: userRole === 'seller' ? 'inline-block' : 'none' }}
            onClick={() => userRole === 'seller' && (window.location.href = '/createlisting')}
          />
          <i
            id="messageIcon"
            className="fa fa-comment"
            onClick={() => (window.location.href = '/messages')}
          />
          <div className="profile-container">
            <i
              id="profileIcon"
              className="fa fa-user"
              onClick={() => setProfileOpen(o => !o)}
            />
            {profileOpen && (
              <div id="profileDropdown" className="dropdown">
                <ul>
                  <li
                    id="goToProfile"
                    onClick={() =>
                      (window.location.href =
                        userRole === 'seller'
                          ? '/selleraccount'
                          : '/buyeraccount')
                    }
                  >
                    <i className="fa fa-user-circle" /> Go to Profile
                  </li>
                  <li
                    id="logout"
                    onClick={() => (window.location.href = '/logout')}
                  >
                    <i className="fa fa-sign-out-alt" /> Logout
                  </li>
                </ul>
              </div>
            )}
          </div>
        </div>
      </header>

      <main>
        <div className="search-container">
          <form
            id="searchForm"
            onSubmit={e => {
              e.preventDefault();
              document.getElementById(
                'listingsHeading'
              ).textContent = `Listings for “${searchTerm.trim()}”`;
            }}
          >
            <div className="search-input-wrapper">
              <i className="fa fa-search" />
              <input
                type="text"
                id="searchInput"
                placeholder="What are you looking for?"
                required
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
              />
            </div>
          </form>
        </div>
        <h1 id="listingsHeading">
          Listings for “{searchTerm.trim() || ' '}”
        </h1>
        <div id="listingsGrid" className="grid">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="listing-item" />
          ))}
        </div>
      </main>
    </>
  );
}
