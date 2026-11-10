/**
 * KEC ONLINE NOTICE BOARD - STUDENT NOTICES PORTAL LOGIC
 */

let allNotices = [];
let currentCategory = '';
let currentPriority = '';
let currentSearch = '';
let currentSort = 'latest';

document.addEventListener('DOMContentLoaded', () => {
  setupNavigation();
  initUrlParams();
  setupFilterListeners();
  loadStudentNotices();
});

function setupNavigation() {
  const mobileBtn = document.getElementById('mobileMenuBtn');
  const navLinks = document.getElementById('navLinks');
  if (mobileBtn && navLinks) {
    mobileBtn.addEventListener('click', () => {
      navLinks.classList.toggle('mobile-open');
    });
  }

  const navAuthBtn = document.getElementById('navAuthBtn');
  if (navAuthBtn && Auth.isAuthenticated()) {
    navAuthBtn.innerText = 'Admin Dashboard';
    navAuthBtn.href = 'admin-dashboard.html';
    navAuthBtn.className = 'btn btn-primary';
  }
}

function initUrlParams() {
  const params = new URLSearchParams(window.location.search);
  const cat = params.get('category');
  if (cat) {
    currentCategory = cat.toUpperCase();
    const pills = document.querySelectorAll('.category-pill');
    pills.forEach(p => {
      if (p.getAttribute('data-category') === currentCategory) {
        p.classList.add('active');
      } else {
        p.classList.remove('active');
      }
    });
  }
}

function setupFilterListeners() {
  // Category Pills
  const pills = document.querySelectorAll('.category-pill');
  pills.forEach(pill => {
    pill.addEventListener('click', () => {
      pills.forEach(p => p.classList.remove('active'));
      pill.classList.add('active');
      currentCategory = pill.getAttribute('data-category') || '';
      applyFilters();
    });
  });

  // Search Input with dynamic debounce
  const searchInput = document.getElementById('searchInput');
  let searchTimeout = null;
  searchInput.addEventListener('input', (e) => {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
      currentSearch = e.target.value.trim().toLowerCase();
      applyFilters();
    }, 250);
  });

  // Priority Filter
  const priorityFilter = document.getElementById('priorityFilter');
  priorityFilter.addEventListener('change', (e) => {
    currentPriority = e.target.value;
    applyFilters();
  });

  // Sort Filter
  const sortFilter = document.getElementById('sortFilter');
  sortFilter.addEventListener('change', (e) => {
    currentSort = e.target.value;
    applyFilters();
  });

  // Reset Button
  const resetBtn = document.getElementById('resetFiltersBtn');
  resetBtn.addEventListener('click', () => {
    searchInput.value = '';
    priorityFilter.value = '';
    sortFilter.value = 'latest';
    currentSearch = '';
    currentPriority = '';
    currentSort = 'latest';
    currentCategory = '';
    pills.forEach(p => {
      if (p.getAttribute('data-category') === '') p.classList.add('active');
      else p.classList.remove('active');
    });
    applyFilters();
  });

  // Modal close handlers
  const modal = document.getElementById('noticeModal');
  const closeBtn = document.getElementById('modalCloseBtn');
  const closeFooterBtn = document.getElementById('modalCloseFooterBtn');

  const closeModal = () => {
    if (modal) modal.classList.remove('active');
  };

  if (closeBtn) closeBtn.addEventListener('click', closeModal);
  if (closeFooterBtn) closeFooterBtn.addEventListener('click', closeModal);
  if (modal) {
    modal.addEventListener('click', (e) => {
      if (e.target === modal) closeModal();
    });
  }
}

async function loadStudentNotices() {
  try {
    const res = await apiRequest('/notices');
    if (res && res.success && Array.isArray(res.data)) {
      allNotices = res.data;
    } else {
      throw new Error('Invalid response');
    }
  } catch (err) {
    console.warn('Backend API unavailable. Using fallback data.', err);
    const stored = localStorage.getItem('kec_local_notices');
    if (stored) {
      allNotices = JSON.parse(stored).filter(n => n.published);
    } else {
      allNotices = FALLBACK_NOTICES.filter(n => n.published);
    }
  }

  renderMarquee(allNotices);
  applyFilters();
}

function renderMarquee(notices) {
  const ticker = document.getElementById('heroTickerContent');
  if (!ticker) return;

  if (notices.length === 0) {
    ticker.innerHTML = '<span>No urgent notifications at this time.</span>';
    return;
  }

  ticker.innerHTML = notices.slice(0, 5).map(n => {
    return `<a href="javascript:void(0)" onclick="openNoticeModal(${n.id})">🔔 ${n.title}</a>`;
  }).join(' &nbsp;&bull;&nbsp; ');
}

function applyFilters() {
  let filtered = [...allNotices];

  // Category filter
  if (currentCategory) {
    filtered = filtered.filter(n => n.category === currentCategory);
  }

  // Priority filter
  if (currentPriority) {
    filtered = filtered.filter(n => n.priority === currentPriority);
  }

  // Search keyword (matches title, description, or department)
  if (currentSearch) {
    filtered = filtered.filter(n => {
      const matchTitle = n.title && n.title.toLowerCase().includes(currentSearch);
      const matchDesc = n.description && n.description.toLowerCase().includes(currentSearch);
      const matchDept = n.department && n.department.toLowerCase().includes(currentSearch);
      return matchTitle || matchDesc || matchDept;
    });
  }

  // Sort
  if (currentSort === 'latest') {
    filtered.sort((a, b) => new Date(b.noticeDate) - new Date(a.noticeDate));
  } else if (currentSort === 'oldest') {
    filtered.sort((a, b) => new Date(a.noticeDate) - new Date(b.noticeDate));
  }

  renderNoticesGrid(filtered);

  // Update badge count
  const countBadge = document.getElementById('noticeCountBadge');
  if (countBadge) {
    countBadge.innerText = `Showing ${filtered.length} of ${allNotices.length} notices`;
  }
}

function renderNoticesGrid(notices) {
  const grid = document.getElementById('studentNoticesGrid');
  if (!grid) return;

  if (notices.length === 0) {
    grid.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">🔍</div>
        <h3>No Notices Found</h3>
        <p>No circulars match your active search and filter criteria. Try clearing filters.</p>
        <button class="btn btn-secondary" onclick="document.getElementById('resetFiltersBtn').click()" style="margin-top: 1rem;">
          Reset All Filters ↻
        </button>
      </div>
    `;
    return;
  }

  grid.innerHTML = notices.map(notice => {
    const catBadge = getCategoryBadge(notice.category);
    const priBadge = getPriorityBadge(notice.priority);

    return `
      <div class="notice-card ${priBadge.borderClass}" onclick="openNoticeModal(${notice.id})">
        <div class="notice-card-header">
          <div class="notice-badges">
            <span class="badge ${priBadge.class}">${priBadge.label}</span>
            <span class="badge ${catBadge.class}">${catBadge.icon} ${catBadge.label}</span>
          </div>
          <span class="notice-date">📅 ${formatDate(notice.noticeDate)}</span>
        </div>

        <h3 class="notice-card-title">${escapeHtml(notice.title)}</h3>
        <p class="notice-card-desc">${escapeHtml(notice.description)}</p>

        <div class="notice-card-meta">
          <span class="notice-dept">🏛️ ${escapeHtml(notice.department || 'All Departments')}</span>
          ${notice.attachmentUrl ? `<span class="notice-attachment-tag">📎 Attachment</span>` : ''}
        </div>

        <div class="notice-card-footer">
          <button class="btn btn-secondary" style="width: 100%; font-size: 0.82rem; padding: 0.45rem;">
            View Details & Circular →
          </button>
        </div>
      </div>
    `;
  }).join('');
}

async function openNoticeModal(noticeId) {
  let notice = allNotices.find(n => n.id == noticeId);

  // Try fetching latest with view count increment
  try {
    const res = await apiRequest(`/notices/${noticeId}`);
    if (res && res.success && res.data) {
      notice = res.data;
    }
  } catch (e) {
    // fallback uses existing notice in memory
  }

  if (!notice) return;

  const modal = document.getElementById('noticeModal');
  const catBadge = getCategoryBadge(notice.category);
  const priBadge = getPriorityBadge(notice.priority);

  document.getElementById('modalNoticeTitle').innerText = notice.title;
  document.getElementById('modalNoticeCategory').innerHTML = `${catBadge.icon} ${catBadge.label}`;
  document.getElementById('modalNoticePriority').innerText = priBadge.label;
  document.getElementById('modalNoticeDate').innerText = formatDate(notice.noticeDate);
  document.getElementById('modalNoticeDept').innerText = notice.department || 'All Departments';
  document.getElementById('modalNoticeDesc').innerText = notice.description;
  document.getElementById('modalNoticePostedBy').innerText = notice.postedBy || 'Principal Office';

  const attachSec = document.getElementById('modalAttachmentSection');
  const attachName = document.getElementById('modalAttachmentName');
  const attachBtn = document.getElementById('modalAttachmentDownloadBtn');

  if (notice.attachmentUrl) {
    attachSec.style.display = 'block';
    attachName.innerText = notice.attachmentFileName || 'Official_Circular.pdf';
    attachBtn.href = notice.attachmentUrl;
  } else {
    attachSec.style.display = 'none';
  }

  modal.classList.add('active');
}

function escapeHtml(text) {
  if (!text) return '';
  return text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}
