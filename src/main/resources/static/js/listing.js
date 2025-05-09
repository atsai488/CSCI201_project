function listingDetails(id){
	
	const name = document.getElementById("product-name");
	const img1 = document.getElementById("product-image1");
	const img2 = document.getElementById("product-image2");
	const img3 = document.getElementById("product-image3");
	const description = document.getElementById("product-description");
	const price = document.getElementById("product-price");
	
	fetch(`/listing-details-servlet?id=${id}`)
	.then(res => res.json())
	.then(data => data.listings[0])
	.then(listing => {
		console.log("got fetch");
		name.textContent += listing.product_name;
		img1.src = listing.image1;
		img2.src = listing.image2;
		img3.src = listing.image3;
		description.textContent += listing.description;
		price.textContent += listing.price;	
	})
	.catch(error => {
		console.error(error);
	});
}

window.onload = function () {
	const url = window.location.pathname.split("/");
	const id = url[url.length - 1];
	listingDetails(id);
	console.log("Script loaded");
}