import { useState, useEffect, startTransition } from 'react';
import {
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  CircularProgress,
} from '@mui/material';

const initialForm = {
  name: '',
  price: '',
  stockQuantity: '',
  description: '',
};

const ProductFormDialog = ({ open, onClose, onSave, product, loading }) => {
  const isEdit = !!product;
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (open) {
      startTransition(() => {
        if (product) {
          setForm({
            name: product.name || '',
            price: product.price ?? '',
            stockQuantity: product.stockQuantity ?? '',
            description: product.description || '',
          });
        } else {
          setForm(initialForm);
        }
        setErrors({});
      });
    }
  }, [open, product]);

  const validate = () => {
    const newErrors = {};
    if (!form.name.trim()) {
      newErrors.name = 'Product name is required';
    }
    if (form.price === '' || form.price == null) {
      newErrors.price = 'Price is required';
    } else if (Number(form.price) <= 0 || !Number.isFinite(Number(form.price))) {
      newErrors.price = 'Must be greater than 0';
    }
    if (form.stockQuantity === '' || form.stockQuantity == null) {
      newErrors.stockQuantity = 'Stock quantity is required';
    } else if (!Number.isInteger(Number(form.stockQuantity)) || Number(form.stockQuantity) < 0) {
      newErrors.stockQuantity = 'Must be a non-negative integer';
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
      name: form.name.trim(),
      price: Number(form.price),
      stockQuantity: Number(form.stockQuantity),
      description: form.description.trim(),
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
        {isEdit ? 'Edit Product' : 'Add Product'}
      </DialogTitle>

      <Box component="form" onSubmit={handleSubmit} noValidate>
        <DialogContent sx={{ pt: 1 }}>
          <TextField
            fullWidth
            label="Product Name"
            value={form.name}
            onChange={handleChange('name')}
            error={!!errors.name}
            helperText={errors.name}
            required
            autoFocus
            sx={{ mb: 2 }}
          />

          <TextField
            fullWidth
            label="Stock Quantity"
            type="number"
            value={form.stockQuantity}
            onChange={handleChange('stockQuantity')}
            error={!!errors.stockQuantity}
            helperText={errors.stockQuantity}
            required
            inputProps={{ min: '0', step: '1' }}
            sx={{ mb: 2 }}
          />

          <TextField
            fullWidth
            label="Price"
            type="number"
            value={form.price}
            onChange={handleChange('price')}
            error={!!errors.price}
            helperText={errors.price}
            required
            inputProps={{ step: '0.01', min: '0' }}
            sx={{ mb: 2 }}
          />

          <TextField
            fullWidth
            label="Description"
            value={form.description}
            onChange={handleChange('description')}
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
            disabled={loading}
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

export default ProductFormDialog;
