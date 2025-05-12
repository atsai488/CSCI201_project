(async () => {
  const formatName = s => s.replace(/([a-z])([A-Z])/g, '$1 $2');

  // —— HEADER ICONS SETUP ——
  let headerRes = await fetch('/api/users/me');
  let role = 'guest';
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
  document.getElementById('logoutIcon').onclick   = () => { localStorage.removeItem('email'); window.location.href = '/login'; };

  // —— 1) grab sellerId from URL ——
  const params   = new URLSearchParams(location.search);
  const sellerId = params.get('sellerId');

  // —— 2) fetch seller’s user info ——
  let res = await fetch(`/api/users/${sellerId}`);
  if (!res.ok) {
    document.getElementById('seller-name').textContent = 'Seller not found';
    return;
  }
  const user = await res.json();
  document.getElementById('seller-name').textContent = formatName(user.username);

  // —— 3) fetch ratings summary + list ——
  res = await fetch(`/api/ratings/seller/${sellerId}`);
  const { average, ratings } = await res.json();
  document.getElementById('seller-summary').textContent =
    `${average.toFixed(1)} ★ (${ratings.length} reviews)`;

  // —— Render distribution bar chart (1–5) ——
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

  // —— 4) buyer‑only review form ——
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
          headers: { 'Content-Type':'application/json' },
          body: JSON.stringify({ sellerId:+sellerId, stars:selected, comment })
        });
        if (resp.ok) location.reload();
        else {
          let errMsg = 'Failed to submit review';
          try { const err = await resp.json(); if (err.error) errMsg = err.error; } catch {}
          alert(errMsg);
        }
      });
    }
  }

  // —— 5) render the existing reviews ——
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
})();
