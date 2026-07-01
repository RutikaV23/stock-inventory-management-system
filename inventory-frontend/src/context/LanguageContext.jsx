/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, useCallback, useMemo } from 'react';
import translations from '../i18n/translations';

const LanguageContext = createContext(null);

export const useLanguage = () => {
  const ctx = useContext(LanguageContext);
  if (!ctx) throw new Error('useLanguage must be used within LanguageProvider');
  return ctx;
};

const LANGUAGES = {
  en: { label: 'English', nativeLabel: 'English' },
  hi: { label: 'Hindi', nativeLabel: 'हिन्दी' },
  mr: { label: 'Marathi', nativeLabel: 'मराठी' },
};

export { LANGUAGES };

const getInitialLanguage = () => {
  try { return localStorage.getItem('language') || 'en'; } catch { return 'en'; }
};

export const LanguageProvider = ({ children }) => {
  const [language, setLanguageState] = useState(getInitialLanguage);

  const setLanguage = useCallback((lang) => {
    setLanguageState(lang);
    try { localStorage.setItem('language', lang); } catch { /* ignore */ }
  }, []);

  const t = useCallback((key) => {
    const langTranslations = translations[language];
    if (langTranslations && langTranslations[key] !== undefined) {
      return langTranslations[key];
    }
    const fallback = translations.en[key];
    return fallback !== undefined ? fallback : key;
  }, [language]);

  const value = useMemo(() => ({ language, setLanguage, t, LANGUAGES }), [language, setLanguage, t]);

  return (
    <LanguageContext.Provider value={value}>
      {children}
    </LanguageContext.Provider>
  );
};
