import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  TextField,
  Button,
  CircularProgress,
  Snackbar,
  Alert,
  Card,
  CardContent,
  Typography,
} from '@mui/material';
import { Save as SaveIcon, ArrowBack as ArrowBackIcon } from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import { getProfile, updateProfile } from '../api/authApi';
import { toSentenceCase } from '../utils/sentenceCase';

const ProfileEdit = () => {
  const { user } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({ firstName: '', lastName: '', phone: '' });
  const [formErrors, setFormErrors] = useState({});
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success',
  });

  useEffect(() => {
    const load = async () => {
      try {
        const { data } = await getProfile();
        const u = data.data;
        setProfile(u);
        setForm({
          firstName: u.firstName || '',
          lastName: u.lastName || '',
          phone: u.phone || '',
        });
      } catch {
        setSnackbar({ open: true, message: t('Failed to load profile'), severity: 'error' });
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [t]);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
    if (formErrors[field]) {
      setFormErrors((prev) => ({ ...prev, [field]: '' }));
    }
  };

  const handleBlur = (field) => () => {
    setForm((prev) => ({ ...prev, [field]: toSentenceCase(prev[field]) }));
  };

  const handlePhoneChange = (e) => {
    const cleaned = e.target.value.replace(/\D/g, '').slice(0, 10);
    setForm((prev) => ({ ...prev, phone: cleaned }));
    if (formErrors.phone) {
      setFormErrors((prev) => ({ ...prev, phone: '' }));
    }
  };

  const validate = () => {
    const errs = {};
    if (!form.firstName.trim()) {
      errs.firstName = t('First name is required');
    }
    if (!form.lastName.trim()) {
      errs.lastName = t('Last name is required');
    }
    if (form.phone.trim() && !/^[0-9]{10}$/.test(form.phone.trim())) {
      errs.phone = t('Phone must be exactly 10 digits');
    }
    setFormErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSave = async () => {
    if (!validate()) return;

    setSaving(true);
    try {
      const payload = {
        firstName: toSentenceCase(form.firstName.trim()),
        lastName: toSentenceCase(form.lastName.trim()),
        phone: form.phone.trim(),
      };
      await updateProfile(payload);
      navigate('/profile', {
        state: { success: t('Profile updated successfully.') },
        replace: true,
      });
    } catch (err) {
      const message =
        err.response?.data?.message ||
        (err.message === 'Network Error'
          ? t('Unable to connect to server')
          : t('An unexpected error occurred'));
      setSnackbar({ open: true, message, severity: 'error' });
    } finally {
      setSaving(false);
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar((prev) => ({ ...prev, open: false }));
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  const info = profile || user;

  return (
    <Box sx={{ maxWidth: 720, mx: 'auto', mt: 2 }}>
      <Card
        elevation={0}
        sx={{ borderRadius: 3, border: '1px solid', borderColor: 'divider' }}
      >
        <CardContent sx={{ px: { xs: 3, sm: 4.5 }, py: { xs: 3, sm: 4.5 } }}>
          <Typography variant="h6" fontWeight={600} sx={{ mb: 0.5 }}>
            {t('Edit Profile')}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
            {t('Update your profile information.')}
          </Typography>

          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
            <Box sx={{ display: 'flex', gap: 2.5, flexWrap: 'wrap' }}>
              <TextField
                label={t('First Name')}
                value={form.firstName}
                onChange={handleChange('firstName')}
                onBlur={handleBlur('firstName')}
                error={!!formErrors.firstName}
                helperText={formErrors.firstName}
                size="small"
                sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 10px)' } }}
              />
              <TextField
                label={t('Last Name')}
                value={form.lastName}
                onChange={handleChange('lastName')}
                onBlur={handleBlur('lastName')}
                error={!!formErrors.lastName}
                helperText={formErrors.lastName}
                size="small"
                sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 10px)' } }}
              />
            </Box>
            <TextField
              label={t('Email')}
              value={info?.email || ''}
              size="small"
              InputProps={{ readOnly: true }}
              sx={{ '& .MuiInputBase-root': { bgcolor: 'action.hover' } }}
            />
            <TextField
              label={t('Phone Number')}
              value={form.phone}
              onChange={handlePhoneChange}
              error={!!formErrors.phone}
              helperText={formErrors.phone}
              size="small"
              inputProps={{ maxLength: 10 }}
              sx={{ maxWidth: 340 }}
            />
          </Box>

          <Box sx={{ display: 'flex', gap: 2, mt: 4.5, justifyContent: 'flex-end' }}>
            <Button
              variant="outlined"
              startIcon={<ArrowBackIcon />}
              onClick={() => navigate('/profile')}
              disabled={saving}
            >
              {t('Cancel')}
            </Button>
            <Button
              variant="contained"
              startIcon={saving ? <CircularProgress size={18} color="inherit" /> : <SaveIcon />}
              onClick={handleSave}
              disabled={saving}
              sx={{ boxShadow: '0 4px 14px rgba(21,101,192,0.25)' }}
            >
              {saving ? t('Saving...') : t('Save Changes')}
            </Button>
          </Box>
        </CardContent>
      </Card>

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
    </Box>
  );
};

export default ProfileEdit;
