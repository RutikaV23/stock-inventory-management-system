import { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  TextField,
  Button,
  Avatar,
  Grid,
  CircularProgress,
  Alert,
  IconButton,
  InputAdornment,
  Card,
  CardContent,
  Divider,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  Save as SaveIcon,
  Cancel as CancelIcon,
  LockReset,
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import { getProfile, updateProfile, changePassword } from '../api/authApi';
import { toSentenceCase } from '../utils/sentenceCase';

const Profile = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [changingPw, setChangingPw] = useState(false);

  const [form, setForm] = useState({ firstName: '', lastName: '', phone: '' });
  const [formErrors, setFormErrors] = useState({});
  const [pwForm, setPwForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [showPw, setShowPw] = useState({ current: false, new: false, confirm: false });
  const [pwErrors, setPwErrors] = useState({});
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

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
        setError('Failed to load profile');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

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

  const handleSave = async () => {
    const errs = {};
    if (form.phone.trim() && !/^[0-9]{10}$/.test(form.phone.trim())) {
      errs.phone = 'Phone must be exactly 10 digits';
    }
    setFormErrors(errs);
    if (Object.keys(errs).length > 0) return;

    setSaving(true);
    setError('');
    setSuccess('');
    try {
      const payload = {
        firstName: toSentenceCase(form.firstName.trim()),
        lastName: toSentenceCase(form.lastName.trim()),
        phone: form.phone.trim(),
      };
      const { data } = await updateProfile(payload);
      setProfile(data.data);
      setSuccess('Profile updated successfully');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setForm({
      firstName: profile?.firstName || '',
      lastName: profile?.lastName || '',
      phone: profile?.phone || '',
    });
    setFormErrors({});
    setError('');
    setSuccess('');
  };

  const handlePwChange = (field) => (e) => {
    setPwForm((prev) => ({ ...prev, [field]: e.target.value }));
    if (pwErrors[field]) {
      setPwErrors((prev) => ({ ...prev, [field]: '' }));
    }
  };

  const togglePwVisibility = (field) => () => {
    setShowPw((prev) => ({ ...prev, [field]: !prev[field] }));
  };

  const validatePw = () => {
    const errs = {};
    if (!pwForm.currentPassword) errs.currentPassword = 'Current password is required';
    if (!pwForm.newPassword) errs.newPassword = 'New password is required';
    else if (pwForm.newPassword.length < 6)
      errs.newPassword = 'Must be at least 6 characters';
    if (!pwForm.confirmPassword)
      errs.confirmPassword = 'Confirm password is required';
    else if (pwForm.newPassword !== pwForm.confirmPassword)
      errs.confirmPassword = 'Passwords do not match';
    setPwErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleChangePassword = async () => {
    if (!validatePw()) return;
    setChangingPw(true);
    setError('');
    setSuccess('');
    try {
      await changePassword({
        currentPassword: pwForm.currentPassword,
        newPassword: pwForm.newPassword,
      });
      setSuccess('Password changed successfully');
      setPwForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
      setPwErrors({});
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to change password');
    } finally {
      setChangingPw(false);
    }
  };

  const handleResetPw = () => {
    setPwForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
    setPwErrors({});
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  const info = profile || user;

  const readOnlyField = (label, value) => (
    <TextField
      label={label}
      value={value || ''}
      fullWidth
      size="small"
      InputProps={{ readOnly: true }}
      sx={{
        '& .MuiInputBase-root': { bgcolor: 'grey.50' },
      }}
    />
  );

  const formatDate = (dateStr) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <Box sx={{ maxWidth: 720, mx: 'auto' }}>
      {success && (
        <Alert severity="success" sx={{ mb: 3, borderRadius: 2 }} onClose={() => setSuccess('')}>
          {success}
        </Alert>
      )}
      {error && (
        <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      <Card
        elevation={0}
        sx={{ borderRadius: 3, border: '1px solid', borderColor: 'grey.200', mb: 4 }}
      >
        <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <Avatar
              sx={{
                width: 80,
                height: 80,
                bgcolor: 'primary.main',
                fontSize: '1.8rem',
                fontWeight: 700,
                mx: 'auto',
                mb: 1.5,
              }}
            >
              {info?.firstName?.[0]}
              {info?.lastName?.[0]}
            </Avatar>
            <Typography variant="h5" fontWeight={600}>
              {info?.firstName} {info?.lastName}
            </Typography>
            <Typography variant="body2" color="text.secondary" fontWeight={500}>
              {info?.role?.replace(/_/g, ' ')}
            </Typography>
          </Box>

          <Divider sx={{ mb: 3 }} />

          <Typography variant="h6" fontWeight={600} sx={{ mb: 3 }}>
            Profile Information
          </Typography>

          <Grid container spacing={2.5}>
            <Grid item xs={12} sm={6}>
              <TextField
                label="First Name"
                value={form.firstName}
                onChange={handleChange('firstName')}
                onBlur={handleBlur('firstName')}
                fullWidth
                size="small"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Last Name"
                value={form.lastName}
                onChange={handleChange('lastName')}
                onBlur={handleBlur('lastName')}
                fullWidth
                size="small"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Email"
                value={info?.email || ''}
                fullWidth
                size="small"
                InputProps={{ readOnly: true }}
                sx={{ '& .MuiInputBase-root': { bgcolor: 'grey.50' } }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Phone Number"
                value={form.phone}
                onChange={handlePhoneChange}
                error={!!formErrors.phone}
                helperText={formErrors.phone}
                fullWidth
                size="small"
                inputProps={{ maxLength: 10 }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              {readOnlyField('Role', info?.role?.replace(/_/g, ' '))}
            </Grid>
            <Grid item xs={12} sm={6}>
              {readOnlyField('Status', info?.status)}
            </Grid>
            <Grid item xs={12} sm={6}>
              {readOnlyField('Last Login', formatDate(info?.lastLoginAt))}
            </Grid>
            <Grid item xs={12} sm={6}>
              {readOnlyField('Account Created', formatDate(info?.createdAt))}
            </Grid>
          </Grid>

          <Box sx={{ display: 'flex', gap: 2, mt: 4, justifyContent: 'flex-end' }}>
            <Button
              variant="outlined"
              startIcon={<CancelIcon />}
              onClick={handleCancel}
              disabled={saving}
            >
              Cancel
            </Button>
            <Button
              variant="contained"
              startIcon={saving ? <CircularProgress size={18} /> : <SaveIcon />}
              onClick={handleSave}
              disabled={saving}
            >
              {saving ? 'Saving...' : 'Save Changes'}
            </Button>
          </Box>
        </CardContent>
      </Card>

      <Card
        elevation={0}
        sx={{ borderRadius: 3, border: '1px solid', borderColor: 'grey.200' }}
      >
        <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
          <Typography variant="h6" fontWeight={600} sx={{ mb: 3 }}>
            Change Password
          </Typography>

          <Grid container spacing={2.5}>
            <Grid item xs={12}>
              <TextField
                label="Current Password"
                type={showPw.current ? 'text' : 'password'}
                value={pwForm.currentPassword}
                onChange={handlePwChange('currentPassword')}
                error={!!pwErrors.currentPassword}
                helperText={pwErrors.currentPassword}
                fullWidth
                size="small"
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={togglePwVisibility('current')}
                        edge="end"
                        size="small"
                      >
                        {showPw.current ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="New Password"
                type={showPw.new ? 'text' : 'password'}
                value={pwForm.newPassword}
                onChange={handlePwChange('newPassword')}
                error={!!pwErrors.newPassword}
                helperText={pwErrors.newPassword}
                fullWidth
                size="small"
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={togglePwVisibility('new')}
                        edge="end"
                        size="small"
                      >
                        {showPw.new ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Confirm New Password"
                type={showPw.confirm ? 'text' : 'password'}
                value={pwForm.confirmPassword}
                onChange={handlePwChange('confirmPassword')}
                error={!!pwErrors.confirmPassword}
                helperText={pwErrors.confirmPassword}
                fullWidth
                size="small"
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={togglePwVisibility('confirm')}
                        edge="end"
                        size="small"
                      >
                        {showPw.confirm ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
            </Grid>
          </Grid>

          <Box sx={{ display: 'flex', gap: 2, mt: 4, justifyContent: 'flex-end' }}>
            <Button
              variant="outlined"
              startIcon={<CancelIcon />}
              onClick={handleResetPw}
              disabled={changingPw}
            >
              Reset
            </Button>
            <Button
              variant="contained"
              startIcon={changingPw ? <CircularProgress size={18} /> : <LockReset />}
              onClick={handleChangePassword}
              disabled={changingPw}
            >
              {changingPw ? 'Changing...' : 'Change Password'}
            </Button>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};

export default Profile;
