import { useState, useEffect, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  Avatar,
  CircularProgress,
  Snackbar,
  Alert,
  Card,
  CardContent,
  Divider,
  Radio,
  RadioGroup,
  FormControlLabel,
  FormControl,
  Select,
  MenuItem,
  InputLabel,
} from '@mui/material';
import { Edit as EditIcon } from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import { useThemeMode } from '../context/ThemeContext';
import { useLanguage, LANGUAGES } from '../context/LanguageContext';
import { getProfile } from '../api/authApi';

const Profile = () => {
  const { user } = useAuth();
  const { mode, setMode } = useThemeMode();
  const { language, setLanguage, t } = useLanguage();
  const navigate = useNavigate();
  const location = useLocation();
  const appearanceRef = useRef(null);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [snackbar, setSnackbar] = useState(() => {
    if (location.state?.success) {
      return { open: true, message: location.state.success, severity: 'success' };
    }
    return { open: false, message: '', severity: 'success' };
  });

  useEffect(() => {
    if (location.state?.success) {
      window.history.replaceState({}, document.title);
    }
  }, [location.state]);

  useEffect(() => {
    if (location.state?.section === 'appearance' && appearanceRef.current) {
      appearanceRef.current.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }, [location.state]);

  useEffect(() => {
    const load = async () => {
      try {
        const { data } = await getProfile();
        setProfile(data.data);
      } catch {
        setSnackbar({ open: true, message: t('Failed to load profile'), severity: 'error' });
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [t]);

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

  const infoRow = (label, value) => (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', py: 1.5, px: 0.5 }}>
        <Typography variant="body2" color="text.secondary" fontWeight={500}>
          {label}
        </Typography>
        <Typography variant="body1" fontWeight={500}>
          {value || '-'}
        </Typography>
      </Box>
      <Divider />
    </Box>
  );

  const themeOptions = [
    { value: 'light', label: t('Light Theme') },
    { value: 'dark', label: t('Dark Theme') },
    { value: 'system', label: t('System Default') },
  ];

  return (
    <Box sx={{ maxWidth: 720, mx: 'auto', mt: 2, display: 'flex', flexDirection: 'column', gap: 3.5 }}>
      {/* Section 1: Profile Information */}
      <Card
        elevation={0}
        sx={{ borderRadius: 3, border: '1px solid', borderColor: 'divider', position: 'relative' }}
      >
        <Box sx={{ display: 'flex', justifyContent: 'flex-end', pt: 2.5, pr: 2.5 }}>
          <Button
            variant="contained"
            size="small"
            startIcon={<EditIcon />}
            onClick={() => navigate('/profile/edit')}
            sx={{ boxShadow: '0 4px 14px rgba(21,101,192,0.25)' }}
          >
            {t('Edit Profile')}
          </Button>
        </Box>

        <CardContent sx={{ px: { xs: 3, sm: 4.5 }, pb: { xs: 3, sm: 4.5 }, pt: 0 }}>
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <Avatar
              sx={{
                width: 88,
                height: 88,
                bgcolor: 'primary.main',
                fontSize: '2rem',
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
            <Typography variant="body2" color="text.secondary" fontWeight={500} sx={{ textTransform: 'uppercase', letterSpacing: 0.5 }}>
              {info?.role?.replace(/_/g, ' ')}
            </Typography>
          </Box>

          {infoRow(t('First Name'), info?.firstName)}
          {infoRow(t('Last Name'), info?.lastName)}
          {infoRow(t('Email'), info?.email)}
          {infoRow(t('Phone'), info?.phone)}
        </CardContent>
      </Card>

      {/* Section 2: Appearance */}
      <Card
        ref={appearanceRef}
        id="appearance-section"
        elevation={0}
        sx={{ borderRadius: 3, border: '1px solid', borderColor: 'divider' }}
      >
        <CardContent sx={{ px: { xs: 3, sm: 4.5 }, py: { xs: 3, sm: 4.5 } }}>
          <Typography variant="h6" fontWeight={600} sx={{ mb: 0.5 }}>
            {t('Appearance')}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2.5 }}>
            {t('Customize the appearance of your application.')}
          </Typography>

          <FormControl>
            <RadioGroup
              value={mode}
              onChange={(e) => setMode(e.target.value)}
            >
              {themeOptions.map((opt) => (
                <FormControlLabel
                  key={opt.value}
                  value={opt.value}
                  control={<Radio size="small" />}
                  label={
                    <Typography variant="body2">
                      {opt.label}
                    </Typography>
                  }
                  sx={{ mb: 0.5 }}
                />
              ))}
            </RadioGroup>
          </FormControl>
        </CardContent>
      </Card>

      {/* Section 3: Language */}
      <Card
        elevation={0}
        sx={{ borderRadius: 3, border: '1px solid', borderColor: 'divider' }}
      >
        <CardContent sx={{ px: { xs: 3, sm: 4.5 }, py: { xs: 3, sm: 4.5 } }}>
          <Typography variant="h6" fontWeight={600} sx={{ mb: 0.5 }}>
            {t('Language')}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2.5 }}>
            {t('Choose your preferred application language.')}
          </Typography>

          <FormControl size="small" sx={{ minWidth: 240 }}>
            <InputLabel id="language-label">{t('Language')}</InputLabel>
            <Select
              labelId="language-label"
              value={language}
              label={t('Language')}
              onChange={(e) => setLanguage(e.target.value)}
            >
              {Object.entries(LANGUAGES).map(([code, lang]) => (
                <MenuItem key={code} value={code}>
                  {lang.nativeLabel}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
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

export default Profile;
