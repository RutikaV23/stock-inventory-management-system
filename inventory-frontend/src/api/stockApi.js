import api from './axios';

export const stockIn = (data) => api.post('/stock/in', data);

export const stockOut = (data) => api.post('/stock/out', data);

export const updateStockIn = (id, data) => api.put(`/stock/in/${id}`, data);

export const updateStockOut = (id, data) => api.put(`/stock/out/${id}`, data);

export const deleteStockIn = (id) => api.delete(`/stock/in/${id}`);

export const deleteStockOut = (id) => api.delete(`/stock/out/${id}`);

export const getStockInHistory = (params) => api.get('/stock/in/history', { params });

export const getStockOutHistory = (params) => api.get('/stock/out/history', { params });
