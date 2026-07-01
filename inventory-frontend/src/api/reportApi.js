import api from './axios';

export const getReports = (params) => api.get('/reports', { params });

export const exportReportsToExcel = (params) =>
  api.get('/reports/export/excel', { params, responseType: 'blob' });
