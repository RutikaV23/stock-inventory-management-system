import api from './axios';

export const login = (credentials) => api.post('/auth/login', credentials);

export const logout = (refreshToken) =>
  api.post('/auth/logout', { refreshToken });

export const refreshToken = (token) =>
  api.post('/auth/refresh-token', { refreshToken: token });

export const getProfile = () => api.get('/auth/profile');
