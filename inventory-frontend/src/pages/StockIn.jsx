import { useState, useEffect, useCallback, startTransition } from 'react';
import {
  Box,
  Button,
  TextField,
  InputAdornment,
  Paper,
  Snackbar,
  Alert,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  ArrowCircleDown,
} from '@mui/icons-material';
import PageHeader from '../components/common/PageHeader';
import StockHistoryTable from '../components/stock/StockHistoryTable';
import StockInDialog from '../components/stock/StockInDialog';
import { stockIn, getStockInHistory } from '../api/stockApi';

const columns = [
  { key: 'productName', label: 'Product Name' },
  { key: 'quantity', label: 'Quantity', align: 'right' },
  { key: 'currentStock', label: 'Current Stock', align: 'right' },
  { key: 'referenceNumber', label: 'Reference Number' },
  { key: 'notes', label: 'Notes / Remarks' },
  {
    key: 'stockInDate',
    label: 'Stock In Date',
    render: (row) => {
      if (!row.stockInDate && !row.createdAt) return '-';
      const date = row.stockInDate || row.createdAt;
      return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    },
  },
];

const StockIn = () => {
  const [rows, setRows] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [loading, setLoading] = useState(true);
  const [saveLoading, setSaveLoading] = useState(false);

  const [dialogOpen, setDialogOpen] = useState(false);

  const totalPages = Math.ceil(total / 10) || 0;

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success',
  });

  const fetchHistory = useCallback(async () => {
    setLoading(true);
    try {
      const params = { page, size: 10 };
      if (search.trim()) {
        params.keyword = search.trim();
      }
      const { data } = await getStockInHistory(params);
      const responseData = data.data;
      if (responseData.content) {
        setRows(responseData.content);
        setTotal(responseData.totalElements);
      } else if (Array.isArray(responseData)) {
        setRows(responseData);
        setTotal(responseData.length);
      } else {
        setRows([]);
        setTotal(0);
      }
    } catch {
      setSnackbar({
        open: true,
        message: 'Failed to load stock in history',
        severity: 'error',
      });
    } finally {
      setLoading(false);
    }
  }, [page, search]);

  useEffect(() => {
    startTransition(() => {
      fetchHistory();
    });
  }, [fetchHistory]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    setSearch(searchInput.trim());
  };

  const handleClearSearch = () => {
    setSearchInput('');
    setSearch('');
    setPage(0);
  };

  const handleChangePage = (newPage) => {
    setPage(newPage);
  };

  const handleOpenAdd = () => {
    setDialogOpen(true);
  };

  const handleCloseAdd = () => {
    setDialogOpen(false);
  };

  const handleSave = async (payload) => {
    setSaveLoading(true);
    try {
      await stockIn(payload);
      setSnackbar({
        open: true,
        message: 'Stock added successfully',
        severity: 'success',
      });
      handleCloseAdd();
      fetchHistory();
    } catch (err) {
      const message =
        err.response?.data?.message ||
        (err.message === 'Network Error'
          ? 'Unable to connect to server'
          : 'An unexpected error occurred');
      setSnackbar({ open: true, message, severity: 'error' });
    } finally {
      setSaveLoading(false);
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar((prev) => ({ ...prev, open: false }));
  };

  return (
    <>
      <PageHeader
        title="Stock In"
        subtitle="Record incoming stock"
        action={
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleOpenAdd}
            sx={{ boxShadow: '0 4px 14px rgba(21,101,192,0.25)' }}
          >
            Add Stock
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
        <Box
          component="form"
          onSubmit={handleSearch}
          sx={{ display: 'flex', gap: 1, flex: 1, minWidth: 280 }}
        >
          <TextField
            size="small"
            placeholder="Search stock in history..."
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
          {search && (
            <Button
              variant="outlined"
              size="small"
              color="inherit"
              onClick={handleClearSearch}
            >
              Clear
            </Button>
          )}
        </Box>
      </Paper>

      <StockHistoryTable
        columns={columns}
        rows={rows}
        page={page}
        totalPages={totalPages}
        loading={loading}
        onPageChange={handleChangePage}
        emptyMessage="No Stock In records found."
        emptyIcon={ArrowCircleDown}
      />

      <StockInDialog
        open={dialogOpen}
        onClose={handleCloseAdd}
        onSave={handleSave}
        loading={saveLoading}
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

export default StockIn;
