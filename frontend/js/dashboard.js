/**
 * KEC ONLINE NOTICE BOARD - ADMIN DASHBOARD LOGIC
 */

let adminNotices = [];
let deleteTargetId = null;

document.addEventListener('DOMContentLoaded', () => {
  // Enforce authentication
  Auth.requireAuth('login.html');

  initUserProfile();
  setupEventListeners();
  loadDashboardData();
});

function initUserProfile() {
  const user = Auth.getUser();
  if (user) {
    document.getElementById('adminFullName').innerText = user.fullName || 'Administrator';
    document.getElementById('adminUsername').innerText = '@' + (user.username || 'admin');
    document.getElementById('adminAvatar').innerText = (user.fullName || user.username || 'A')[0].toUpperCase();
  }
}

function setupEventListeners() {
  // Mobile sidebar toggle
  const sidebar = document.getElementById('dashboardSidebar');
  const toggleBtn = document.getElementById('sidebarToggleBtn');
  if (toggleBtn && sidebar) {
    toggleBtn.addEventListener('click', () => {
      sidebar.classList.toggle('sidebar-open');
    });
  }

  // Logout button
  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', () => {
      Auth.clearSession();
      showToast('Signed out successfully', 'info');
      setTimeout(() => {
        window.location.href = 'login.html';
      }, 500);
    });
  }

  // Refresh button
  const refreshBtn = document.getElementById('adminRefreshBtn');
  if (refreshBtn) {
    refreshBtn.addEventListener('click', () => {
      loadDashboardData();
      showToast('Dashboard data refreshed', 'info');
    });
  }

  // Search and Filters
  const searchInput = document.getElementById('adminSearchInput');
  const categoryFilter = document.getElementById('adminCategoryFilter');
  const priorityFilter = document.getElementById('adminPriorityFilter');

  const triggerFilter = () => {
    const searchVal = searchInput.value.toLowerCase().trim();
    const catVal = categoryFilter.value;
    const priVal = priorityFilter.value;

    let filtered = [...adminNotices];
    if (searchVal) {
      filtered = filtered.filter(n => 
        (n.title && n.title.toLowerCase().includes(searchVal)) ||
        (n.description && n.description.toLowerCase().includes(searchVal)) ||
        (n.department && n.department.toLowerCase().includes(searchVal))
      );
    }
    if (catVal) {
      filtered = filtered.filter(n => n.category === catVal);
    }
    if (priVal) {
      filtered = filtered.filter(n => n.priority === priVal);
    }

    renderTable(filtered);
  };

  searchInput.addEventListener('input', triggerFilter);
  categoryFilter.addEventListener('change', triggerFilter);
  priorityFilter.addEventListener('change', triggerFilter);

  // Modal Open/Close Controls
  setupModalControls();

  // File dropzone
  setupFileDropzone();

  // Notice Form Submit (Create / Edit)
  const noticeForm = document.getElementById('noticeForm');
  noticeForm.addEventListener('submit', handleNoticeFormSubmit);

  // Delete Confirm Button
  const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
  confirmDeleteBtn.addEventListener('click', executeDeleteNotice);
}

function setupModalControls() {
  const formModal = document.getElementById('noticeFormModal');
  const openCreateBtn = document.getElementById('openCreateModalBtn');
  const closeFormBtn = document.getElementById('closeFormModalBtn');
  const cancelFormBtn = document.getElementById('cancelFormModalBtn');

  openCreateBtn.addEventListener('click', () => {
    resetNoticeForm();
    document.getElementById('formModalTitle').innerText = 'Post New College Notice';
    document.getElementById('saveNoticeSubmitBtn').innerText = 'Publish Notice ➔';
    formModal.classList.add('active');
  });

  const closeForm = () => formModal.classList.remove('active');
  closeFormBtn.addEventListener('click', closeForm);
  cancelFormBtn.addEventListener('click', closeForm);

  // Delete modal controls
  const deleteModal = document.getElementById('deleteConfirmModal');
  const closeDeleteBtn = document.getElementById('closeDeleteModalBtn');
  const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');

  const closeDelete = () => deleteModal.classList.remove('active');
  closeDeleteBtn.addEventListener('click', closeDelete);
  cancelDeleteBtn.addEventListener('click', closeDelete);

  // View modal controls
  const viewModal = document.getElementById('viewNoticeModal');
  const closeViewBtn = document.getElementById('closeViewModalBtn');
  const closeViewFooterBtn = document.getElementById('closeViewModalFooterBtn');

  const closeView = () => viewModal.classList.remove('active');
  closeViewBtn.addEventListener('click', closeView);
  closeViewFooterBtn.addEventListener('click', closeView);
}

function setupFileDropzone() {
  const dropzone = document.getElementById('fileDropzone');
  const fileInput = document.getElementById('fileInput');
  const previewStrip = document.getElementById('filePreviewStrip');
  const previewName = document.getElementById('previewFileName');
  const removeBtn = document.getElementById('removeFileBtn');

  dropzone.addEventListener('click', () => fileInput.click());

  fileInput.addEventListener('change', async () => {
    const file = fileInput.files[0];
    if (!file) return;

    previewName.innerText = file.name;
    previewStrip.style.display = 'flex';

    // If server is active, upload immediately
    try {
      const formData = new FormData();
      formData.append('file', file);

      const res = await apiRequest('/files/upload', {
        method: 'POST',
        body: formData
      });

      if (res.success && res.data) {
        document.getElementById('formAttachmentUrl').value = res.data.fileUrl;
        document.getElementById('formAttachmentFileName').value = res.data.originalFileName;
        document.getElementById('formAttachmentFileType').value = res.data.fileType;
        showToast('Attachment uploaded successfully', 'success');
      }
    } catch (err) {
      console.warn('Backend file upload offline. Simulating local attachment path.', err);
      document.getElementById('formAttachmentUrl').value = 'uploads/' + file.name;
      document.getElementById('formAttachmentFileName').value = file.name;
      document.getElementById('formAttachmentFileType').value = file.type || 'application/pdf';
      showToast('Attachment attached locally', 'info');
    }
  });

  removeBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    fileInput.value = '';
    document.getElementById('formAttachmentUrl').value = '';
    document.getElementById('formAttachmentFileName').value = '';
    document.getElementById('formAttachmentFileType').value = '';
    previewStrip.style.display = 'none';
  });
}

function resetNoticeForm() {
  document.getElementById('noticeForm').reset();
  document.getElementById('noticeFormId').value = '';
  document.getElementById('formDate').value = new Date().toISOString().split('T')[0];
  document.getElementById('formPublished').checked = true;
  document.getElementById('formAttachmentUrl').value = '';
  document.getElementById('formAttachmentFileName').value = '';
  document.getElementById('formAttachmentFileType').value = '';
  document.getElementById('filePreviewStrip').style.display = 'none';
}

async function loadDashboardData() {
  try {
    const [statsRes, noticesRes] = await Promise.all([
      apiRequest('/admin/stats'),
      apiRequest('/admin/notices')
    ]);

    if (statsRes.success && statsRes.data) {
      updateStatBoxes(statsRes.data);
    }
    if (noticesRes.success && Array.isArray(noticesRes.data)) {
      adminNotices = noticesRes.data;
      renderTable(adminNotices);
    }
  } catch (err) {
    console.warn('Backend API offline. Using fallback local repository.', err);
    loadFallbackData();
  }
}

function loadFallbackData() {
  const stored = localStorage.getItem('kec_local_notices');
  if (stored) {
    adminNotices = JSON.parse(stored);
  } else {
    adminNotices = FALLBACK_NOTICES;
    localStorage.setItem('kec_local_notices', JSON.stringify(FALLBACK_NOTICES));
  }

  const total = adminNotices.length;
  const published = adminNotices.filter(n => n.published).length;
  const draft = total - published;
  const urgent = adminNotices.filter(n => n.priority === 'URGENT').length;

  updateStatBoxes({ totalNotices: total, publishedNotices: published, draftNotices: draft, urgentNotices: urgent });
  renderTable(adminNotices);
}

function updateStatBoxes(stats) {
  document.getElementById('statTotalNotices').innerText = stats.totalNotices;
  document.getElementById('statPublishedNotices').innerText = stats.publishedNotices;
  document.getElementById('statDraftNotices').innerText = stats.draftNotices;
  document.getElementById('statUrgentNotices').innerText = stats.urgentNotices;
}

function renderTable(notices) {
  const tbody = document.getElementById('adminNoticesTableBody');
  if (!tbody) return;

  if (notices.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="9" style="text-align: center; padding: 3rem;">
          <div class="empty-state-icon" style="font-size: 2rem;">📋</div>
          <h4 style="margin: 0.5rem 0 0.2rem; color: var(--primary-navy);">No notices found</h4>
          <p style="color: var(--text-muted); font-size: 0.85rem;">Click "Post New Notice" to create an announcement.</p>
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = notices.map((notice, index) => {
    const catBadge = getCategoryBadge(notice.category);
    const priBadge = getPriorityBadge(notice.priority);
    const isPub = notice.published;

    return `
      <tr>
        <td style="color: var(--text-muted); font-weight: 600;">${index + 1}</td>
        <td>
          <div class="table-notice-title" title="${escapeHtml(notice.title)}">
            ${escapeHtml(notice.title)}
          </div>
          <span style="font-size: 0.75rem; color: var(--text-muted);">By: ${escapeHtml(notice.postedBy || 'Principal')}</span>
        </td>
        <td><span class="badge ${catBadge.class}">${catBadge.icon} ${catBadge.label}</span></td>
        <td><span class="badge ${priBadge.class}">${priBadge.label}</span></td>
        <td style="white-space: nowrap;">${formatDate(notice.noticeDate)}</td>
        <td>${escapeHtml(notice.department || 'All')}</td>
        <td>
          <span class="status-badge ${isPub ? 'status-published' : 'status-draft'}">
            ${isPub ? '● Published' : '○ Draft'}
          </span>
        </td>
        <td>
          ${notice.attachmentUrl 
            ? `<a href="${notice.attachmentUrl}" target="_blank" style="font-weight: 600; font-size: 0.8rem; color: var(--secondary-blue);">📎 Yes</a>` 
            : '<span style="color: #94a3b8;">None</span>'}
        </td>
        <td style="text-align: right;">
          <div class="action-buttons" style="justify-content: flex-end;">
            <button class="action-btn" title="View Notice" onclick="viewNotice(${notice.id})">👁️</button>
            <button class="action-btn" title="${isPub ? 'Unpublish to Draft' : 'Publish Live'}" onclick="togglePublish(${notice.id})">
              ${isPub ? '⏸️' : '▶️'}
            </button>
            <button class="action-btn" title="Edit Notice" onclick="openEditNoticeModal(${notice.id})">✏️</button>
            <button class="action-btn delete-btn" title="Delete Notice" onclick="promptDeleteNotice(${notice.id})">🗑️</button>
          </div>
        </td>
      </tr>
    `;
  }).join('');
}

async function handleNoticeFormSubmit(e) {
  e.preventDefault();

  const id = document.getElementById('noticeFormId').value;
  const title = document.getElementById('formTitle').value.trim();
  const category = document.getElementById('formCategory').value;
  const priority = document.getElementById('formPriority').value;
  const department = document.getElementById('formDept').value.trim();
  const noticeDate = document.getElementById('formDate').value;
  const postedBy = document.getElementById('formPostedBy').value.trim();
  const description = document.getElementById('formDescription').value.trim();
  const published = document.getElementById('formPublished').checked;

  const attachmentUrl = document.getElementById('formAttachmentUrl').value || null;
  const attachmentFileName = document.getElementById('formAttachmentFileName').value || null;
  const attachmentFileType = document.getElementById('formAttachmentFileType').value || null;

  const payload = {
    title,
    category,
    priority,
    department,
    noticeDate,
    postedBy,
    description,
    published,
    attachmentUrl,
    attachmentFileName,
    attachmentFileType
  };

  const submitBtn = document.getElementById('saveNoticeSubmitBtn');
  submitBtn.disabled = true;
  submitBtn.innerText = 'Saving... ⏳';

  try {
    let res;
    if (id) {
      // Update
      res = await apiRequest(`/admin/notices/${id}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      });
      showToast('Notice updated successfully', 'success');
    } else {
      // Create
      res = await apiRequest('/admin/notices', {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      showToast('Notice published successfully', 'success');
    }

    document.getElementById('noticeFormModal').classList.remove('active');
    loadDashboardData();
  } catch (err) {
    console.warn('Backend API save failed. Persisting locally in localStorage.', err);

    if (id) {
      const idx = adminNotices.findIndex(n => n.id == id);
      if (idx !== -1) {
        adminNotices[idx] = { ...adminNotices[idx], ...payload, id: Number(id) };
      }
      showToast('Notice updated locally', 'success');
    } else {
      const newNotice = {
        ...payload,
        id: Date.now(),
        createdAt: new Date().toISOString()
      };
      adminNotices.unshift(newNotice);
      showToast('Notice created locally', 'success');
    }

    localStorage.setItem('kec_local_notices', JSON.stringify(adminNotices));
    document.getElementById('noticeFormModal').classList.remove('active');
    loadFallbackData();
  } finally {
    submitBtn.disabled = false;
  }
}

function openEditNoticeModal(id) {
  const notice = adminNotices.find(n => n.id == id);
  if (!notice) return;

  document.getElementById('formModalTitle').innerText = 'Edit College Notice';
  document.getElementById('saveNoticeSubmitBtn').innerText = 'Update Notice ➔';

  document.getElementById('noticeFormId').value = notice.id;
  document.getElementById('formTitle').value = notice.title;
  document.getElementById('formCategory').value = notice.category;
  document.getElementById('formPriority').value = notice.priority;
  document.getElementById('formDept').value = notice.department || 'All Departments';
  document.getElementById('formDate').value = notice.noticeDate;
  document.getElementById('formPostedBy').value = notice.postedBy || 'Principal Office';
  document.getElementById('formDescription').value = notice.description;
  document.getElementById('formPublished').checked = notice.published;

  if (notice.attachmentUrl) {
    document.getElementById('formAttachmentUrl').value = notice.attachmentUrl;
    document.getElementById('formAttachmentFileName').value = notice.attachmentFileName || 'file.pdf';
    document.getElementById('previewFileName').innerText = notice.attachmentFileName || 'file.pdf';
    document.getElementById('filePreviewStrip').style.display = 'flex';
  } else {
    document.getElementById('formAttachmentUrl').value = '';
    document.getElementById('formAttachmentFileName').value = '';
    document.getElementById('filePreviewStrip').style.display = 'none';
  }

  document.getElementById('noticeFormModal').classList.add('active');
}

async function togglePublish(id) {
  try {
    const res = await apiRequest(`/admin/notices/${id}/publish`, {
      method: 'PATCH'
    });
    if (res.success) {
      showToast(res.message || 'Status updated', 'success');
      loadDashboardData();
    }
  } catch (err) {
    const notice = adminNotices.find(n => n.id == id);
    if (notice) {
      notice.published = !notice.published;
      localStorage.setItem('kec_local_notices', JSON.stringify(adminNotices));
      showToast(notice.published ? 'Notice published live' : 'Notice moved to drafts', 'info');
      loadFallbackData();
    }
  }
}

function promptDeleteNotice(id) {
  const notice = adminNotices.find(n => n.id == id);
  if (!notice) return;

  deleteTargetId = id;
  document.getElementById('deleteNoticeTitlePreview').innerText = `"${notice.title}"`;
  document.getElementById('deleteConfirmModal').classList.add('active');
}

async function executeDeleteNotice() {
  if (!deleteTargetId) return;

  const btn = document.getElementById('confirmDeleteBtn');
  btn.disabled = true;
  btn.innerText = 'Deleting...';

  try {
    const res = await apiRequest(`/admin/notices/${deleteTargetId}`, {
      method: 'DELETE'
    });
    showToast('Notice deleted successfully', 'success');
    document.getElementById('deleteConfirmModal').classList.remove('active');
    loadDashboardData();
  } catch (err) {
    adminNotices = adminNotices.filter(n => n.id != deleteTargetId);
    localStorage.setItem('kec_local_notices', JSON.stringify(adminNotices));
    showToast('Notice deleted locally', 'success');
    document.getElementById('deleteConfirmModal').classList.remove('active');
    loadFallbackData();
  } finally {
    btn.disabled = false;
    btn.innerText = 'Yes, Delete Notice';
    deleteTargetId = null;
  }
}

function viewNotice(id) {
  const notice = adminNotices.find(n => n.id == id);
  if (!notice) return;

  const catBadge = getCategoryBadge(notice.category);
  const priBadge = getPriorityBadge(notice.priority);

  document.getElementById('viewModalTitle').innerText = notice.title;
  document.getElementById('viewModalCategory').innerHTML = `${catBadge.icon} ${catBadge.label}`;
  document.getElementById('viewModalPriority').innerText = priBadge.label;
  document.getElementById('viewModalDate').innerText = formatDate(notice.noticeDate);
  document.getElementById('viewModalDept').innerText = notice.department || 'All Departments';
  document.getElementById('viewModalDesc').innerText = notice.description;
  document.getElementById('viewModalStatusInfo').innerText = `Status: ${notice.published ? 'Published Live' : 'Draft'}`;

  const attachBox = document.getElementById('viewModalAttachmentBox');
  const attachLink = document.getElementById('viewModalAttachmentLink');
  const attachName = document.getElementById('viewModalAttachmentName');

  if (notice.attachmentUrl) {
    attachBox.style.display = 'block';
    attachName.innerText = notice.attachmentFileName || 'Notice_Attachment.pdf';
    attachLink.href = notice.attachmentUrl;
  } else {
    attachBox.style.display = 'none';
  }

  document.getElementById('viewNoticeModal').classList.add('active');
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
