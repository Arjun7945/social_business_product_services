import { IFishProduct } from 'app/entities/fish-product/fish-product.model';
import { ICustomerOrder } from 'app/entities/customer-order/customer-order.model';

export interface IOrderItem {
  id: number;
  quantityKg?: number | null;
  priceAtOrder?: number | null;
  product?: Pick<IFishProduct, 'id' | 'name'> | null;
  order?: Pick<ICustomerOrder, 'id'> | null;
}

export type NewOrderItem = Omit<IOrderItem, 'id'> & { id: null };
