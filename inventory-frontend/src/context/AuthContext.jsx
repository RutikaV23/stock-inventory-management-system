/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, useEffect } from 'react';
import { login as loginApi, logout as logoutApi, getProfile } from '../api/authApi';

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      Promise.resolve().then(() => setLoading(false));
      return;
    }
    getProfile()
      .then(({ data }) => setUser(data.data))
      .catch(() => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        localStorage.removeItem('role');
      })
      .finally(() => setLoading(false));
  }, []);

  const login = async (email, password) => {
    const { data } = await loginApi({ email, password });
    const { accessToken, refreshToken, user: userData, role } = data.data;
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(userData));
    localStorage.setItem('role', role);
    setUser(userData);
    return data;
  };

  const logout = async () => {
    const storedRefreshToken = localStorage.getItem('refreshToken');
    try {
      if (storedRefreshToken) {
        await logoutApi(storedRefreshToken);
      }
    } catch {
      // Logout errors are silently ignored
    } finally {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      localStorage.removeItem('role');
      setUser(null);
    }
  };

  return (
    <AuthContext.Provider
      value={{ user, login, logout, loading, isAuthenticated: !!user }}
    >
      {children}
    </AuthContext.Provider>
  );
};
