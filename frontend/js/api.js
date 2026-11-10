/**
 * KEC ONLINE NOTICE BOARD - SHARED CLIENT & API SERVICE
 * Fully compatible with:
 *  1. Spring Boot Backend (http://localhost:8080)
 *  2. GitHub Pages Live Website (https://<user>.github.io/<repo>/)
 *  3. VS Code Live Server / Local Browser
 */

const IS_GITHUB_PAGES = window.location.hostname.includes('github.io');
const API_BASE_URL = window.location.origin + '/api';

const TOKEN_KEY = 'kec_noticeboard_jwt';
const USER_KEY = 'kec_noticeboard_user';
const NOTICES_STORAGE_KEY = 'kec_online_notices_data_v2';

// 6 Realistic Initial Sample Notices
const DEFAULT_SAMPLE_NOTICES = [
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
    attachmentFileType: "application/pdf",
    postedBy: "Controller of Examinations",
    viewCount: 142
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
    attachmentFileType: null,
    postedBy: "Placement & Training Cell",
    viewCount: 289
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
    attachmentFileType: null,
    postedBy: "CSE Association & HOD",
    viewCount: 95
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
    attachmentFileType: null,
    postedBy: "Principal Office",
    viewCount: 310
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
    attachmentFileType: null,
    postedBy: "Academic Coordinator",
    viewCount: 114
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
    attachmentFileType: null,
    postedBy: "Administrative Officer",
    viewCount: 78
  }
];

// Local Repository for Client-Side Storage
const LocalRepo = {
  init() {
    const data = localStorage.getItem(NOTICES_STORAGE_KEY);
    if (!data) {
      localStorage.setItem(NOTICES_STORAGE_KEY, JSON.stringify(DEFAULT_SAMPLE_NOTICES));
    }
  },

  getAll() {
    this.init();
    try {
      return JSON.parse(localStorage.getItem(NOTICES_STORAGE_KEY)) || DEFAULT_SAMPLE_NOTICES;
    } catch (e) {
      return DEFAULT_SAMPLE_NOTICES;
    }
  },

  saveAll(notices) {
    localStorage.setItem(NOTICES_STORAGE_KEY, JSON.stringify(notices));
  },

  getPublished() {
    return this.getAll().filter(n => n.published);
  },

  getById(id) {
    const list = this.getAll();
    const notice = list.find(n => n.id == id);
    if (notice) {
      notice.viewCount = (notice.viewCount || 0) + 1;
      this.saveAll(list);
    }
    return notice;
  },

  add(noticeData) {
    const list = this.getAll();
    const newNotice = {
      ...noticeData,
      id: Date.now(),
      viewCount: 0,
      createdAt: new Date().toISOString()
    };
    list.unshift(newNotice);
    this.saveAll(list);
    return newNotice;
  },

  update(id, updateData) {
    const list = this.getAll();
    const index = list.findIndex(n => n.id == id);
    if (index !== -1) {
      list[index] = { ...list[index], ...updateData, id: Number(id) };
      this.saveAll(list);
      return list[index];
    }
    return null;
  },

  delete(id) {
    let list = this.getAll();
    list = list.filter(n => n.id != id);
    this.saveAll(list);
  },

  togglePublish(id) {
    const list = this.getAll();
    const notice = list.find(n => n.id == id);
    if (notice) {
      notice.published = !notice.published;
      this.saveAll(list);
      return notice;
    }
    return null;
  },

  getStats() {
    const list = this.getAll();
    const total = list.length;
    const published = list.filter(n => n.published).length;
    const draft = total - published;
    const urgent = list.filter(n => n.priority === 'URGENT').length;
    return {
      totalNotices: total,
      publishedNotices: published,
      draftNotices: draft,
      urgentNotices: urgent
    };
  }
};

// Ensure storage is initialized on script load
LocalRepo.init();

// Session & Authentication Helper
const Auth = {
  setSession(token, user) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },

  getToken() {
    return localStorage.getItem(TOKEN_KEY);
  },

  getUser() {
    try {
      return JSON.parse(localStorage.getItem(USER_KEY));
    } catch (e) {
      return null;
    }
  },

  isAuthenticated() {
    return !!this.getToken();
  },

  clearSession() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },

  requireAuth(redirectUrl = 'login.html') {
    if (!this.isAuthenticated()) {
      window.location.href = redirectUrl;
    }
  }
};

/**
 * Universal API Request Wrapper
 * Checks for live Spring Boot server; falls back gracefully to LocalRepo on GitHub Pages or static mode.
 */
async function apiRequest(endpoint, options = {}) {
  // If hosted on GitHub Pages or static host, handle via LocalRepo immediately
  if (IS_GITHUB_PAGES || window.location.protocol === 'file:') {
    return handleLocalApiMock(endpoint, options);
  }

  const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
  const headers = { ...options.headers };

  const token = Auth.getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  if (options.body && !(options.body instanceof FormData) && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }

  try {
    const response = await fetch(url, { ...options, headers });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    // If backend connection fails, fall back to LocalRepo seamlessly
    return handleLocalApiMock(endpoint, options);
  }
}

/**
 * Mock Handler for GitHub Pages & Offline Mode
 */
function handleLocalApiMock(endpoint, options = {}) {
  const method = (options.method || 'GET').toUpperCase();

  // Public: Get published notices
  if (endpoint.startsWith('/notices') && method === 'GET') {
    if (endpoint === '/notices' || endpoint.startsWith('/notices?')) {
      return { success: true, data: LocalRepo.getPublished() };
    }
    if (endpoint === '/notices/recent') {
      return { success: true, data: LocalRepo.getPublished().slice(0, 5) };
    }
    const matchId = endpoint.match(/\/notices\/(\d+)/);
    if (matchId) {
      const notice = LocalRepo.getById(matchId[1]);
      return notice ? { success: true, data: notice } : { success: false, message: 'Notice not found' };
    }
  }

  // Admin: Get all notices
  if (endpoint.startsWith('/admin/notices') && method === 'GET') {
    return { success: true, data: LocalRepo.getAll() };
  }

  // Admin: Create notice
  if (endpoint === '/admin/notices' && method === 'POST') {
    const body = JSON.parse(options.body || '{}');
    const created = LocalRepo.add(body);
    return { success: true, message: 'Notice published', data: created };
  }

  // Admin: Update notice
  if (endpoint.startsWith('/admin/notices/') && method === 'PUT') {
    const id = endpoint.split('/')[3];
    const body = JSON.parse(options.body || '{}');
    const updated = LocalRepo.update(id, body);
    return { success: true, message: 'Notice updated', data: updated };
  }

  // Admin: Delete notice
  if (endpoint.startsWith('/admin/notices/') && method === 'DELETE') {
    const id = endpoint.split('/')[3];
    LocalRepo.delete(id);
    return { success: true, message: 'Notice deleted' };
  }

  // Admin: Toggle publish
  if (endpoint.includes('/publish') && (method === 'PATCH' || method === 'POST')) {
    const match = endpoint.match(/\/notices\/(\d+)\/publish/);
    if (match) {
      const updated = LocalRepo.togglePublish(match[1]);
      return { success: true, message: updated.published ? 'Notice published' : 'Notice set to draft', data: updated };
    }
  }

  // Admin: Stats
  if (endpoint === '/admin/stats') {
    return { success: true, data: LocalRepo.getStats() };
  }

  // Auth: Login
  if (endpoint === '/auth/login' && method === 'POST') {
    const body = JSON.parse(options.body || '{}');
    if (body.username === 'admin' && body.password === 'admin123') {
      return {
        success: true,
        message: 'Login successful',
        token: 'github_pages_demo_jwt_' + Date.now(),
        username: 'admin',
        fullName: 'KEC Admin Controller',
        role: 'ROLE_ADMIN'
      };
    } else {
      return { success: false, message: 'Invalid credentials! Use admin / admin123' };
    }
  }

  return { success: true, data: [] };
}

/**
 * Toast Notification Helper
 */
function showToast(message, type = 'info', duration = 3500) {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;

  let icon = 'ℹ️';
  if (type === 'success') icon = '✅';
  if (type === 'error') icon = '⚠️';

  toast.innerHTML = `
    <span style="font-size: 1.1rem;">${icon}</span>
    <span>${message}</span>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => {
      if (toast.parentElement) toast.remove();
    }, 300);
  }, duration);
}

/**
 * Helper: Format Date
 */
function formatDate(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return dateStr;
  return d.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  });
}

/**
 * Helper: Category Badges
 */
function getCategoryBadge(category) {
  switch (category) {
    case 'EXAMINATION':
      return { class: 'badge-category', label: 'Examination', icon: '📝' };
    case 'PLACEMENTS':
      return { class: 'badge-category', label: 'Placements', icon: '💼' };
    case 'EVENTS':
      return { class: 'badge-category', label: 'Events', icon: '🎯' };
    case 'HOLIDAYS':
      return { class: 'badge-category', label: 'Holidays', icon: '📅' };
    case 'DEPARTMENT':
      return { class: 'badge-category', label: 'Department', icon: '🏛️' };
    default:
      return { class: 'badge-category', label: 'General', icon: '📢' };
  }
}

/**
 * Helper: Priority Badges
 */
function getPriorityBadge(priority) {
  switch (priority) {
    case 'URGENT':
      return { class: 'badge-urgent', label: 'Urgent', borderClass: 'urgent-border' };
    case 'HIGH':
      return { class: 'badge-high', label: 'High', borderClass: 'high-border' };
    default:
      return { class: 'badge-normal', label: 'Normal', borderClass: 'normal-border' };
  }
}
