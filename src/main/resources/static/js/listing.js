const formatName = s => s.replace(/([a-z])([A-Z])/g, '$1 $2');

function listingDetails(id) {
  const name = document.getElementById("product-name");
  const img1 = document.getElementById("product-image1");
  const img2 = document.getElementById("product-image2");
  const img3 = document.getElementById("product-image3");
  const description = document.getElementById("product-description");
  const price = document.getElementById("product-price");
  const sellerLink  = document.getElementById("seller-profile-link");

  fetch(`/listing-details-servlet?id=${id}`)
    .then(res => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return res.json();
    })
    .then(data => {
      if (!data.listings || data.listings.length === 0) {
        throw new Error("No listing found");
      }
      return data.listings[0];
    })
    .then(listing => {
      console.log("got fetch", listing);
      name.textContent += listing.product_name;
      img1.src = listing.image1;
      img2.src = listing.image2;
      img3.src = listing.image3;
      description.textContent += listing.description;
      price.textContent += listing.price;

      // format the seller’s camel‑case name
      sellerLink.textContent = formatName(listing.sellerUsername);
      sellerLink.href        = `/seller.html?sellerId=${listing.sellerId}`;
    })
    .catch(error => {
      console.error("Failed to load listing:", error);
    });
}

window.onload = function () {
  const parts = window.location.pathname.split("/");
  const id    = parts[parts.length - 1];
  listingDetails(id);
  console.log("listing.js loaded");
};