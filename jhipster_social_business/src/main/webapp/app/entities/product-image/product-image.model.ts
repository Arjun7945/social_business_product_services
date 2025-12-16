import { IFishProduct } from 'app/entities/fish-product/fish-product.model';

export interface IProductImage {
  id: number;
  imageUrl?: string | null;
  displayOrder?: number | null;
  product?: Pick<IFishProduct, 'id' | 'name'> | null;
}

export type NewProductImage = Omit<IProductImage, 'id'> & { id: null };
