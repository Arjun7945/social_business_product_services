import { IFishProduct } from 'app/shared/model/fish-product.model';
import { ICustomerOrder } from 'app/shared/model/customer-order.model';

export interface IOrderItem {
  id?: number;
  quantityKg?: number;
  priceAtOrder?: number;
  product?: IFishProduct;
  order?: ICustomerOrder;
}

export const defaultValue: Readonly<IOrderItem> = {};
