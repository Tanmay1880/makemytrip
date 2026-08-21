import api from './axiosConfig';

// ==================== LOGIN ====================

export async function login(credentials) {
  const response = await api.post('/api/auth/login', credentials);

  const data = response.data;

  const user = {
    id: data.userId,
    email: data.email,
    role: data.role,
  };

  localStorage.setItem('jwt_token', data.accessToken);
  localStorage.setItem('user_data', JSON.stringify(user));

  // Keep the structure expected by AuthContext/Login
  return {
    token: data.accessToken,
    user,
  };
}

// ==================== LOGOUT ====================

export function logout() {
  localStorage.removeItem('jwt_token');
  localStorage.removeItem('user_data');
}

// ==================== CURRENT USER ====================

export function getCurrentUser() {
  const stored = localStorage.getItem('user_data');

  return stored ? JSON.parse(stored) : null;
}

// ==================== REGISTER ====================
// We'll connect this after login is working.

export async function register(payload) {
  const response = await api.post('/api/users', payload);

  return response.data;
}