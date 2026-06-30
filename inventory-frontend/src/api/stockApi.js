import api from './axios';

export const stockIn = (data) => api.post('/stock/in', data);

export const stockOut = (data) => api.post('/stock/out', data);

export const getStockInHistory = (params) => api.get('/stock/in/history', { params });

export const getStockOutHistory = (params) => api.get('/stock/out/history', { params });
