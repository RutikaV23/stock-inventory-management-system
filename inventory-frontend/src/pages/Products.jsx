import { useState, useEffect, useCallback, startTransition } from 'react';
import {
  Box,
  Button,
  TextField,
  InputAdornment,
  Paper,
  Typography,
  Snackbar,
  Alert,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  Inventory2Outlined,
} from '@mui/icons-material';
import PageHeader from '../components/common/PageHeader';
import ProductTable from '../components/products/ProductTable';
import ProductFormDialog from '../components/products/ProductFormDialog';
import DeleteConfirmDialog from '../components/products/DeleteConfirmDialog';
import {
  getProducts,
  createProduct,
  updateProduct,
  deleteProduct,
} from '../api/productApi';

const Products = () => {
  const [products, setProducts] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [saveLoading, setSaveLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  const [formOpen, setFormOpen] = useState(false);
  const [editProduct, setEditProduct] = useState(null);

  const [deleteOpen, setDeleteOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const totalPages = Math.ceil(total / 10) || 0;

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success',
  });

  const fetchProducts = useCallback(async () => {
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
      const { data } = await getProducts(params);
      const responseData = data.data;
      if (responseData.content) {
        setProducts(responseData.content);
        setTotal(responseData.totalElements);
      } else if (Array.isArray(responseData)) {
        setProducts(responseData);
        setTotal(responseData.length);
      } else {
        setProducts([]);
        setTotal(0);
      }
    } catch {
      setSnackbar({
        open: true,
        message: 'Failed to load products',
        severity: 'error',
      });
    } finally {
      setLoading(false);
    }
  }, [page, search, statusFilter]);

  useEffect(() => {
    startTransition(() => {
      fetchProducts();
    });
  }, [fetchProducts]);

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

  const handleStatusFilter = (e) => {
    setStatusFilter(e.target.value);
    setPage(0);
  };

  const handleChangePage = (newPage) => {
    setPage(newPage);
  };

  const handleOpenAdd = () => {
    setEditProduct(null);
    setFormOpen(true);
  };

  const handleOpenView = (product) => {
    setEditProduct(product);
    setFormOpen(true);
  };

  const handleCloseForm = () => {
    setFormOpen(false);
    setEditProduct(null);
  };

  const handleSave = async (payload) => {
    setSaveLoading(true);
    try {
      if (editProduct) {
        await updateProduct(editProduct.id, payload);
        setSnackbar({
          open: true,
          message: 'Product updated successfully',
          severity: 'success',
        });
      } else {
        await createProduct(payload);
        setSnackbar({
          open: true,
          message: 'Product created successfully',
          severity: 'success',
        });
      }
      handleCloseForm();
      fetchProducts();
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

  const handleOpenDelete = (product) => {
    setDeleteTarget(product);
    setDeleteOpen(true);
  };

  const handleCloseDelete = () => {
    setDeleteOpen(false);
    setDeleteTarget(null);
  };

  const handleConfirmDelete = async () => {
    if (!deleteTarget) return;
    setDeleteLoading(true);
    try {
      await deleteProduct(deleteTarget.id);
      setSnackbar({
        open: true,
        message: 'Product deleted successfully',
        severity: 'success',
      });
      handleCloseDelete();
      fetchProducts();
    } catch (err) {
      const message =
        err.response?.data?.message ||
        (err.message === 'Network Error'
          ? 'Unable to connect to server'
          : 'An unexpected error occurred');
      setSnackbar({ open: true, message, severity: 'error' });
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
        title="Products"
        subtitle="Manage your product catalog"
        action={
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleOpenAdd}
            sx={{ boxShadow: '0 4px 14px rgba(21,101,192,0.25)' }}
          >
            Add Product
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

      {loading && products.length === 0 ? (
        <ProductTable loading />
      ) : products.length === 0 ? (
        <Paper
          elevation={0}
          sx={{
            p: 6,
            textAlign: 'center',
            borderRadius: 3,
            border: '1px solid',
            borderColor: 'grey.200',
          }}
        >
          <Inventory2Outlined
            sx={{ fontSize: 64, color: 'text.disabled', mb: 2 }}
          />
          <Typography variant="h6" fontWeight={600} color="text.secondary" gutterBottom>
            No products found
          </Typography>
          <Typography variant="body2" color="text.disabled" sx={{ mb: 3 }}>
            {search
              ? 'No products match your search. Try a different search term.'
              : 'Get started by adding your first product.'}
          </Typography>
          {!search && (
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={handleOpenAdd}
            >
              Add Product
            </Button>
          )}
        </Paper>
      ) : (
        <ProductTable
          products={products}
          page={page}
          totalPages={totalPages}
          loading={loading}
          onPageChange={handleChangePage}
          onView={handleOpenView}
          onDelete={handleOpenDelete}
        />
      )}

      <ProductFormDialog
        open={formOpen}
        onClose={handleCloseForm}
        onSave={handleSave}
        product={editProduct}
        loading={saveLoading}
      />

      <DeleteConfirmDialog
        open={deleteOpen}
        onClose={handleCloseDelete}
        onConfirm={handleConfirmDelete}
        productName={deleteTarget?.name}
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

export default Products;
