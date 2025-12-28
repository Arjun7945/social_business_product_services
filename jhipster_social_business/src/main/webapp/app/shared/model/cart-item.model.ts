import dayjs from 'dayjs';
import { IFishProduct } from 'app/shared/model/fish-product.model';
import { IShoppingCart } from 'app/shared/model/shopping-cart.model';

export interface ICartItem {
  id?: number;
  quantityKg?: number;
  addedAt?: dayjs.Dayjs | null;
  product?: IFishProduct;
  cart?: IShoppingCart;
}

export const defaultValue: Readonly<ICartItem> = {};
