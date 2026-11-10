/**
 * KEC ONLINE NOTICE BOARD - SHARED API CLIENT & UTILITIES
 */

const API_BASE_URL = window.location.origin + '/api';

const TOKEN_KEY = 'kec_noticeboard_jwt';
const USER_KEY = 'kec_noticeboard_user';

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
 */
async function apiRequest(endpoint, options = {}) {
  const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
  
  const headers = {
    ...options.headers
  };

  // Attach JWT if present
  const token = Auth.getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  // Automatically set Content-Type to JSON if sending raw body and not FormData
  if (options.body && !(options.body instanceof FormData) && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }

  try {
    const response = await fetch(url, {
      ...options,
      headers
    });

    // Handle 401 Unauthorized (session expired or invalid token)
    if (response.status === 401 && Auth.isAuthenticated()) {
      Auth.clearSession();
      showToast('Session expired. Please log in again.', 'error');
      setTimeout(() => {
        window.location.href = 'login.html';
      }, 1200);
      throw new Error('Unauthorized');
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error(`API Error [${endpoint}]:`, error);
    throw error;
  }
}

/**
 * Toast Notification System
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

  let iconSvg = '';
  if (type === 'success') {
    iconSvg = '✓';
  } else if (type === 'error') {
    iconSvg = '⚠';
  } else {
    iconSvg = 'ℹ';
  }

  toast.innerHTML = `
    <span style="font-weight: 800; font-size: 1.1rem;">${iconSvg}</span>
    <span>${message}</span>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => {
      if (toast.parentElement) {
        toast.remove();
      }
    }, 300);
  }, duration);
}

/**
 * Helper: Format Date String to Readable Format
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
 * Helper: Get Category Badge Class
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
 * Helper: Priority Badge Details
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
