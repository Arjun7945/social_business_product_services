import { IFishProduct } from 'app/shared/model/fish-product.model';
import { IReturnedOrder } from 'app/shared/model/returned-order.model';

export interface IReturnedOrderItem {
  id?: number;
  quantity?: number;
  productComment?: string | null;
  product?: IFishProduct;
  returnedOrder?: IReturnedOrder;
}

export const defaultValue: Readonly<IReturnedOrderItem> = {};
