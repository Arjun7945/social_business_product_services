import dayjs from 'dayjs/esm';

import { IShoppingCart, NewShoppingCart } from './shopping-cart.model';

export const sampleWithRequiredData: IShoppingCart = {
  id: 26783,
  createdAt: dayjs('2025-12-15T07:42'),
};

export const sampleWithPartialData: IShoppingCart = {
  id: 30101,
  createdAt: dayjs('2025-12-15T00:25'),
};

export const sampleWithFullData: IShoppingCart = {
  id: 18836,
  createdAt: dayjs('2025-12-15T02:34'),
  updatedAt: dayjs('2025-12-14T21:22'),
};

export const sampleWithNewData: NewShoppingCart = {
  createdAt: dayjs('2025-12-14T18:49'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
