import { useState } from 'react';
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
} from '@mui/material';
import { getProducts } from '../../api/productApi';

const initialForm = {
  product: null,
  quantity: '',
  referenceNumber: '',
  notes: '',
};

const StockInDialog = ({ open, onClose, onSave, loading }) => {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});
  const [products, setProducts] = useState([]);
  const [productsLoading, setProductsLoading] = useState(false);

  const fetchProducts = async () => {
    setProductsLoading(true);
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
    } finally {
      setProductsLoading(false);
    }
  };

  const handleEntered = () => {
    setForm(initialForm);
    setErrors({});
    fetchProducts();
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
    if (!form.referenceNumber.trim()) {
      newErrors.referenceNumber = 'Reference number is required';
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

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      productId: form.product.id,
      quantity: Number(form.quantity),
      referenceNumber: form.referenceNumber.trim(),
      notes: form.notes.trim(),
    };

    onSave(payload);
  };

  return (
    <Dialog
      open={open}
      onClose={loading ? undefined : onClose}
      maxWidth="sm"
      fullWidth
      slotProps={{
        transition: {
          onEntered: handleEntered,
        },
      }}
      PaperProps={{
        sx: { borderRadius: 3 },
      }}
    >
      <DialogTitle sx={{ fontWeight: 600, pb: 1 }}>
        Add Stock
      </DialogTitle>

      <Box component="form" onSubmit={handleSubmit} noValidate>
        <DialogContent sx={{ pt: 1 }}>
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
            isOptionEqualToValue={(option, value) => option.id === value.id}
            noOptionsText="No active products available"
            renderInput={(params) => (
              <TextField
                {...params}
                label="Product"
                required
                error={!!errors.product}
                helperText={errors.product}
                slotProps={{
                  input: {
                    ...params.slotProps?.input,
                    endAdornment: (
                      <>
                        {productsLoading ? <CircularProgress size={20} /> : null}
                        {params.slotProps?.input?.endAdornment}
                      </>
                    ),
                  },
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
            label="Reference Number"
            value={form.referenceNumber}
            onChange={handleChange('referenceNumber')}
            error={!!errors.referenceNumber}
            helperText={errors.referenceNumber}
            required
            sx={{ mb: 2 }}
          />

          <TextField
            fullWidth
            label="Notes"
            value={form.notes}
            onChange={handleChange('notes')}
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
