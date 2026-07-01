import { useState, useEffect, useCallback, useMemo, startTransition } from 'react';
import {
  Box,
  Button,
  TextField,
  InputAdornment,
  Paper,
  Snackbar,
  Alert,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  ArrowCircleUp,
  Visibility as ViewIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import PageHeader from '../components/common/PageHeader';
import StockHistoryTable from '../components/stock/StockHistoryTable';
import StockOutDialog from '../components/stock/StockOutDialog';
import ConfirmDialog from '../components/common/ConfirmDialog';
import {
  stockOut,
  updateStockOut,
  deleteStockOut,
  getStockOutHistory,
} from '../api/stockApi';
import { useLanguage } from '../context/LanguageContext';

const StockOut = () => {
  const [rows, setRows] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [loading, setLoading] = useState(true);
  const [saveLoading, setSaveLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editRecord, setEditRecord] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const { t } = useLanguage();

  const totalPages = Math.ceil(total / 10) || 0;

  const columns = useMemo(() => [
    {
      key: 'srNo',
      label: t('Sr. No.'),
      render: (_, index) => page * 10 + index + 1,
    },
    { key: 'productName', label: t('Product Name') },
    { key: 'quantity', label: t('Quantity'), align: 'right' },
    { key: 'currentStock', label: t('Current Stock'), align: 'right' },
    { key: 'reason', label: t('Reason') },
    { key: 'performedBy', label: t('Performed By'), render: (row) => row.performedBy || '-' },
    {
      key: 'stockOutDate',
      label: t('Stock Out Date'),
      render: (row) => {
        if (!row.stockOutDate && !row.createdAt) return '-';
        const date = row.stockOutDate || row.createdAt;
        return new Date(date).toLocaleDateString('en-US', {
          year: 'numeric',
          month: 'short',
          day: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
        });
      },
    },
    {
      key: 'actions',
      label: t('Actions'),
      render: (row) => (
        <Box sx={{ display: 'flex', gap: 0.5 }}>
          <Tooltip title={t('View / Edit')}>
            <IconButton size="small" color="primary" onClick={() => handleOpenEdit(row)}>
              <ViewIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title={t('Delete')}>
            <IconButton size="small" color="error" onClick={() => handleOpenDelete(row)}>
              <DeleteIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Box>
      ),
    },
  ], [page, t]);

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
      const { data } = await getStockOutHistory(params);
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
        message: t('Failed to load stock out history'),
        severity: 'error',
      });
    } finally {
      setLoading(false);
    }
  }, [page, search, t]);

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
    setEditRecord(null);
    setDialogOpen(true);
  };

  const handleOpenEdit = (record) => {
    setEditRecord(record);
    setDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setDialogOpen(false);
    setEditRecord(null);
  };

  const handleSave = async (payload) => {
    setSaveLoading(true);
    try {
      if (editRecord) {
        await updateStockOut(editRecord.id, payload);
      } else {
        await stockOut(payload);
      }
      setSnackbar({
        open: true,
        message: editRecord
          ? t('Stock out record updated successfully')
          : t('Stock removed successfully'),
        severity: 'success',
      });
      handleCloseDialog();
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

  const handleOpenDelete = (record) => {
    setDeleteTarget(record);
  };

  const handleCloseDelete = () => {
    setDeleteTarget(null);
  };

  const handleConfirmDelete = async () => {
    if (!deleteTarget) return;
    setDeleteLoading(true);
    try {
      await deleteStockOut(deleteTarget.id);
      setSnackbar({
        open: true,
        message: t('Stock out record deleted successfully'),
        severity: 'success',
      });
      setDeleteTarget(null);
      fetchHistory();
    } catch (err) {
      const message =
        err.response?.data?.message ||
        (err.message === 'Network Error'
          ? 'Unable to connect to server'
          : 'An unexpected error occurred');
      setSnackbar({ open: true, message, severity: 'error' });
      setDeleteTarget(null);
    } finally {
      setDeleteLoading(false);
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar((prev) => ({ ...prev, open: false }));
  };

  return (
    <>
      <PageHeader
        title={t('Stock Out')}
        subtitle={t('Record outgoing stock')}
        action={
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleOpenAdd}
            sx={{ boxShadow: '0 4px 14px rgba(21,101,192,0.25)' }}
          >
            {t('Issue Stock')}
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
            placeholder={t('Search stock out history...')}
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
            {t('Search')}
          </Button>
          {search && (
            <Button
              variant="outlined"
              size="small"
              color="inherit"
              onClick={handleClearSearch}
            >
              {t('Clear')}
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
        emptyMessage={t('No Stock Out records found.')}
        emptyIcon={ArrowCircleUp}
      />

      <StockOutDialog
        open={dialogOpen}
        onClose={handleCloseDialog}
        onSave={handleSave}
        loading={saveLoading}
        stockOutRecord={editRecord}
      />

      <ConfirmDialog
        open={!!deleteTarget}
        onClose={handleCloseDelete}
        onConfirm={handleConfirmDelete}
        title={t('Delete Stock Out Record')}
        message={
          deleteTarget
            ? `${t('Are you sure you want to delete the stock out record for "')}${deleteTarget.productName}${t('"? This action cannot be undone.')}`
            : ''
        }
        loading={deleteLoading}
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

export default StockOut;
