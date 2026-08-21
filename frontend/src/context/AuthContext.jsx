// ============================================================
// AUTH CONTEXT
// ============================================================

import {
  createContext,
  useContext,
  useState,
  useCallback,
  useEffect,
} from 'react';

import * as authApi from '@/api/authApi';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const stored = localStorage.getItem('user_data');

    if (stored) {
      try {
        setUser(JSON.parse(stored));
      } catch {
        localStorage.removeItem('user_data');
        localStorage.removeItem('jwt_token');
      }
    }

    setLoading(false);
  }, []);

  const login = useCallback(async (credentials) => {
    const result = await authApi.login(credentials);

    setUser(result.user);

    return result;
  }, []);

  const register = useCallback(async (payload) => {
    // Create the account
    const registeredUser = await authApi.register(payload);

    // Automatically log the newly created user in
    const loginResult = await authApi.login({
      email: payload.email,
      password: payload.password,
    });

    // Combine login information with registration information
    const user = {
      ...loginResult.user,
      firstName: registeredUser.firstName,
      lastName: registeredUser.lastName,
      phoneNumber: registeredUser.phoneNumber,
    };

    // Store complete user information
    localStorage.setItem(
      'user_data',
      JSON.stringify(user)
    );

    setUser(user);

    return {
      ...loginResult,
      user,
    };
  }, []);

  const logout = useCallback(async () => {
    await authApi.logout();
    setUser(null);
  }, []);

  const isAdmin = user?.role === 'ADMIN';

  const value = {
    user,
    loading,
    isAuthenticated: !!user,
    isAdmin,
    login,
    register,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);

  if (!ctx) {
    throw new Error(
      'useAuth must be used within AuthProvider'
    );
  }

  return ctx;
}