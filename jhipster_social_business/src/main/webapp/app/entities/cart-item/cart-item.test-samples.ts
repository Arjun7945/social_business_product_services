import dayjs from 'dayjs/esm';

import { ICartItem, NewCartItem } from './cart-item.model';

export const sampleWithRequiredData: ICartItem = {
  id: 12545,
  quantityKg: 12389.22,
};

export const sampleWithPartialData: ICartItem = {
  id: 14799,
  quantityKg: 11066.87,
  addedAt: dayjs('2025-12-15T13:25'),
};

export const sampleWithFullData: ICartItem = {
  id: 9768,
  quantityKg: 32076.29,
  addedAt: dayjs('2025-12-15T06:25'),
};

export const sampleWithNewData: NewCartItem = {
  quantityKg: 916,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
