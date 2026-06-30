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
  MenuItem,
  FormControl,
  InputLabel,
  Select,
} from '@mui/material';
import { toSentenceCase } from '../../utils/sentenceCase';

const initialForm = {
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  phone: '',
  roleName: 'ADMIN',
  status: 'ACTIVE',
};

const ROLES = ['SUPER_ADMIN', 'ADMIN'];
const STATUSES = ['ACTIVE', 'INACTIVE'];

const UserDialog = ({ open, onClose, onSave, user, loading }) => {
  const isEdit = !!user;
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (open) {
      startTransition(() => {
        if (user) {
          setForm({
            firstName: user.firstName || '',
            lastName: user.lastName || '',
            email: user.email || '',
            phone: user.phone || '',
            roleName: user.role || 'ADMIN',
            status: user.status || 'ACTIVE',
            password: '',
          });
        } else {
          setForm(initialForm);
        }
        setErrors({});
      });
    }
  }, [open, user]);

  const validate = () => {
    const newErrors = {};
    if (!form.firstName.trim()) {
      newErrors.firstName = 'First name is required';
    }
    if (!form.lastName.trim()) {
      newErrors.lastName = 'Last name is required';
    }
    if (!form.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email.trim())) {
      newErrors.email = 'Invalid email format';
    }
    if (!isEdit && !form.password.trim()) {
      newErrors.password = 'Password is required';
    }
    if (!form.roleName) {
      newErrors.roleName = 'Role is required';
    }
    if (form.phone.trim() && !/^[0-9]{10}$/.test(form.phone.trim())) {
      newErrors.phone = 'Phone must be exactly 10 digits';
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

  const handlePhoneChange = (e) => {
    const cleaned = e.target.value.replace(/\D/g, '').slice(0, 10);
    setForm((prev) => ({ ...prev, phone: cleaned }));
    if (errors.phone) {
      setErrors((prev) => ({ ...prev, phone: '' }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      firstName: toSentenceCase(form.firstName.trim()),
      lastName: toSentenceCase(form.lastName.trim()),
      phone: form.phone.trim(),
      roleName: form.roleName,
    };

    if (isEdit) {
      payload.status = form.status;
    } else {
      payload.email = form.email.trim();
      payload.password = form.password;
    }

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
        {isEdit ? 'Edit User' : 'Add User'}
      </DialogTitle>

      <Box component="form" onSubmit={handleSubmit} noValidate>
        <DialogContent sx={{ pt: 1 }}>
          <TextField
            fullWidth
            label="First Name"
            value={form.firstName}
            onChange={handleChange('firstName')}
            onBlur={handleBlur('firstName')}
            error={!!errors.firstName}
            helperText={errors.firstName}
            required
            autoFocus
            sx={{ mb: 2 }}
          />

          <TextField
            fullWidth
            label="Last Name"
            value={form.lastName}
            onChange={handleChange('lastName')}
            onBlur={handleBlur('lastName')}
            error={!!errors.lastName}
            helperText={errors.lastName}
            required
            sx={{ mb: 2 }}
          />

          <TextField
            fullWidth
            label="Email"
            value={form.email}
            onChange={handleChange('email')}
            error={!!errors.email}
            helperText={errors.email}
            required
            disabled={isEdit}
            InputProps={{
              readOnly: isEdit,
            }}
            sx={{ mb: 2 }}
          />

          {!isEdit && (
            <TextField
              fullWidth
              label="Password"
              type="password"
              value={form.password}
              onChange={handleChange('password')}
              error={!!errors.password}
              helperText={errors.password}
              required
              sx={{ mb: 2 }}
            />
          )}

          <TextField
            fullWidth
            label="Phone"
            value={form.phone}
            onChange={handlePhoneChange}
            error={!!errors.phone}
            helperText={errors.phone || 'Exactly 10 digits'}
            inputProps={{ maxLength: 10 }}
            sx={{ mb: 2 }}
          />

          <FormControl fullWidth required sx={{ mb: 2 }}>
            <InputLabel>Role</InputLabel>
            <Select
              value={form.roleName}
              label="Role"
              onChange={handleChange('roleName')}
              error={!!errors.roleName}
            >
              {ROLES.map((role) => (
                <MenuItem key={role} value={role}>
                  {role.replace(/_/g, ' ')}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          {isEdit && (
            <FormControl fullWidth required sx={{ mb: 1 }}>
              <InputLabel>Status</InputLabel>
              <Select
                value={form.status}
                label="Status"
                onChange={handleChange('status')}
              >
                {STATUSES.map((s) => (
                  <MenuItem key={s} value={s}>
                    {s}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}
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

export default UserDialog;
