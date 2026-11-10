/**
 * KEC ONLINE NOTICE BOARD - HOME PAGE LOGIC
 */

// Fallback initial sample data in case frontend is run without Spring Boot backend running
const FALLBACK_NOTICES = [
  {
    id: 1,
    title: "End Semester Autonomous Theory & Practical Examinations - Time Table",
    description: "All students of II, III, and IV year B.E./B.Tech/MBA/MCA are hereby informed that the Autonomous End Semester Examinations scheduled for Dec 2026 will commence from 24th November. Detailed hall allocations and session timings are attached. Hall tickets can be downloaded from the student portal starting this Monday.",
    category: "EXAMINATION",
    categoryDisplayName: "Examination",
    priority: "URGENT",
    noticeDate: new Date().toISOString().split('T')[0],
    department: "All Departments",
    published: true,
    attachmentUrl: "uploads/End_Semester_Exam_TimeTable_2026.pdf",
    attachmentFileName: "End_Semester_Exam_TimeTable_2026.pdf",
    postedBy: "Controller of Examinations"
  },
  {
    id: 2,
    title: "Campus Placement Drive: Zoho Corporation Software Development Engineer",
    description: "Zoho Corporation is visiting KEC for on-campus recruitment for final year B.E./B.Tech (CSE, IT, ECE, EEE, AI&DS) students. Eligible criteria: CGPA 7.0 and above with no current standing arrears. CTC offered: 8.5 LPA to 12 LPA. Round 1 Online Aptitude & Coding Assessment will take place on Saturday at CC3 Lab.",
    category: "PLACEMENTS",
    categoryDisplayName: "Placements",
    priority: "HIGH",
    noticeDate: new Date().toISOString().split('T')[0],
    department: "CSE, IT, ECE, EEE, AI&DS",
    published: true,
    attachmentUrl: null,
    attachmentFileName: null,
    postedBy: "Placement & Training Cell"
  },
  {
    id: 3,
    title: "Annual National Technical Symposium 'INNOVFEST 2026' - Call for Papers",
    description: "The Department of Computer Science & Engineering is organizing the National Level Technical Symposium INNOVFEST 2026 on 18th December 2026. Events include Paper Presentation, Hackathon, Web-A-Thon, Code Debugging, and Gaming. Cash prizes worth Rs. 50,000 to be won. Early bird registration closes on 30th November.",
    category: "EVENTS",
    categoryDisplayName: "Events",
    priority: "NORMAL",
    noticeDate: new Date(Date.now() - 86400000).toISOString().split('T')[0],
    department: "Computer Science and Engineering",
    published: true,
    attachmentUrl: null,
    attachmentFileName: null,
    postedBy: "CSE Association & HOD"
  },
  {
    id: 4,
    title: "Declaration of Holiday on Account of National Youth Day Celebrations",
    description: "As per the circular from Anna University and District Administration, the college will remain closed on Monday, 12th January in view of National Youth Day. Special community service events organized by the NSS and YRC units will proceed as planned in the Convention Center.",
    category: "HOLIDAYS",
    categoryDisplayName: "Holidays",
    priority: "NORMAL",
    noticeDate: new Date(Date.now() - 172800000).toISOString().split('T')[0],
    department: "All Departments",
    published: true,
    attachmentUrl: null,
    attachmentFileName: null,
    postedBy: "Principal Office"
  },
  {
    id: 5,
    title: "Department Level Project Expo & Capstone Phase-I Review Schedule",
    description: "All final year students are requested to be present in their respective project laboratories with their PPT slides and hardware/software prototypes for the Capstone Project Phase-I internal review. External industry mentors will be participating in the evaluation process. Formal dress code is mandatory.",
    category: "DEPARTMENT",
    categoryDisplayName: "Department",
    priority: "HIGH",
    noticeDate: new Date(Date.now() - 259200000).toISOString().split('T')[0],
    department: "Department of ECE & Mech",
    published: true,
    attachmentUrl: null,
    attachmentFileName: null,
    postedBy: "Academic Coordinator"
  },
  {
    id: 6,
    title: "Mandatory Biometric ID Card Revalidation and Bus Pass Distribution",
    description: "Students availing college transport are instructed to get their smart RFID bus passes revalidated at the Transport Office (Room No: Admin 104) before 25th of this month. Late renewals will attract a nominal fine. Carry your recent college identity card and fee receipt.",
    category: "GENERAL",
    categoryDisplayName: "General",
    priority: "NORMAL",
    noticeDate: new Date(Date.now() - 345600000).toISOString().split('T')[0],
    department: "All Departments",
    published: true,
    attachmentUrl: null,
    attachmentFileName: null,
    postedBy: "Administrative Officer"
  }
];

// Initialize Home Page
document.addEventListener('DOMContentLoaded', () => {
  setupNavigation();
  loadHomeNotices();
});

function setupNavigation() {
  const mobileBtn = document.getElementById('mobileMenuBtn');
  const navLinks = document.getElementById('navLinks');
  if (mobileBtn && navLinks) {
    mobileBtn.addEventListener('click', () => {
      navLinks.classList.toggle('mobile-open');
    });
  }

  // Update Admin link if already authenticated
  const navAuthBtn = document.getElementById('navAuthBtn');
  if (navAuthBtn && Auth.isAuthenticated()) {
    navAuthBtn.innerText = 'Admin Dashboard';
    navAuthBtn.href = 'admin-dashboard.html';
    navAuthBtn.className = 'btn btn-primary';
  }
}

async function loadHomeNotices() {
  let notices = [];
  try {
    const res = await apiRequest('/notices');
    if (res && res.success && Array.isArray(res.data)) {
      notices = res.data;
    } else {
      throw new Error('API returned invalid structure');
    }
  } catch (err) {
    console.warn('Backend API unavailable. Using fallback localStorage / sample notices.', err);
    const stored = localStorage.getItem('kec_local_notices');
    if (stored) {
      try {
        notices = JSON.parse(stored).filter(n => n.published);
      } catch (e) {
        notices = FALLBACK_NOTICES;
      }
    } else {
      notices = FALLBACK_NOTICES;
      localStorage.setItem('kec_local_notices', JSON.stringify(FALLBACK_NOTICES));
    }
  }

  updateHeroStats(notices);
  renderMarqueeTicker(notices);
  renderLatestNotices(notices.slice(0, 6));
}

function updateHeroStats(notices) {
  const total = notices.length;
  const examCount = notices.filter(n => n.category === 'EXAMINATION').length;
  const placementCount = notices.filter(n => n.category === 'PLACEMENTS').length;
  const eventsCount = notices.filter(n => n.category === 'EVENTS').length;

  document.getElementById('heroTotalNotices').innerText = total;
  document.getElementById('heroExamNotices').innerText = examCount;
  document.getElementById('heroPlacementNotices').innerText = placementCount;
  document.getElementById('heroEventsNotices').innerText = eventsCount;
}

function renderMarqueeTicker(notices) {
  const tickerContainer = document.getElementById('heroTickerContent');
  if (!tickerContainer) return;

  if (notices.length === 0) {
    tickerContainer.innerHTML = '<span>No announcements posted at this time.</span>';
    return;
  }

  const tickerHtml = notices.slice(0, 5).map(n => {
    return `<a href="javascript:void(0)" onclick="openNoticeModalById(${n.id})">🔔 ${n.title} (${formatDate(n.noticeDate)})</a>`;
  }).join(' &nbsp;&bull;&nbsp; ');

  tickerContainer.innerHTML = tickerHtml;
}

function renderLatestNotices(notices) {
  const grid = document.getElementById('latestNoticesGrid');
  if (!grid) return;

  if (notices.length === 0) {
    grid.innerHTML = `
      <div class="empty-state">
        <div class="empty-state-icon">📋</div>
        <h3>No Notices Available</h3>
        <p>Check back later for new college circulars</p>
      </div>
    `;
    return;
  }

  grid.innerHTML = notices.map(notice => {
    const catBadge = getCategoryBadge(notice.category);
    const priBadge = getPriorityBadge(notice.priority);

    return `
      <div class="notice-card ${priBadge.borderClass}" onclick="openNoticeModalById(${notice.id})">
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
            Read Full Notice & Details →
          </button>
        </div>
      </div>
    `;
  }).join('');
}

// Global modal opener for notice
async function openNoticeModalById(noticeId) {
  let notice = null;

  try {
    const res = await apiRequest(`/notices/${noticeId}`);
    if (res && res.success && res.data) {
      notice = res.data;
    }
  } catch (err) {
    // Search in localStorage
    const stored = localStorage.getItem('kec_local_notices');
    if (stored) {
      const list = JSON.parse(stored);
      notice = list.find(n => n.id == noticeId);
    }
    if (!notice) {
      notice = FALLBACK_NOTICES.find(n => n.id == noticeId);
    }
  }

  if (!notice) {
    showToast('Notice not found', 'error');
    return;
  }

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
    attachName.innerText = notice.attachmentFileName || 'Official_Document.pdf';
    attachBtn.href = notice.attachmentUrl;
  } else {
    attachSec.style.display = 'none';
  }

  modal.classList.add('active');
}

// Modal closing setup
document.addEventListener('DOMContentLoaded', () => {
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
});

function escapeHtml(text) {
  if (!text) return '';
  return text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}
