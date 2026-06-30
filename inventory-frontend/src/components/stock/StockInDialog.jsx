import { useState, useEffect } from 'react';
import {
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  CircularProgress,
  Autocomplete,
  Alert,
} from '@mui/material';
import { toSentenceCase } from '../../utils/sentenceCase';
import { getProducts } from '../../api/productApi';

const initialForm = {
  product: null,
  quantity: '',
  performedBy: '',
  notes: '',
};

const StockInDialog = ({ open, onClose, onSave, loading, stockInRecord }) => {
  const isEdit = !!stockInRecord;
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});
  const [products, setProducts] = useState([]);
  const [productsLoading, setProductsLoading] = useState(false);
  const [fetchError, setFetchError] = useState('');

  const fetchProducts = async () => {
    setProductsLoading(true);
    setFetchError('');
    try {
      const { data } = await getProducts({ page: 0, size: 1000, status: 'ACTIVE' });
      const responseData = data.data;
      if (responseData.content) {
        setProducts(responseData.content);
      } else if (Array.isArray(responseData)) {
        setProducts(responseData);
      } else {
        setProducts([]);
      }
    } catch {
      setProducts([]);
      setFetchError('Failed to load products. Please try again.');
    } finally {
      setProductsLoading(false);
    }
  };

  useEffect(() => {
    if (open) {
      setErrors({});
      if (stockInRecord) {
        setForm({
          product: null,
          quantity: stockInRecord.quantity ?? '',
          performedBy: stockInRecord.performedBy || '',
          notes: stockInRecord.notes || '',
        });
      } else {
        setForm(initialForm);
      }
      fetchProducts();
    }
  }, [open, stockInRecord]);

  const getProductFromList = (productId) => {
    return products.find((p) => p.id === productId) || null;
  };

  const validate = () => {
    const newErrors = {};
    if (!form.product) {
      newErrors.product = 'Product is required';
    }
    if (form.quantity === '' || form.quantity == null) {
      newErrors.quantity = 'Quantity is required';
    } else if (!Number.isInteger(Number(form.quantity)) || Number(form.quantity) <= 0) {
      newErrors.quantity = 'Must be a positive integer';
    }
    if (!form.performedBy.trim()) {
      newErrors.performedBy = 'Performed by is required';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: '' }));
    }
  };

  const handleBlur = (field) => () => {
    setForm((prev) => ({ ...prev, [field]: toSentenceCase(prev[field]) }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      productId: form.product.id,
      quantity: Number(form.quantity),
      performedBy: toSentenceCase(form.performedBy.trim()),
      notes: toSentenceCase(form.notes.trim()),
    };

    onSave(payload);
  };

  return (
    <Dialog
      open={open}
      onClose={loading ? undefined : onClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: { borderRadius: 3 },
      }}
    >
      <DialogTitle sx={{ fontWeight: 600, pb: 1 }}>
        {isEdit ? 'Edit Stock In' : 'Add Stock'}
      </DialogTitle>

      <Box component="form" onSubmit={handleSubmit} noValidate>
        <DialogContent sx={{ pt: 1 }}>
          {fetchError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {fetchError}
            </Alert>
          )}
          <Autocomplete
            fullWidth
            options={products}
            loading={productsLoading}
            value={form.product}
            onChange={(_, newValue) => {
              setForm((prev) => ({ ...prev, product: newValue }));
              if (errors.product) {
                setErrors((prev) => ({ ...prev, product: '' }));
              }
            }}
            getOptionLabel={(option) => option.name || ''}
            isOptionEqualToValue={(option, value) => value ? option.id === value.id : false}
            noOptionsText="No active products available"
            renderInput={(params) => (
              <TextField
                {...params}
                label="Product"
                required
                error={!!errors.product}
                helperText={errors.product}
                InputProps={{
                  ...params.InputProps,
                  endAdornment: (
                    <>
                      {productsLoading ? <CircularProgress size={20} /> : null}
                      {params.InputProps?.endAdornment}
                    </>
                  ),
                }}
              />
            )}
            sx={{ mb: 2 }}
          />

          <TextField
            fullWidth
            label="Quantity"
            type="number"
            value={form.quantity}
            onChange={handleChange('quantity')}
            error={!!errors.quantity}
            helperText={errors.quantity}
            required
            inputProps={{ min: '1', step: '1' }}
            sx={{ mb: 2 }}
          />

          <TextField
            fullWidth
            label="Performed By"
            value={form.performedBy}
            onChange={handleChange('performedBy')}
            onBlur={handleBlur('performedBy')}
            error={!!errors.performedBy}
            helperText={errors.performedBy}
            required
            placeholder="Enter person name"
            sx={{ mb: 2 }}
          />

          <TextField
            fullWidth
            label="Notes"
            value={form.notes}
            onChange={handleChange('notes')}
            onBlur={handleBlur('notes')}
            multiline
            rows={3}
            sx={{ mb: 1 }}
          />
        </DialogContent>

        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button
            onClick={onClose}
            disabled={loading}
            variant="outlined"
            color="inherit"
          >
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={loading || !form.product}
            sx={{ minWidth: 100 }}
          >
            {loading ? (
              <CircularProgress size={20} color="inherit" />
            ) : isEdit ? (
              'Update'
            ) : (
              'Save'
            )}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
};

export default StockInDialog;
