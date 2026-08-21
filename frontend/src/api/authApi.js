import api from './axiosConfig';

// ============================================================
// AUTH API
// ------------------------------------------------------------
// NOTE: Adjust the endpoint paths and field names below to
// match your Spring Boot REST API contract.
// ============================================================

/**
 * Login with email and password.
 * Expected response: { token: string, user: { id, firstName, lastName, email, role } }
 * @param {{ email: string, password: string }} credentials
 */
export async function login(credentials) {
  // TODO: Replace with your actual endpoint
  // const response = await api.post('/auth/login', credentials);
  // return response.data;

  // --- Placeholder (remove when backend is connected) ---
  return mockLogin(credentials);
}

/**
 * Register a new user account.
 * @param {{ firstName, lastName, email, phone, password }} payload
 */
export async function register(payload) {
  // TODO: Replace with your actual endpoint
  // const response = await api.post('/auth/register', payload);
  // return response.data;

  // --- Placeholder (remove when backend is connected) ---
  return mockRegister(payload);
}

/**
 * Logout the current user.
 */
export async function logout() {
  // TODO: Replace with your actual endpoint
  // await api.post('/auth/logout');

  // --- Placeholder ---
  localStorage.removeItem('jwt_token');
  localStorage.removeItem('user_data');
  return Promise.resolve();
}

/**
 * Fetch the current authenticated user's profile.
 */
export async function getCurrentUser() {
  // TODO: Replace with your actual endpoint
  // const response = await api.get('/auth/me');
  // return response.data;

  // --- Placeholder ---
  const stored = localStorage.getItem('user_data');
  return stored ? JSON.parse(stored) : null;
}

// ============================================================
// PLACEHOLDER IMPLEMENTATIONS (remove when backend is connected)
// ============================================================

const MOCK_USERS_KEY = 'mmt_mock_users';

function getMockUsers() {
  const raw = localStorage.getItem(MOCK_USERS_KEY);
  return raw ? JSON.parse(raw) : [];
}

function saveMockUsers(users) {
  localStorage.setItem(MOCK_USERS_KEY, JSON.stringify(users));
}

// Seed a default admin account
if (getMockUsers().length === 0) {
  saveMockUsers([
    {
      id: 1,
      firstName: 'Admin',
      lastName: 'User',
      email: 'admin@makemytrip.com',
      phone: '+1-555-0100',
      password: 'admin123',
      role: 'ADMIN',
      status: 'ACTIVE',
    },
  ]);
}

function makeToken(user) {
  // Fake JWT for placeholder only
  return btoa(JSON.stringify({ id: user.id, email: user.email, role: user.role }));
}

async function mockLogin({ email, password }) {
  await delay(600);
  const users = getMockUsers();
  const user = users.find((u) => u.email === email && u.password === password);
  if (!user) throw { response: { status: 401, data: { message: 'Invalid email or password' } } };
  const { password: _pw, ...safeUser } = user;
  const token = makeToken(user);
  localStorage.setItem('jwt_token', token);
  localStorage.setItem('user_data', JSON.stringify(safeUser));
  return { token, user: safeUser };
}

async function mockRegister(payload) {
  await delay(700);
  const users = getMockUsers();
  if (users.some((u) => u.email === payload.email)) {
    throw { response: { status: 409, data: { message: 'Email already registered' } } };
  }
  const newUser = {
    id: Date.now(),
    firstName: payload.firstName,
    lastName: payload.lastName,
    email: payload.email,
    phone: payload.phone,
    password: payload.password,
    role: 'USER',
    status: 'ACTIVE',
  };
  users.push(newUser);
  saveMockUsers(users);
  const { password: _pw, ...safeUser } = newUser;
  const token = makeToken(newUser);
  localStorage.setItem('jwt_token', token);
  localStorage.setItem('user_data', JSON.stringify(safeUser));
  return { token, user: safeUser };
}

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}
