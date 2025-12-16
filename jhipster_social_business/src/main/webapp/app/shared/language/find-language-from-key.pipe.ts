import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'findLanguageFromKey',
})
export default class FindLanguageFromKeyPipe implements PipeTransform {
  private readonly languages: Record<string, { name: string; rtl?: boolean }> = {
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

  transform(lang: string): string {
    return this.languages[lang].name;
  }

  isRTL(lang: string): boolean {
    return Boolean(this.languages[lang].rtl);
  }
}
