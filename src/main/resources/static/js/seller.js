(async () => {
  // 1) grab sellerId from URL
  const params   = new URLSearchParams(location.search);
  const sellerId = params.get('sellerId');

  // 2) fetch seller’s user info
  let res = await fetch(`/api/users/${sellerId}`);
  if (!res.ok) {
    document.getElementById('seller-name').textContent = 'Seller not found';
    return;
  }
  const user = await res.json();
  document.getElementById('seller-name').textContent = user.username;

  // 3) fetch ratings summary + list
  res = await fetch(`/api/ratings/seller/${sellerId}`);
  const { average, ratings } = await res.json();
  document.getElementById('seller-summary').textContent =
    `${average.toFixed(1)} ★ (${ratings.length} reviews)`;

  // —— INSERTED HERE: check current user’s role ——
  res = await fetch(`/api/users/me`);
  if (res.ok) {
    const me = await res.json();
    // normalize to lower‑case so 'buyer' matches your ENUM
    if (me.role.toLowerCase() === 'buyer') {
      const container = document.getElementById('rating-form-container');
      container.innerHTML = `
        <form id="rating-form">
          <div class="star-picker">
            ${[1,2,3,4,5].map(n =>
              `<span class="star" data-value="${n}">☆</span>`
            ).join('')}
          </div>
          <textarea id="comment" placeholder="Write a review…"></textarea>
          <button type="submit" disabled>Submit</button>
        </form>
      `;
      const stars = [...container.querySelectorAll('.star')];
      let selected = 0;

      stars.forEach(s => s.addEventListener('click', () => {
        selected = +s.dataset.value;
        stars.forEach(t => t.textContent = t.dataset.value <= selected ? '★' : '☆');
        container.querySelector('button').disabled = selected === 0;
      }));

      container.querySelector('form').addEventListener('submit', async e => {
        e.preventDefault();
        const comment = container.querySelector('#comment').value;
        const resp = await fetch('/api/ratings', {
          method: 'POST',
          headers: { 'Content-Type':'application/json' },
          body: JSON.stringify({ sellerId:+sellerId, stars:selected, comment })
        });
        if (resp.ok) location.reload();  // refresh to show the new review
        else alert('Failed to submit review');
      });
    }
  }
  // — end of inserted block —

  // 4) render the existing reviews
  const listEl = document.getElementById('ratings-list');
  if (!ratings.length) {
    listEl.textContent = 'No reviews yet.';
  } else {
    listEl.innerHTML = ratings.map(r => `
      <div class="rating-item">
        <div class="star-display">
          ${'★'.repeat(r.stars)}${'☆'.repeat(5-r.stars)}
        </div>
        <p>${r.comment}</p>
        <small>by ${r.buyerUsername} on
          ${new Date(r.createdAt).toLocaleDateString()}</small>
      </div>
    `).join('');
  }
})();
