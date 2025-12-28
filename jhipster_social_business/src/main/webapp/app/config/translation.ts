import { Storage, TranslatorContext } from 'react-jhipster';

import { setLocale } from 'app/shared/reducers/locale';

TranslatorContext.setDefaultLocale('en');
TranslatorContext.setRenderInnerTextForMissingKeys(false);

export const languages: any = {
  en: { name: 'English' },
  'ar-ly': { name: 'العربية', rtl: true },
  nl: { name: 'Nederlands' },
  fr: { name: 'Français' },
  de: { name: 'Deutsch' },
  el: { name: 'Ελληνικά' },
  hi: { name: 'हिंदी' },
  mr: { name: 'मराठी' },
  pa: { name: 'ਪੰਜਾਬੀ' },
  ru: { name: 'Русский' },
  es: { name: 'Español' },
  ta: { name: 'தமிழ்' },
  te: { name: 'తెలుగు' },
  // jhipster-needle-i18n-language-key-pipe - JHipster will add/remove languages in this object
};

export const locales = Object.keys(languages).sort();

export const isRTL = (lang: string): boolean => languages[lang] && languages[lang].rtl;

export const setTextDirection = (lang: string) => {
  document.querySelector('html').setAttribute('dir', isRTL(lang) ? 'rtl' : 'ltr');
};

export const registerLocale = store => {
  store.dispatch(setLocale(Storage.session.get('locale', 'en')));
};
