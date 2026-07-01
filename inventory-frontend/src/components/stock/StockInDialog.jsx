import { useState, useEffect, useCallback } from 'react';
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
import { useLanguage } from '../../context/LanguageContext';
import { useAuth } from '../../context/AuthContext';
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

  const { t } = useLanguage();
  const { user } = useAuth();

  const fetchProducts = useCallback(async () => {
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
      setFetchError(t('Failed to load products. Please try again.'));
    } finally {
      setProductsLoading(false);
    }
  }, [t]);

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
        const userName = user ? `${user.firstName} ${user.lastName}`.trim() : '';
        setForm({
          ...initialForm,
          performedBy: userName,
        });
      }
      fetchProducts();
    }
  }, [open, stockInRecord, user, fetchProducts]);

  const getProductFromList = (productId) => {
    return products.find((p) => p.id === productId) || null;
  };

  const validate = () => {
    const newErrors = {};
    if (!form.product) {
      newErrors.product = t('Product is required');
    }
    if (form.quantity === '' || form.quantity == null) {
      newErrors.quantity = t('Quantity is required');
    } else if (!Number.isInteger(Number(form.quantity)) || Number(form.quantity) <= 0) {
      newErrors.quantity = t('Must be a positive integer');
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
        {isEdit ? t('Edit Stock In') : t('Add Stock')}
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
            noOptionsText={t('No active products available')}
            renderInput={(params) => (
              <TextField
                {...params}
                label={t('Product')}
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
            label={t('Quantity')}
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
            label={t('Performed By')}
            value={form.performedBy}
            onChange={handleChange('performedBy')}
            onBlur={handleBlur('performedBy')}
            error={!!errors.performedBy}
            helperText={errors.performedBy}
            required
            placeholder={t('Enter person name')}
            sx={{ mb: 2 }}
            slotProps={{ input: { readOnly: true } }}
          />

          <TextField
            fullWidth
            label={t('Notes')}
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
            {t('Cancel')}
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
              t('Update')
            ) : (
              t('Save')
            )}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
};

export default StockInDialog;
