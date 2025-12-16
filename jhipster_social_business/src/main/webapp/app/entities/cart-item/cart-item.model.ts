import dayjs from 'dayjs/esm';
import { IFishProduct } from 'app/entities/fish-product/fish-product.model';
import { IShoppingCart } from 'app/entities/shopping-cart/shopping-cart.model';

export interface ICartItem {
  id: number;
  quantityKg?: number | null;
  addedAt?: dayjs.Dayjs | null;
  product?: Pick<IFishProduct, 'id' | 'name'> | null;
  cart?: Pick<IShoppingCart, 'id'> | null;
}

export type NewCartItem = Omit<ICartItem, 'id'> & { id: null };
