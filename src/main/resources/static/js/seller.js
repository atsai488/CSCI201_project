(async () => {
  // ——— 0) helper to split camelCase names ———
  const formatName = s => s.replace(/([a-z])([A-Z])/g, '$1 $2');

  // ——— 1) grab sellerId from URL ——————————
  const params   = new URLSearchParams(location.search);
  const sellerId = params.get('sellerId');

  // ——— 2) HEADER ICONS (incl. profile redirect) ———
  let headerRes = await fetch('/api/users/me'),
      role      = 'guest';
  if (headerRes.ok) {
    const me = await headerRes.json();
    role = me.role.toLowerCase();
  }

  document.getElementById('addIcon').style.display      = role === 'seller' ? 'inline-block' : 'none';
  document.getElementById('messageIcon').style.display  = role !== 'guest'  ? 'inline-block' : 'none';
  document.getElementById('loginIcon').style.display    = role === 'guest'  ? 'inline-block' : 'none';
  document.getElementById('registerIcon').style.display = role === 'guest'  ? 'inline-block' : 'none';
  document.getElementById('profileIcon').style.display  = role !== 'guest'  ? 'inline-block' : 'none';
  document.getElementById('logoutIcon').style.display   = role !== 'guest'  ? 'inline-block' : 'none';

  document.getElementById('addIcon').onclick      = () => window.location.href = '/createlisting';
  document.getElementById('messageIcon').onclick  = () => window.location.href = '/messages';
  document.getElementById('loginIcon').onclick    = () => { localStorage.removeItem('email'); window.location.href = '/login'; };
  document.getElementById('registerIcon').onclick = () => { localStorage.removeItem('email'); window.location.href = '/register'; };
  document.getElementById('profileIcon').onclick  = () => window.location.href = `/selleraccount?sellerId=${sellerId}`;
  document.getElementById('logoutIcon').onclick   = () => { localStorage.removeItem('email'); window.location.href = '/login'; };

  // ——— 3) fetch seller’s user info ——————————
  let res = await fetch(`/api/users/${sellerId}`);
  if (!res.ok) {
    document.getElementById('seller-name').textContent = 'Seller not found';
    return;
  }
  const user = await res.json();
  document.getElementById('seller-name').textContent = formatName(user.username);

  // ——— 4) fetch ratings summary + distribution chart ———
  res = await fetch(`/api/ratings/seller/${sellerId}`);
  const { average, ratings } = await res.json();
  document.getElementById('seller-summary').textContent =
    `${average.toFixed(1)} ★ (${ratings.length} reviews)`;

  const distContainer = document.getElementById('rating-dist-container');
  const counts = {1:0,2:0,3:0,4:0,5:0};
  ratings.forEach(r => counts[r.stars]++);
  const total = ratings.length;
  distContainer.innerHTML = [5,4,3,2,1].map(star => {
    const cnt = counts[star];
    const pct = total > 0 ? Math.round(cnt / total * 100) : 0;
    return `
      <div class="dist-row">
        <div class="star-label">${star}★</div>
        <div class="dist-bar-container">
          <div class="dist-bar" style="width:${pct}%;"></div>
        </div>
        <div class="dist-count">${cnt}</div>
      </div>
    `;
  }).join('');

  // ——— 5) buyer‑only “write a review” form ———
  res = await fetch(`/api/users/me`);
  if (res.ok) {
    const me = await res.json();
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
          headers: {'Content-Type':'application/json'},
          body: JSON.stringify({ sellerId:+sellerId, stars:selected, comment })
        });
        if (resp.ok)      location.reload();
        else {
          let err = 'Failed to submit review';
          try { const body = await resp.json(); if (body.error) err = body.error; } catch {}
          alert(err);
        }
      });
    }
  }

  // ——— 6) render existing reviews ——————————
  const listEl = document.getElementById('ratings-list');
  if (!ratings.length) {
    listEl.textContent = 'No reviews yet.';
  } else {
    listEl.innerHTML = ratings.map(r => `
      <div class="rating-item">
        <div class="buyer-name">${formatName(r.buyerUsername)}</div>
        <div class="star-display">
          ${'★'.repeat(r.stars)}${'☆'.repeat(5-r.stars)}
        </div>
        <p>${r.comment}</p>
        <small>${new Date(r.createdAt).toLocaleDateString()}</small>
      </div>
    `).join('');
  }

  // ——— 7) fetch & render this seller’s own listings + delete X ———
  res = await fetch(`/api/listings/seller/${sellerId}`);
  if (res.ok) {
    const products = await res.json(); // should be an array
    const el = document.getElementById('seller-listings');
    el.innerHTML = products.map(p => `
      <div class="seller-listing-item" data-id="${p.id}">
        <img src="${p.image1}" alt="${p.product_name}" />
        <button class="delete-btn">×</button>
        <h4>${p.product_name}</h4>
        <p>$${(+p.price).toFixed(2)}</p>
      </div>
    `).join('');
    el.querySelectorAll('.delete-btn').forEach(btn => {
      btn.addEventListener('click', async () => {
        const card = btn.closest('.seller-listing-item');
        const id   = card.dataset.id;
        const resp = await fetch(`/api/listings/${id}`, { method: 'DELETE' });
        if (resp.ok) card.remove();
        else         alert('Failed to delete listing');
      });
    });
  }
})();
