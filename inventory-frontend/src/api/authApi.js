import api from './axios';

export const login = (credentials) => api.post('/auth/login', credentials);

export const logout = (refreshToken) =>
  api.post('/auth/logout', { refreshToken });

export const refreshToken = (token) =>
  api.post('/auth/refresh-token', { refreshToken: token });

export const getProfile = () => api.get('/auth/profile');

export const updateProfile = (data) => api.put('/auth/profile', data);

export const changePassword = (data) => api.put('/auth/change-password', data);
