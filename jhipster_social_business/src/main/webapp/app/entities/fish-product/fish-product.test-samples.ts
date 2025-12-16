import dayjs from 'dayjs/esm';

import { IFishProduct, NewFishProduct } from './fish-product.model';

export const sampleWithRequiredData: IFishProduct = {
  id: 28902,
  name: 'ack',
  pricePerKg: 31331.71,
  isAvailable: true,
};

export const sampleWithPartialData: IFishProduct = {
  id: 21022,
  name: 'rudely',
  pricePerKg: 10789.22,
  imageUrl: 'shinny',
  description: 'whenever',
  isAvailable: true,
  createdAt: dayjs('2025-12-14T23:18'),
};

export const sampleWithFullData: IFishProduct = {
  id: 25571,
  name: 'fishery mmm',
  pricePerKg: 2967.41,
  imageUrl: 'exhaust',
  description: 'fluctuate scenario',
  isAvailable: true,
  createdAt: dayjs('2025-12-14T16:40'),
};

export const sampleWithNewData: NewFishProduct = {
  name: 'via',
  pricePerKg: 25293.59,
  isAvailable: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
