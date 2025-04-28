const userRole = "seller";

document.addEventListener("DOMContentLoaded", () => {
  const addIcon      = document.getElementById("addIcon");
  const msgIcon      = document.getElementById("messageIcon");
  const profileIcon  = document.getElementById("profileIcon");
  const dropdown     = document.getElementById("profileDropdown");
  const goToProfile  = document.getElementById("goToProfile");
  const logout       = document.getElementById("logout");

  addIcon.style.display = userRole === "seller" ? "inline-block" : "none";

  addIcon.addEventListener("click", () => {
    if (userRole === "seller") {
      window.location.href = "createlisting.html";
    }
  });

  msgIcon.addEventListener("click", () => {
    window.location.href = "messages.html";
  });

  profileIcon.addEventListener("click", () => {
    dropdown.style.display =
      dropdown.style.display === "block" ? "none" : "block";
  });

  goToProfile.addEventListener("click", () => {
    if (userRole === "seller") {
      window.location.href = "selleraccount.html";
    } else {
      window.location.href = "buyeraccount.html";
    }
  });

  logout.addEventListener("click", () => {
    window.location.href = "logout.html";
  });

  document.getElementById("searchForm").addEventListener("submit", (e) => {
    e.preventDefault();
    const q = document.getElementById("searchInput").value.trim();
    document.getElementById(
      "listingsHeading"
    ).textContent = `Listings for “${q}”`;
  });
});