import api from './axiosConfig';

// ============================================================
// LOGIN
// ============================================================

export async function login(credentials) {
  const response = await api.post('/api/auth/login', credentials);

  const data = response.data;

  // Store JWT first so the next API request is authenticated.
  localStorage.setItem('jwt_token', data.accessToken);

  // Fetch complete user information.
  const userResponse = await api.get(`/api/users/${data.userId}`);

  const userData = userResponse.data;

  const user = {
    id: userData.id,
    firstName: userData.firstName,
    lastName: userData.lastName,
    email: userData.email,
    phoneNumber: userData.phoneNumber,
    role: userData.role,
    active: userData.active,
  };

  localStorage.setItem(
    'user_data',
    JSON.stringify(user)
  );

  return {
    token: data.accessToken,
    user,
  };
}

// ============================================================
// LOGOUT
// ============================================================

export function logout() {
  localStorage.removeItem('jwt_token');
  localStorage.removeItem('user_data');
}

// ============================================================
// CURRENT USER
// ============================================================

export function getCurrentUser() {
  const stored = localStorage.getItem('user_data');

  return stored ? JSON.parse(stored) : null;
}

// ============================================================
// REGISTER
// ============================================================

export async function register(payload) {
  const response = await api.post('/api/users', {
    firstName: payload.firstName,
    lastName: payload.lastName,
    email: payload.email,
    phoneNumber: payload.phone,
    password: payload.password,
  });

  return response.data;
}