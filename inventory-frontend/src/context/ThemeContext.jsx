/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, useEffect, useMemo, useCallback } from 'react';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

const ThemeModeContext = createContext(null);

export const useThemeMode = () => {
  const ctx = useContext(ThemeModeContext);
  if (!ctx) throw new Error('useThemeMode must be used within ThemeModeProvider');
  return ctx;
};

const getInitialMode = () => {
  try { return localStorage.getItem('themeMode') || 'light'; } catch { return 'light'; }
};

const commonComponents = {
  MuiButton: {
    styleOverrides: { root: { textTransform: 'none', fontWeight: 600, borderRadius: 8 } },
  },
  MuiTextField: {
    defaultProps: { variant: 'outlined', size: 'medium' },
  },
  MuiDrawer: {
    styleOverrides: { paper: { borderRight: 'none' } },
  },
};

const commonTypography = {
  fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
  h4: { fontWeight: 700 },
  h5: { fontWeight: 600 },
  h6: { fontWeight: 600 },
};

const lightTheme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#1565c0', light: '#1e88e5', dark: '#0d47a1' },
    secondary: { main: '#7b1fa2' },
    background: { default: '#f5f5f5', paper: '#ffffff' },
  },
  typography: commonTypography,
  shape: { borderRadius: 8 },
  components: {
    ...commonComponents,
    MuiPaper: {
      styleOverrides: { root: { boxShadow: '0 2px 12px rgba(0,0,0,0.04)' } },
    },
  },
});

const darkTheme = createTheme({
  palette: {
    mode: 'dark',
    primary: { main: '#1565c0', light: '#1e88e5', dark: '#0d47a1' },
    secondary: { main: '#7b1fa2' },
    background: { default: '#121212', paper: '#1e1e1e' },
  },
  typography: commonTypography,
  shape: { borderRadius: 8 },
  components: {
    ...commonComponents,
    MuiPaper: {
      styleOverrides: { root: { boxShadow: '0 2px 12px rgba(0,0,0,0.2)' } },
    },
  },
});

export const ThemeModeProvider = ({ children }) => {
  const [mode, setModeState] = useState(getInitialMode);
  const [systemDark, setSystemDark] = useState(
    () => window.matchMedia('(prefers-color-scheme: dark)').matches,
  );

  useEffect(() => {
    const mq = window.matchMedia('(prefers-color-scheme: dark)');
    const handler = (e) => setSystemDark(e.matches);
    mq.addEventListener('change', handler);
    return () => mq.removeEventListener('change', handler);
  }, []);

  const effectiveMode = mode === 'system' ? (systemDark ? 'dark' : 'light') : mode;

  const theme = useMemo(
    () => (effectiveMode === 'dark' ? darkTheme : lightTheme),
    [effectiveMode],
  );

  const setMode = useCallback((newMode) => {
    setModeState(newMode);
    try { localStorage.setItem('themeMode', newMode); } catch { /* ignore */ }
  }, []);

  const value = useMemo(() => ({ mode, setMode, effectiveMode }), [mode, setMode, effectiveMode]);

  return (
    <ThemeModeContext.Provider value={value}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </ThemeProvider>
    </ThemeModeContext.Provider>
  );
};
