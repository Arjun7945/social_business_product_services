import { IProductImage, NewProductImage } from './product-image.model';

export const sampleWithRequiredData: IProductImage = {
  id: 9781,
  imageUrl: 'before zealous',
  displayOrder: 7426,
};

export const sampleWithPartialData: IProductImage = {
  id: 16088,
  imageUrl: 'vivacious into',
  displayOrder: 14482,
};

export const sampleWithFullData: IProductImage = {
  id: 20134,
  imageUrl: 'scamper usually since',
  displayOrder: 31969,
};

export const sampleWithNewData: NewProductImage = {
  imageUrl: 'taut',
  displayOrder: 8411,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
