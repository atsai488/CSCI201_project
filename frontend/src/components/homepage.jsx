import React, { useState, useEffect } from 'react';
import '@fortawesome/fontawesome-free/css/all.min.css';
import '../css/style.css'; 

export default function Homepage() {
  const [userRole, setUserRole] = useState('');
  const [profileOpen, setProfileOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [listings, setListings] = useState([]);
  
  const viewListing = (listing) => {
  	window.location.href = `/listing/${listing.id}`;
  }

  const fetchInitialListings = async () => {
        try {
          const response = await fetch('/default-listings-servlet');
          if (response.error) throw new Error('Failed to fetch initial listings');
          const data = await response.json();
          setListings(data.listings || []);
        } catch (error) {
          console.error('Error fetching initial listings:', error);
          setListings([]);
        }
      };
  
  useEffect(() => {
    fetchInitialListings();
	
	async function getUserRole() {
		const email = localStorage.getItem("email");
			if(!email){
				window.location.href = "/login";
			}
			try {
				const response = await fetch(`/get-user-role?email=${encodeURIComponent(email)}`);
				if (response.error) throw new Error('Failed to fetch user role');
				const data = await response.json();
				if(data.role === "fail"){
					// Do nothing, keep user role empty
				}
				else{
					setUserRole(data.role);	
				}
			} catch (error) {
				console.error('Error fetching user role:', error);
			}	
	}
	
	getUserRole();
  }, []);
  return (
    <>
      <header>
		<div className="header" onClick={fetchInitialListings} style={{cursor: "pointer"}}>
		  USC Marketplace
		</div>
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
			style={{ display: userRole !== 'guest' ? 'inline-block' : 'none' }}
            onClick={() => (window.location.href = '/messages')}
          />
		  <i
	        id="loginIcon"
	        className="fa fa-sign-in-alt"
			style={{ display: userRole ==='guest' ? 'inline-block' : 'none' }}
	        onClick={() => {
				localStorage.removeItem("email");
				window.location.href = '/login';
			}}
	      />
		  <i
  	        id="registerIcon"
  	        className="fa fa-user-plus"
  			style={{ display: userRole ==='guest' ? 'inline-block' : 'none' }}
  	        onClick={() => {
				localStorage.removeItem("email");
				window.location.href = '/register';
			}}
  	      />
          <div className="profile-container">
            <i
              id="profileIcon"
              className="fa fa-user"
			  style={{ display: userRole !== 'guest' ? 'inline-block' : 'none' }}
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
					onClick={() => {
						localStorage.removeItem("email");
						window.location.href = '/logout';
					}}
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
            onSubmit={async (e) => {
              e.preventDefault();
              document.getElementById('listingsHeading').textContent = `Listings for “${searchTerm.trim()}”`;

              try {
                const response = await fetch(`/search-listings-servlet?search=${encodeURIComponent(searchTerm.trim())}`);
                if (response.error) throw new Error('Search failed');
                const data = await response.json();
                setListings(data.listings || []);
              } catch (error) {
                console.error('Error fetching search results:', error);
                setListings([]);
              }
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
          {listings.length > 0 ? (
            listings.map(listing => (
              <div key={listing.id} className="listing-item"
			  	   onClick={() => viewListing(listing)}
				   style={{cursor: "pointer"}} >
                <img
                  src={listing.image1}
                  alt={listing.product_name}
                  className="listing-image"
                />
                <h2 className="listing-title">{listing.product_name}</h2>
                <p className="listing-price">${listing.price}</p>
              </div>
            ))
          ) : (
            <p>No listings found.</p>
          )}
        </div>
      </main>
    </>
  );
}
