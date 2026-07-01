import { useState, useEffect, useCallback, useMemo, startTransition } from 'react';
import {
  Box,
  Button,
  TextField,
  InputAdornment,
  Paper,
  Snackbar,
  Alert,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  CircularProgress,
} from '@mui/material';
import {
  Search as SearchIcon,
  FileDownload as FileDownloadIcon,
} from '@mui/icons-material';
import PageHeader from '../components/common/PageHeader';
import ReportTable from '../components/reports/ReportTable';
import { getReports, exportReportsToExcel } from '../api/reportApi';

const Reports = () => {
  const [reports, setReports] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [loading, setLoading] = useState(true);
  const [exportLoading, setExportLoading] = useState(false);

  const totalPages = Math.ceil(total / 10) || 0;

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success',
  });

  const fetchReports = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size: 10,
      };
      if (search.trim()) {
        params.keyword = search.trim();
      }
      if (statusFilter) {
        params.status = statusFilter;
      }
      if (dateFrom) {
        params.dateFrom = `${dateFrom}T00:00:00Z`;
      }
      if (dateTo) {
        params.dateTo = `${dateTo}T23:59:59Z`;
      }
      const { data } = await getReports(params);
      const responseData = data.data;
      if (responseData.content) {
        setReports(responseData.content);
        setTotal(responseData.totalElements);
      } else if (Array.isArray(responseData)) {
        setReports(responseData);
        setTotal(responseData.length);
      } else {
        setReports([]);
        setTotal(0);
      }
      setSnackbar({
        open: true,
        message: 'Report loaded successfully',
        severity: 'success',
      });
    } catch {
      setSnackbar({
        open: true,
        message: 'Network error',
        severity: 'error',
      });
    } finally {
      setLoading(false);
    }
  }, [page, search, statusFilter, dateFrom, dateTo]);

  useEffect(() => {
    startTransition(() => {
      fetchReports();
    });
  }, [fetchReports]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    setSearch(searchInput.trim());
  };

  const handleReset = () => {
    setSearchInput('');
    setSearch('');
    setStatusFilter('');
    setDateFrom('');
    setDateTo('');
    setPage(0);
  };

  const handleStatusFilter = (e) => {
    setStatusFilter(e.target.value);
    setPage(0);
  };

  const handleChangePage = (newPage) => {
    setPage(newPage);
  };

  const handleExport = async () => {
    setExportLoading(true);
    try {
      const params = {};
      if (search.trim()) {
        params.keyword = search.trim();
      }
      if (statusFilter) {
        params.status = statusFilter;
      }
      if (dateFrom) {
        params.dateFrom = `${dateFrom}T00:00:00Z`;
      }
      if (dateTo) {
        params.dateTo = `${dateTo}T23:59:59Z`;
      }
      const response = await exportReportsToExcel(params);
      const blob = new Blob([response.data], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'inventory-reports.xlsx');
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      setSnackbar({
        open: true,
        message: 'Report exported successfully',
        severity: 'success',
      });
    } catch {
      setSnackbar({
        open: true,
        message: 'Network error',
        severity: 'error',
      });
    } finally {
      setExportLoading(false);
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar((prev) => ({ ...prev, open: false }));
  };

  const columns = useMemo(() => [
    {
      key: 'srNo',
      label: 'Sr. No.',
      width: 60,
      render: (_, index) => page * 10 + index + 1,
    },
    { key: 'productName', label: 'Product Name' },
    { key: 'currentStock', label: 'Current Stock', align: 'right' },
    { key: 'totalStockIn', label: 'Total Stock In', align: 'right' },
    { key: 'totalStockOut', label: 'Total Stock Out', align: 'right' },
    { key: 'availableStock', label: 'Available Stock', align: 'right' },
    { key: 'inventoryValue', label: 'Inventory Value (₹)', align: 'right' },
    { key: 'status', label: 'Status', align: 'center' },
  ], [page]);

  const hasActiveFilters = search || statusFilter || dateFrom || dateTo;

  return (
    <>
      <PageHeader
        title="Reports"
        subtitle="Inventory Reports & Analytics"
        action={
          <Button
            variant="contained"
            startIcon={exportLoading ? <CircularProgress size={18} color="inherit" /> : <FileDownloadIcon />}
            onClick={handleExport}
            disabled={exportLoading}
            sx={{ boxShadow: '0 4px 14px rgba(21,101,192,0.25)' }}
          >
            {exportLoading ? 'Exporting...' : 'Export to Excel'}
          </Button>
        }
      />

      <Paper
        elevation={0}
        sx={{
          p: 2,
          mb: 3,
          borderRadius: 2,
          border: '1px solid',
          borderColor: 'grey.200',
          display: 'flex',
          gap: 2,
          flexWrap: 'wrap',
          alignItems: 'center',
        }}
      >
        <FormControl size="small" sx={{ minWidth: 130 }}>
          <InputLabel>Status</InputLabel>
          <Select
            value={statusFilter}
            label="Status"
            onChange={handleStatusFilter}
          >
            <MenuItem value="">All</MenuItem>
            <MenuItem value="ACTIVE">Active</MenuItem>
            <MenuItem value="INACTIVE">Inactive</MenuItem>
          </Select>
        </FormControl>

        <TextField
          type="date"
          size="small"
          label="From Date"
          value={dateFrom}
          onChange={(e) => { setDateFrom(e.target.value); setPage(0); }}
          slotProps={{ inputLabel: { shrink: true } }}
          sx={{ minWidth: 160 }}
        />

        <TextField
          type="date"
          size="small"
          label="To Date"
          value={dateTo}
          onChange={(e) => { setDateTo(e.target.value); setPage(0); }}
          slotProps={{ inputLabel: { shrink: true } }}
          sx={{ minWidth: 160 }}
        />

        <Box
          component="form"
          onSubmit={handleSearch}
          sx={{ display: 'flex', gap: 1, flex: 1, minWidth: 280 }}
        >
          <TextField
            size="small"
            placeholder="Search by product name..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            sx={{ flex: 1, minWidth: 200 }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon fontSize="small" color="action" />
                </InputAdornment>
              ),
            }}
          />
          <Button type="submit" variant="contained" size="small">
            Search
          </Button>
          {hasActiveFilters && (
            <Button
              variant="outlined"
              size="small"
              color="inherit"
              onClick={handleReset}
            >
              Reset
            </Button>
          )}
        </Box>
      </Paper>

      <ReportTable
        columns={columns}
        rows={reports}
        page={page}
        totalPages={totalPages}
        loading={loading}
        onPageChange={handleChangePage}
        emptyMessage="No reports found."
      />

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
      >
        <Alert
          onClose={handleCloseSnackbar}
          severity={snackbar.severity}
          variant="filled"
          sx={{ width: '100%', borderRadius: 2 }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </>
  );
};

export default Reports;
