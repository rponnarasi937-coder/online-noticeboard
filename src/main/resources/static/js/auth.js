/**
 * KEC ONLINE NOTICE BOARD - ADMIN AUTHENTICATION
 */

document.addEventListener('DOMContentLoaded', () => {
  // If already authenticated, directly redirect to dashboard
  if (Auth.isAuthenticated()) {
    window.location.href = 'admin-dashboard.html';
    return;
  }

  const form = document.getElementById('adminLoginForm');
  const usernameInput = document.getElementById('usernameInput');
  const passwordInput = document.getElementById('passwordInput');
  const submitBtn = document.getElementById('loginSubmitBtn');
  const togglePassBtn = document.getElementById('togglePasswordBtn');
  const fillDemoBtn = document.getElementById('fillDemoBtn');

  // Password visibility toggle
  if (togglePassBtn) {
    togglePassBtn.addEventListener('click', () => {
      const isPassword = passwordInput.type === 'password';
      passwordInput.type = isPassword ? 'text' : 'password';
      togglePassBtn.innerText = isPassword ? '🙈' : '👁️';
    });
  }

  // Auto fill demo credentials button
  if (fillDemoBtn) {
    fillDemoBtn.addEventListener('click', () => {
      usernameInput.value = 'admin';
      passwordInput.value = 'admin123';
      showToast('Demo credentials filled: admin / admin123', 'info');
    });
  }

  // Handle Form Submission
  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();

    if (!username || !password) {
      showToast('Please enter both username and password', 'error');
      return;
    }

    submitBtn.disabled = true;
    submitBtn.innerHTML = 'Verifying Credentials ⏳...';

    try {
      // Attempt login via Spring Boot backend API
      const res = await apiRequest('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password })
      });

      if (res.success && res.token) {
        Auth.setSession(res.token, {
          username: res.username,
          fullName: res.fullName,
          role: res.role
        });

        showToast('Login successful! Redirecting to Dashboard...', 'success');
        setTimeout(() => {
          window.location.href = 'admin-dashboard.html';
        }, 800);
      } else {
        throw new Error(res.message || 'Invalid username or password');
      }
    } catch (err) {
      console.warn('Backend login endpoint error or offline fallback check:', err.message);

      // Fallback for standalone frontend run (Live Server / no Java running)
      if (username === 'admin' && password === 'admin123') {
        const mockToken = 'mock_jwt_token_for_standalone_demo_' + Date.now();
        Auth.setSession(mockToken, {
          username: 'admin',
          fullName: 'KEC Admin Controller',
          role: 'ROLE_ADMIN'
        });

        showToast('Authenticated! Opening Admin Dashboard...', 'success');
        setTimeout(() => {
          window.location.href = 'admin-dashboard.html';
        }, 800);
      } else {
        showToast('Invalid credentials! Use admin / admin123', 'error');
        submitBtn.disabled = false;
        submitBtn.innerHTML = 'Authenticate & Access Dashboard ➔';
      }
    }
  });
});
